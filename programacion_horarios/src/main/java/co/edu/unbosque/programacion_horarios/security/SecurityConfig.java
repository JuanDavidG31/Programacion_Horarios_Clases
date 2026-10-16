package co.edu.unbosque.programacion_horarios.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de seguridad del prototipo. Por ahora deja pasar todas las
 * peticiones, sin esto Spring Security pide login incluso en el handshake del
 * WebSocket. Cuando se agregue autenticacion de usuarios se cambia aqui.
 *
 * @version 1.0
 */
@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(a -> a.anyRequest().permitAll());
		return http.build();
	}
}
