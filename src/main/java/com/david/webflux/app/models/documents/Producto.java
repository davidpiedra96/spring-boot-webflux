package com.david.webflux.app.models.documents;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Document(collection="productos")
public class Producto {
	
	// Atributos
	@Id
	private String id;
	@NotEmpty
	private String nombre;
	@NotNull
	private Double precio;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createAt;
	@Valid
	private Categoria categoria;
	
	private String foto;
	
	// Constructores
	/**
	 * Constructor vacio
	 */
	public Producto() {}
	
	/**
	 * Constructor con campos de nombre y precio
	 * @param nombre
	 * @param precio
	 */
	public Producto(String nombre, Double precio) {
		this.nombre = nombre;
		this.precio = precio;
	}
	
	
	/**
	 * Constructor sin id
	 * @param nombre
	 * @param precio
	 * @param categoria
	 */
	public Producto(String nombre, Double precio, Categoria categoria) {
		this.nombre = nombre;
		this.precio = precio;
		this.categoria = categoria;
	}

	/**
	 * Constructor con todos los campos
	 * @param id
	 * @param nombre
	 * @param precio
	 * @param createAt
	 */
	public Producto(String id, String nombre, Double precio, Date createAt) {
		this.id = id;
		this.nombre = nombre;
		this.precio = precio;
		this.createAt = createAt;
	}
	
	/**
	 * Constructor con todos los campos y la categoria
	 * @param id
	 * @param nombre
	 * @param precio
	 * @param createAt
	 * @param categoria
	 */
	public Producto(String id, String nombre, Double precio, Date createAt, Categoria categoria) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.precio = precio;
		this.createAt = createAt;
		this.categoria = categoria;
	}

	// Getters y Setters
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	// Métodos
	@Override
	public String toString() {
		return "Producto [id=" + id + ", nombre=" + nombre + ", precio=" + precio + ", createAt=" + createAt
				+ ", categoria=" + categoria + "]";
	}
	
}
