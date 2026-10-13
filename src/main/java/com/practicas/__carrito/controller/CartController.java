package com.practicas.__carrito.controller;

import com.practicas.__carrito.model.CartItem;
import com.practicas.__carrito.model.Product;
import com.practicas.__carrito.model.User;
import com.practicas.__carrito.repository.UserRepo;
import com.practicas.__carrito.service.CartService;
import com.practicas.__carrito.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final UserRepo userRepo;

    @Autowired
    public CartController(CartService cartService, ProductService productService, UserRepo userRepo) {
        this.cartService = cartService;
        this.productService = productService;
        this.userRepo = userRepo;
    }

    private User getCurrentUser(UserDetails userDetails) {
        if (userDetails == null) return null;
        return userRepo.findByUsername(userDetails.getUsername()).orElse(null);
    }

    @GetMapping("/cart")
    public String cart(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.getCartTotal(user));
        model.addAttribute("cartItemCount", cartService.getCartItemCount(user));
        return "cart";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable("id") Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes ra) {
        User user = getCurrentUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }

        Product p = productService.getProductById(id);
        if (p != null) {
            cartService.addToCart(user, id);
            ra.addFlashAttribute("msg", "Añadido al carrito");
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes ra) {
        User user = getCurrentUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }

        cartService.removeFromCart(user, id);
        ra.addFlashAttribute("msg", "Eliminado del carrito");
        return "redirect:/cart";
    }
}