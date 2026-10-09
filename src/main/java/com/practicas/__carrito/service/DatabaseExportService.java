package com.practicas.__carrito.service;

import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.repository.ProductRepo;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
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

        sqlBuilder.append("-- Exported Data for Product Table\n\n");

        for (Product product : products) {
            String name = product.getName() != null ? product.getName().replace("'", "''") : "";
            // Replace comma for decimal separator if Locale affects String.format, so
            // better use String.valueOf or Locale.US
            sqlBuilder.append(String.format(java.util.Locale.US,
                    "INSERT INTO product (id, name, price) VALUES (%d, '%s', %.2f);\n",
                    product.getId(), name, product.getPrice()));
        }

        return sqlBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
}