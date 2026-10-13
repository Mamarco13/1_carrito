package com.practicas.__carrito.service;

import com.practicas.__carrito.model.CartItem;
import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.model.User;
import com.practicas.__carrito.repository.CartItemRepo;
import com.practicas.__carrito.repository.ProductRepo;
import com.practicas.__carrito.repository.UserRepo;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DatabaseExportService {

    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final CartItemRepo cartItemRepo;

    public DatabaseExportService(ProductRepo productRepo, UserRepo userRepo, CartItemRepo cartItemRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.cartItemRepo = cartItemRepo;
    }

    public byte[] exportDatabaseToSql() {
        List<Product> products = productRepo.findAll();
        List<User> users = userRepo.findAll();
        List<CartItem> cartItems = cartItemRepo.findAll();
        StringBuilder sqlBuilder = new StringBuilder();

        // Header with metadata
        sqlBuilder.append("-- =============================================\n");
        sqlBuilder.append("-- Database Export Script\n");
        sqlBuilder.append("-- Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sqlBuilder.append("-- Database: PostgreSQL\n");
        sqlBuilder.append("-- =============================================\n\n");

        // CREATE TABLE statements
        sqlBuilder.append("-- ---------------------------------------------\n");
        sqlBuilder.append("-- Table structure for users\n");
        sqlBuilder.append("-- ---------------------------------------------\n\n");
        
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS users (\n");
        sqlBuilder.append("    id BIGSERIAL PRIMARY KEY,\n");
        sqlBuilder.append("    username VARCHAR(255) NOT NULL UNIQUE,\n");
        sqlBuilder.append("    password VARCHAR(255) NOT NULL,\n");
        sqlBuilder.append("    email VARCHAR(255) NOT NULL UNIQUE,\n");
        sqlBuilder.append("    role VARCHAR(20) NOT NULL DEFAULT 'USER'\n");
        sqlBuilder.append(");\n\n");

        sqlBuilder.append("-- ---------------------------------------------\n");
        sqlBuilder.append("-- Table structure for product\n");
        sqlBuilder.append("-- ---------------------------------------------\n\n");
        
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS product (\n");
        sqlBuilder.append("    id BIGSERIAL PRIMARY KEY,\n");
        sqlBuilder.append("    name VARCHAR(255) NOT NULL,\n");
        sqlBuilder.append("    price DOUBLE PRECISION NOT NULL\n");
        sqlBuilder.append(");\n\n");

        sqlBuilder.append("-- ---------------------------------------------\n");
        sqlBuilder.append("-- Table structure for cart_items\n");
        sqlBuilder.append("-- ---------------------------------------------\n\n");
        
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS cart_items (\n");
        sqlBuilder.append("    id BIGSERIAL PRIMARY KEY,\n");
        sqlBuilder.append("    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,\n");
        sqlBuilder.append("    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,\n");
        sqlBuilder.append("    quantity INT NOT NULL DEFAULT 1\n");
        sqlBuilder.append(");\n\n");

        // Sequence resets
        sqlBuilder.append("-- ---------------------------------------------\n");
        sqlBuilder.append("-- Reset sequences\n");
        sqlBuilder.append("-- ---------------------------------------------\n\n");
        sqlBuilder.append("SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 0), true);\n");
        sqlBuilder.append("SELECT setval('product_id_seq', COALESCE((SELECT MAX(id) FROM product), 0), true);\n");
        sqlBuilder.append("SELECT setval('cart_items_id_seq', COALESCE((SELECT MAX(id) FROM cart_items), 0), true);\n\n");

        // INSERT statements for users
        sqlBuilder.append("-- ---------------------------------------------\n");
        sqlBuilder.append("-- Data for users\n");
        sqlBuilder.append("-- ---------------------------------------------\n\n");

        if (users.isEmpty()) {
            sqlBuilder.append("-- No users to export\n");
        } else {
            for (User user : users) {
                String username = user.getUsername() != null ? user.getUsername().replace("'", "''") : "";
                String email = user.getEmail() != null ? user.getEmail().replace("'", "''") : "";
                String password = user.getPassword() != null ? user.getPassword().replace("'", "''") : "";
                String role = user.getRole() != null ? user.getRole().name() : "USER";
                
                sqlBuilder.append(String.format(java.util.Locale.US,
                        "INSERT INTO users (id, username, password, email, role) VALUES (%d, '%s', '%s', '%s', '%s');\n",
                        user.getId(), username, password, email, role));
            }
        }

        // INSERT statements for products
        sqlBuilder.append("\n-- ---------------------------------------------\n");
        sqlBuilder.append("-- Data for product\n");
        sqlBuilder.append("-- ---------------------------------------------\n\n");

        if (products.isEmpty()) {
            sqlBuilder.append("-- No products to export\n");
        } else {
            for (Product product : products) {
                String name = product.getName() != null ? product.getName().replace("'", "''") : "";
                sqlBuilder.append(String.format(java.util.Locale.US,
                        "INSERT INTO product (id, name, price) VALUES (%d, '%s', %.2f);\n",
                        product.getId(), name, product.getPrice()));
            }
        }

        // INSERT statements for cart_items
        sqlBuilder.append("\n-- ---------------------------------------------\n");
        sqlBuilder.append("-- Data for cart_items\n");
        sqlBuilder.append("-- ---------------------------------------------\n\n");

        if (cartItems.isEmpty()) {
            sqlBuilder.append("-- No cart items to export\n");
        } else {
            for (CartItem item : cartItems) {
                sqlBuilder.append(String.format(java.util.Locale.US,
                        "INSERT INTO cart_items (id, user_id, product_id, quantity) VALUES (%d, %d, %d, %d);\n",
                        item.getId(), item.getUser().getId(), item.getProduct().getId(), item.getQuantity()));
            }
        }

        sqlBuilder.append("\n-- =============================================\n");
        sqlBuilder.append("-- End of export\n");
        sqlBuilder.append("-- =============================================\n");

        return sqlBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
}