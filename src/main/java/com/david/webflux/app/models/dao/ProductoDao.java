package com.david.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.david.webflux.app.models.documents.Producto;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String>{

}
