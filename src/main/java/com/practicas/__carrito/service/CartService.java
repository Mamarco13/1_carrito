package com.practicas.__carrito.service;

import com.practicas.__carrito.model.CartItem;
import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.model.User;
import com.practicas.__carrito.repository.CartItemRepo;
import com.practicas.__carrito.repository.ProductRepo;
import com.practicas.__carrito.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartItemRepo cartItemRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;

    @Autowired
    public CartService(CartItemRepo cartItemRepo, ProductRepo productRepo, UserRepo userRepo) {
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public List<CartItem> getCartItems(User user) {
        return cartItemRepo.findByUser(user);
    }

    @Transactional
    public void addToCart(User user, Long productId) {
        Product product = productRepo.findById(productId).orElse(null);
        if (product == null) return;

        Optional<CartItem> existingItem = cartItemRepo.findByUserAndProduct(user, product);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepo.save(item);
        } else {
            CartItem newItem = new CartItem(user, product);
            cartItemRepo.save(newItem);
        }
    }

    @Transactional
    public void removeFromCart(User user, Long productId) {
        Product product = productRepo.findById(productId).orElse(null);
        if (product == null) return;

        Optional<CartItem> existingItem = cartItemRepo.findByUserAndProduct(user, product);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                cartItemRepo.save(item);
            } else {
                cartItemRepo.delete(item);
            }
        }
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepo.deleteByUser(user);
    }

    public int getCartItemCount(User user) {
        return cartItemRepo.findByUser(user).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public int getCartItemCount(String username) {
        return userRepo.findByUsername(username)
                .map(this::getCartItemCount)
                .orElse(0);
    }

    public double getCartTotal(User user) {
        return cartItemRepo.findByUser(user).stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}