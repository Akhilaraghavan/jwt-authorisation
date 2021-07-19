package com.aragh.resource.controller;

import com.aragh.resource.model.Product;
import com.aragh.resource.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1")
public class ProductController {

    private ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PreAuthorize("hasAnyAuthority('READ_PRIVILEDGE', 'WRITE_PRIVILEDGE')")
    @GetMapping("/product/list")
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @PreAuthorize("hasAuthority('WRITE_PRIVILEDGE')")
    @PostMapping("/product")
    public Integer saveProduct(@RequestBody Product product) {
        return productRepository.save(product).getId();
    }
}
