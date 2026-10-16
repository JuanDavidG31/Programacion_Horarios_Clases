package co.edu.unbosque.programacion_horarios.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import co.edu.unbosque.programacion_horarios.dto.BloqueDTO;
import co.edu.unbosque.programacion_horarios.util.MensajeWsUtil;

/**
 * Service que guarda las conexiones WebSocket abiertas y les envia los avisos
 * de cambios en el horario.
 *
 * @version 1.0
 * @see MensajeWsUtil
 */
@Service
public class NotificacionService {

	private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

	/** Conexiones abiertas, la llave es el id de la sesion de WebSocket. */
	private final Map<String, WebSocketSession> sesiones = new ConcurrentHashMap<>();

	/**
	 * Guarda una conexion nueva. Se envuelve en un decorador para que varios hilos
	 * puedan escribirle sin que se mezclen los mensajes.
	 *
	 * @param session Conexion recien abierta.
	 */
	public void registrar(WebSocketSession session) {
		sesiones.put(session.getId(), new ConcurrentWebSocketSessionDecorator(session, 5000, 64 * 1024));
	}

	/**
	 * Quita una conexion que se cerro.
	 *
	 * @param session Conexion cerrada.
	 */
	public void retirar(WebSocketSession session) {
		sesiones.remove(session.getId());
	}

	/**
	 * Envia un mensaje a una sola conexion.
	 *
	 * @param sesionId Id de la sesion de WebSocket.
	 * @param texto    Mensaje a enviar.
	 * @throws IOException Si falla el envio.
	 */
	public void enviar(String sesionId, String texto) throws IOException {
		WebSocketSession s = sesiones.get(sesionId);
		if (s != null) {
			s.sendMessage(new TextMessage(texto));
		}
	}

	/**
	 * Avisa a todas las conexiones abiertas que un bloque cambio de posicion.
	 *
	 * @param bloque Bloque con su posicion y version nuevas.
	 */
	public void notificarCambio(BloqueDTO bloque) {
		TextMessage msg = new TextMessage(MensajeWsUtil.armar("ACTUALIZADO", bloque));
		for (WebSocketSession s : sesiones.values()) {
			try {
				if (s.isOpen()) {
					s.sendMessage(msg);
				}
			} catch (IOException e) {
				log.warn("No se pudo enviar a la sesion {}", s.getId());
			}
		}
	}
}
