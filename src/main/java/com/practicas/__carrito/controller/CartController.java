package com.practicas.__carrito.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.service.ProductService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
@SessionAttributes("cartItems")
public class CartController {
    private final ProductService productService;

	public CartController(ProductService productService){
		this.productService = productService;
	}

	@ModelAttribute("cartItems")
	public List<Product> cartItems(){
      return new ArrayList<>();
    }

	@GetMapping("/cart")
	public String cart(Model model){
      return "cart";
    }

	@PostMapping("/cart/add/{id}")
	public String addToCart(@PathVariable("id") Long id, @ModelAttribute("cartItems") List<Product> cartItems, RedirectAttributes ra){
	  Product p = productService.getProductById(id);
	  if (p != null){
	    cartItems.add(p);
	    ra.addFlashAttribute("msg", "Añadido al carrito");
	  }
	  return "redirect:/cart";
	}

	@PostMapping("/cart/remove/{id}")
	public String removeFromCart(@PathVariable("id") Long id, @ModelAttribute("cartItems") List<Product> cartItems, RedirectAttributes ra){
	  for (Iterator<Product> it = cartItems.iterator(); it.hasNext();){
	    if (it.next().getId().equals(id)){
          it.remove();
          break;
        }
	  }
	  ra.addFlashAttribute("msg", "Eliminado del carrito");
	  return "redirect:/cart";
	}
}
