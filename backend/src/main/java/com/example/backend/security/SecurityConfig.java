package com.example.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public hoan toan - khong can dang nhap
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/hello").permitAll()

                // Xem (GET) thi public - ai cung xem duoc danh muc/san pham de mua hang
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/brands/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/attributes/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/attribute-values/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/category-attributes/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                // Sua/xoa (POST, PUT, PATCH, DELETE) cac danh muc quan tri -> chi ADMIN
                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/categories/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAuthority("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/brands/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/brands/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/brands/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/brands/**").hasAuthority("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/attributes/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/attributes/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/attributes/**").hasAuthority("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/attribute-values/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/attribute-values/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/attribute-values/**").hasAuthority("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/category-attributes/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/category-attributes/**").hasAuthority("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasAuthority("ADMIN")

                // Warehouse: toan bo chi ADMIN (khong co GET public, vi day la du lieu quan tri noi bo)
                .requestMatchers("/api/suppliers/**").hasAuthority("ADMIN")
                .requestMatchers("/api/purchase-orders/**").hasAuthority("ADMIN")

                // Order: cap nhat trang thai chi ADMIN, con lai (checkout, xem don cua minh) chi can dang nhap
                .requestMatchers(HttpMethod.PATCH, "/api/orders/*/status").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/orders").hasAuthority("ADMIN")   // xem toan bo don - chi admin

                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                .requestMatchers("/error").permitAll()
                
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/promotions/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/promotion-products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/promotions/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/promotions/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/promotions/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/promotion-products/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/promotion-products/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/return-requests/*/process").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/return-requests").hasAuthority("ADMIN")

                // Con lai (Cart, checkout, my-orders...) - chi can dang nhap, khong phan biet role
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}