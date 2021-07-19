package com.aragh.resource.repository;

import com.aragh.resource.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    List<Product> findAll();
}
