package com.practicas.__carrito.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.practicas.__carrito.model.CartItem;
import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.repository.CartItemRepo;
import com.practicas.__carrito.repository.ProductRepo;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final CartItemRepo cartItemRepo;

    public ProductService(ProductRepo productRepo, CartItemRepo cartItemRepo) {
        this.productRepo = productRepo;
        this.cartItemRepo = cartItemRepo;
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

    public boolean isProductInAnyCart(Long productId) {
        Product product = productRepo.findById(productId).orElse(null);
        if (product == null) return false;
        return !cartItemRepo.findByProduct(product).isEmpty();
    }

    public List<CartItem> getCartItemsWithProduct(Long productId) {
        Product product = productRepo.findById(productId).orElse(null);
        if (product == null) return List.of();
        return cartItemRepo.findByProduct(product);
    }

    public void deleteProduct(Long id) {
        // First delete all cart items referencing this product to avoid FK constraint violation
        Product product = productRepo.findById(id).orElse(null);
        if (product != null) {
            List<CartItem> cartItems = cartItemRepo.findByProduct(product);
            if (!cartItems.isEmpty()) {
                cartItemRepo.deleteAll(cartItems);
            }
        }
        productRepo.deleteById(id);
    }
}
