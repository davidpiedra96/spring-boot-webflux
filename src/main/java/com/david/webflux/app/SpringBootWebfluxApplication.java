package com.david.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.david.webflux.app.models.documents.Categoria;
import com.david.webflux.app.models.documents.Producto;
import com.david.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	@Autowired
	private ProductoService service;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*
		 * mongoTemplate.dropCollection("productos").subscribe();
		 * mongoTemplate.dropCollection("categorias").subscribe();
		 * 
		 * Categoria electronico = new Categoria("Electronico"); Categoria hogar = new
		 * Categoria("Hogar"); Categoria computacion = new Categoria("Computacion");
		 * 
		 * Flux.just(electronico, hogar, computacion) .flatMap(service::saveCategoria)
		 * .doOnNext(c -> { log.info("Categoria creada: " + c.getNombre() + ", id: " +
		 * c.getId()); }) .thenMany(Flux.just( new Producto("Celular",20.0,electronico),
		 * new Producto("Televisor",100.0,hogar), new Producto("Casa",1200.000,hogar),
		 * new Producto("Computador",800.000,computacion) ) .flatMap(producto -> {
		 * producto.setCreateAt(new Date()); return service.save(producto); }))
		 * .subscribe();
		 */
	}

}
