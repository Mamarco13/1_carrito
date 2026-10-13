package com.practicas.__carrito.service;

import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.repository.ProductRepo;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DatabaseExportService {

    private final ProductRepo productRepo;

    public DatabaseExportService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public byte[] exportDatabaseToSql() {
        List<Product> products = productRepo.findAll();
        StringBuilder sqlBuilder = new StringBuilder();

        // Metadata
        sqlBuilder.append("-- =============================================\n");
        sqlBuilder.append("-- Database Export Script\n");
        sqlBuilder.append("-- Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sqlBuilder.append("-- Database: PostgreSQL\n");
        sqlBuilder.append("-- =============================================\n\n");

        // CREATE TABLE 
        sqlBuilder.append("-- ---------------------------------------------\n");
        sqlBuilder.append("-- Table structure for product\n");
        sqlBuilder.append("-- ---------------------------------------------\n\n");
        
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS product (\n");
        sqlBuilder.append("    id BIGSERIAL PRIMARY KEY,\n");
        sqlBuilder.append("    name VARCHAR(255) NOT NULL,\n");
        sqlBuilder.append("    price DOUBLE PRECISION NOT NULL\n");
        sqlBuilder.append(");\n\n");

        sqlBuilder.append("-- ---------------------------------------------\n");
        sqlBuilder.append("-- Reset sequence for product.id\n");
        sqlBuilder.append("-- ---------------------------------------------\n\n");
        sqlBuilder.append("SELECT setval('product_id_seq', COALESCE((SELECT MAX(id) FROM product), 0), true);\n\n");

        // INSERT
        sqlBuilder.append("-- ---------------------------------------------\n");
        sqlBuilder.append("-- Data for product\n");
        sqlBuilder.append("-- ---------------------------------------------\n\n");

        if (products.isEmpty()) {
            sqlBuilder.append("-- No data to export\n");
        } else {
            for (Product product : products) {
                String name = product.getName() != null ? product.getName().replace("'", "''") : "";
                sqlBuilder.append(String.format(java.util.Locale.US,
                        "INSERT INTO product (id, name, price) VALUES (%d, '%s', %.2f);\n",
                        product.getId(), name, product.getPrice()));
            }
        }

        sqlBuilder.append("\n-- =============================================\n");
        sqlBuilder.append("-- End of export\n");
        sqlBuilder.append("-- =============================================\n");

        return sqlBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
}