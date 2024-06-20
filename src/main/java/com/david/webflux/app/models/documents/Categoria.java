package com.david.webflux.app.models.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;

@Document(collection="categorias")
public class Categoria {

	//  Atributos
	@Id
	@NotEmpty
	private String id;
	private String nombre;
	
	// Constructores
	/**
	 * Constructor vacio
	 */
	public Categoria() {
	}
	
	/**
	 * Constructor con campo nombre
	 * @param nombre
	 */
	public Categoria(String nombre) {
		super();
		this.nombre = nombre;
	}

	/**
	 * Constructor con todos los campos
	 * @param id
	 * @param nombre
	 */
	public Categoria(String id, String nombre) {
		super();
		this.id = id;
		this.nombre = nombre;
	}

	// Getters y setters
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

	// Métodos
	@Override
	public String toString() {
		return "Categoria [id=" + id + ", nombre=" + nombre + "]";
	}
	
	
	
	
}
