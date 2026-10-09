package com.practicas.__carrito.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.service.ProductService;

@Controller
@RequestMapping("/products") // API en /products
public class ProductController {
    // Tenemos un Servicio dentro del controlador
    private final ProductService productService;

    // Constructor del controlador
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //Muestra los productos
    @GetMapping
    public String getProductsPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products"; // Devuelve products.html desde las plantillas
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

    // Borra productos
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products"; // Redirigir correctamente a /products (no existe admin.html)
    }
}