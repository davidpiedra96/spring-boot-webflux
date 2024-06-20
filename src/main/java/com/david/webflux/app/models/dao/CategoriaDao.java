package com.david.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.david.webflux.app.models.documents.Categoria;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria,String>{

}
