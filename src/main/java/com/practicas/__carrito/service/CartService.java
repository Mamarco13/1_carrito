package com.practicas.__carrito.service;

import org.springframework.stereotype.Service;

import java.util.List;
import com.practicas.__carrito.model.Product;

@Service
public class CartService {
    private final List<Product> listaProductos;

    public CartService(List<Product> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public Product getProducto(Long id){
        return listaProductos.get(id.intValue());
    }

    public void addProduct(Product producto) {
        listaProductos.add(producto);
    }

    public void deleteProduct(Product producto) {
        listaProductos.remove(producto);
    }
}
