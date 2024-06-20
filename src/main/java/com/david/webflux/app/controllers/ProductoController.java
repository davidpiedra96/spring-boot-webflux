package com.david.webflux.app.controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import com.david.webflux.app.models.documents.Categoria;
import com.david.webflux.app.models.documents.Producto;
import com.david.webflux.app.models.services.ProductoService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SessionAttributes("producto")
@Controller
public class ProductoController {

	@Autowired
	private ProductoService service;

	@Value("${config.uploads.path}")
	private String path;

	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

	/**
	 * Lista los elementos de la base de datos NoSQL, de forma habitual
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping({ "/listar", "/" })
	public String listar(Model model) {
		Flux<Producto> productos = service.findAllConNombreUpperCase();

		productos.subscribe(prod -> log.info(prod.getNombre()));

		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "Listado de productos");
		return "listar";
	}

	/**
	 * Lista los datos por lotes en buffer
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("listar-datadriver")
	public String listarDataDriver(Model model) {
		Flux<Producto> productos = service.findAllConNombreUpperCase().delayElements(Duration.ofSeconds(1));

		productos.subscribe(prod -> log.info(prod.getNombre()));

		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 2));
		model.addAttribute("titulo", "Listado de productos");
		return "listar";
	}

	/**
	 * Lista los productos en lotes de ha 2
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("listar-full")
	public String listarFull(Model model) {
		Flux<Producto> productos = service.findAllConNombreUpperCase().repeat(5000);

		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 2));
		model.addAttribute("titulo", "Listado de productos");
		return "listar";
	}

	/**
	 * Listar chunked
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("listar-chunked")
	public String listarChunked(Model model) {
		Flux<Producto> productos = service.findAllConNombreUpperCase().repeat(5000);

		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 2));
		model.addAttribute("titulo", "Listado de productos");
		return "listar-chunked";
	}

	/**
	 * Formulario de crear
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/form")
	public Mono<String> crear(Model model) {
		model.addAttribute("producto", new Producto());
		model.addAttribute("titulo", "Formulario de producto");
		return Mono.just("form");
	}

	/**
	 * Controller para editar un producto
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/form/{id}")
	public Mono<String> editar(@PathVariable(name = "id") String id, Model model) {
		Mono<Producto> productoMono = service.findById(id).doOnNext(p -> {
			log.info("Producto: " + p.getNombre());
		}).defaultIfEmpty(new Producto());
		model.addAttribute("titulo", "Editar Producto");
		model.addAttribute("producto", productoMono);
		return Mono.just("form");
	}

	/**
	 * Editar un producto V2
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/form-v2/{id}")
	public Mono<String> editarV2(@PathVariable(name = "id") String id, Model model) {
		return service.findById(id).doOnNext(p -> {
			model.addAttribute("titulo", "Editar Producto");
			model.addAttribute("producto", p);
			log.info("Producto: " + p.getNombre());
		}).defaultIfEmpty(new Producto()).flatMap(p -> {
			if (p.getId() == null) {
				return Mono.error(new InterruptedException("No existe el producto"));
			}
			return Mono.just(p);
		}).thenReturn("form").onErrorResume(ex -> {
			return Mono.just("redirect:/listar?error=No+existe+el+producto");
		});

	}

	/**
	 * Controller para guardar un producto nuevo
	 * 
	 * @param producto
	 * @param status
	 * @return
	 */
	@PostMapping("/form")
	public Mono<String> guardar(@Valid @ModelAttribute("producto") Producto producto, BindingResult result, Model model,
			@RequestPart(name = "file") FilePart file, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Errores en el formulario producto");
			return Mono.just("form");
		} else {
			status.setComplete();
			Mono<Categoria> categoria = service.findCategoriaById(producto.getCategoria().getId());
			return categoria.flatMap(c -> {
				if (producto.getCreateAt() == null) {
					producto.setCreateAt(new Date());
				}
				if (!file.filename().isEmpty()) {
					producto.setFoto(UUID.randomUUID().toString() + "-"
							+ file.filename().replace(" ", "").replace(":", "").replace("\\", ""));
				}
				producto.setCategoria(c);
				return service.save(producto);
			}).doOnNext(p -> log.info("Producto guardado: " + p.getNombre() + ", Id: " + p.getId())).flatMap(p -> {
				if (!file.filename().isEmpty()) {
					return file.transferTo(new File(path + p.getFoto()));
				}
				return Mono.empty();
			}).thenReturn("redirect:/listar?success=producto+guardado+con+exito");
		}

	}

	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id) {
		return service.findById(id).defaultIfEmpty(new Producto()).flatMap(p -> {
			if (p.getId() == null) {
				return Mono.error(new InterruptedException("No existe el producto"));
			}
			return Mono.just(p);
		}).flatMap(p -> {
			log.info("Eliminando producto: " + p.getNombre());
			return service.delete(p);
		}).then(Mono.just("redirect:/listar?success=producto+eliminado+con+exito")).onErrorResume(ex -> {
			return Mono.just("redirect:/listar?error=No+existe+el+producto+a+eliminar");
		});
	}

	@ModelAttribute("categorias")
	public Flux<Categoria> categorias() {
		return service.findAllCategoria();
	}

	@GetMapping("/ver/{id}")
	public Mono<String> ver(Model model, @PathVariable String id) {
		return service.findById(id).doOnNext(p -> {
			model.addAttribute("producto", p);
			model.addAttribute("titulo", "Detalle Producto");
		}).switchIfEmpty(Mono.just(new Producto())).flatMap(p -> {
			if (p.getId() == null) {
				return Mono.error(new InterruptedException("No existe el producto"));
			}
			return Mono.just(p);
		}).then(Mono.just("ver")).onErrorResume(ex -> {
			return Mono.just("redirect:/listar?error=No+existe+el+producto");
		});
	}
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public Mono<ResponseEntity<Resource>> verFoto(@PathVariable String nombreFoto) throws MalformedURLException{
		Path ruta = Paths.get(path).resolve(nombreFoto).toAbsolutePath();
		Resource imagen = new UrlResource(ruta.toUri());
		return Mono.just(
				ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imagen.getFilename() + "\"")
				.body(imagen)
				);
		
	}
}
