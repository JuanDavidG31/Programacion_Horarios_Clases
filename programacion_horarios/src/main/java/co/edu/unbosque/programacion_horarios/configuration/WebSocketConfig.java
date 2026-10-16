package co.edu.unbosque.programacion_horarios.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import co.edu.unbosque.programacion_horarios.controller.HorarioWebSocketController;

/**
 * Configuracion del WebSocket del horario. Registra el handler en la ruta
 * {@code /ws/horario}.
 *
 * @version 1.0
 * @see HorarioWebSocketController
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private HorarioWebSocketController horarioHandler;

	/** Origenes permitidos, se lee de application.properties. */
	@Value("${websocket.allowed-origins:http://localhost:5173}")
	private String allowedOrigins;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(horarioHandler, "/ws/horario").setAllowedOriginPatterns(allowedOrigins);
	}
}
