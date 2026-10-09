package com.practicas.__carrito.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/index", "/index.html", "/login",
                                "/css/**", "/js/**", "/images/**", "/webjars/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/products", "/products/**", "/cart", "/cart/**").permitAll()
                        .requestMatchers("/api/**").permitAll() // si queremos API sea pública
                        .requestMatchers(HttpMethod.POST, "/cart/**").permitAll()
                        .requestMatchers("/admin/**",
                                "/products/add", "/products/edit/**", "/products/delete/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll())
                // En desarrollo, ignora CSRF en endpoints POST que usas en formularios
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/cart/**", "/products/add", "/products/edit/**", "/products/delete/**"));
        return http.build();
    }

    @Bean
    WebSecurityCustomizer ignoringCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/h2-console/**");
    }

    public UserDetailsService users() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin").password("admin").roles("ADMIN").build();
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user").password("user").roles("USER").build();
        return new InMemoryUserDetailsManager(admin, user);
    }

}
