package com.practicas.__carrito.repository;

import com.practicas.__carrito.model.CartItem;
import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, com.practicas.__carrito.model.Product product);
    void deleteByUser(User user);
    List<CartItem> findByProduct(Product product);
}