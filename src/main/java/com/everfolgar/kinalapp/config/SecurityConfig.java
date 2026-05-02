package com.everfolgar.kinalapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
    // CONFIGURACIÓN DE USUARIOS EN MEMORIA
    // ADMIN: Acceso total al sistema
    // USER: Solo acceso a tienda y perfil programador
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("usuario")
                .password(passwordEncoder().encode("usuario123"))
                .roles("USER")
                .build();

        UserDetails ever = User.builder()
                .username("ever")
                .password(passwordEncoder().encode("ever123"))
                .roles("ADMIN", "USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user, ever);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos (públicos)
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // API REST (pública para Postman)
                        .requestMatchers("/clientes/**", "/productos/**", "/usuarios/**",
                                "/ventas/**", "/detalle-ventas/**").permitAll()

                        // Login (público)
                        .requestMatchers("/tienda/login", "/tienda/login/**").permitAll()

                        // Catálogo de productos (público - no requiere login)
                        .requestMatchers("/tienda/catalogo").permitAll()

                        // Rutas protegidas para USER y ADMIN
                        .requestMatchers("/tienda/carrito/**", "/tienda/agregar/**",
                                "/tienda/actualizar/**", "/tienda/eliminar/**",
                                "/tienda/finalizar/**", "/tienda/vaciar/**",
                                "/tienda/logout", "/web/programador").hasAnyRole("USER", "ADMIN")

                        // Rutas exclusivas para ADMIN
                        .requestMatchers("/web/clientes/**", "/web/productos/**",
                                "/web/usuarios/**", "/web/ventas/**").hasRole("ADMIN")

                        // Página de error 403 (pública)
                        .requestMatchers("/error/**").permitAll()

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // Configuración del formulario de login
                .formLogin(form -> form
                        .loginPage("/tienda/login")
                        .loginProcessingUrl("/tienda/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/tienda/catalogo", true)
                        .failureUrl("/tienda/login?error=true")
                        .permitAll()
                )

                // Configuración de logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/tienda/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // Página de acceso denegado (403)
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/error/403")
                )

                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}