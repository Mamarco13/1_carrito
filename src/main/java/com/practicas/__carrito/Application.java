package com.practicas.__carrito;

import com.practicas.__carrito.model.User;
import com.practicas.__carrito.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner initUsers(UserService userService) {
        return args -> {
            userService.createDefaultAdmin();
            userService.createDefaultUser();
        };
    }
}