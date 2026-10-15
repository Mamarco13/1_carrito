package com.practicas.__carrito.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.practicas.__carrito.model.CartItem;
import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.service.DatabaseExportService;
import com.practicas.__carrito.service.ProductService;

import java.util.List;

@Controller
@RequestMapping("/products") // API en /products
public class ProductController {
    // Tenemos un Servicio dentro del controlador
    private final ProductService productService;
    private final DatabaseExportService databaseExportService;

    // Constructor del controlador
    public ProductController(ProductService productService, DatabaseExportService databaseExportService) {
        this.productService = productService;
        this.databaseExportService = databaseExportService;
    }

    //Muestra los productos
    @GetMapping
    public String getProductsPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products"; // Devuelve products.html desde las plantillas
    }

    // Export database as SQL script
    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportDatabase() {
        byte[] sqlContent = databaseExportService.exportDatabaseToSql();
        String filename = "database_export_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql";

        ByteArrayResource resource = new ByteArrayResource(sqlContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/sql"))
                .contentLength(sqlContent.length)
                .body(resource);
    }

    // Muestra el formulario para agregar productos
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "product_form"; // templates/product_form.html
    }

    // Agrega productos
    @PostMapping("/add")
    public String addProduct(@RequestParam String name, @RequestParam double price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        productService.saveProduct(product);
        return "redirect:/products"; // Redirigir correctamente a /products (no existe admin.html)
    }

    // Muestra el formulario para editar productos
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            return "product_form";
        }
        return "redirect:/products";
    }

    // Edita productos
    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, @RequestParam String name, @RequestParam double price) {
        Product product = productService.getProductById(id);
        if (product != null) {
            product.setName(name);
            product.setPrice(price);
            productService.saveProduct(product);
        }
        return "redirect:/products";
    }

    // Muestra advertencia antes de borrar producto
    @GetMapping("/delete/{id}")
    public String showDeleteWarning(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/products";
        }
        
        if (productService.isProductInAnyCart(id)) {
            List<CartItem> cartItems = productService.getCartItemsWithProduct(id);
            model.addAttribute("product", product);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("usersCount", cartItems.size());
            return "product_delete_warning"; // templates/product_delete_warning.html
        } else {
            // Si no está en ningún carrito, eliminamos directamente
            productService.deleteProduct(id);
            return "redirect:/products";
        }
    }

    // Confirma la eliminación del producto
    @PostMapping("/delete/{id}/confirm")
    public String deleteProductConfirmed(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products"; // Redirigir correctamente a /products
    }
}