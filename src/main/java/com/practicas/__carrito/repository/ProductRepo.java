package com.practicas.__carrito.repository;

import com.practicas.__carrito.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> { // Product como lo que almacenamos, Long es nuestra
                                                                    // clave primaria

}
