package com.practicas.__carrito.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.repository.ProductRepo;

@Service
public class ProductService {

    private final ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    public void saveProduct(Product producto) {
        productRepo.save(producto);
    }

    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }
}
