package co.edu.unbosque.programacion_horarios.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import co.edu.unbosque.programacion_horarios.dto.MoverBloqueDTO;
import co.edu.unbosque.programacion_horarios.dto.ResultadoMovimientoDTO;
import co.edu.unbosque.programacion_horarios.exception.Exceptions;
import co.edu.unbosque.programacion_horarios.service.BloqueService;
import co.edu.unbosque.programacion_horarios.service.NotificacionService;
import co.edu.unbosque.programacion_horarios.util.MensajeWsUtil;

/**
 * Controller de WebSocket del tablero de horarios. Recibe las peticiones de
 * mover bloques y delega la logica en {@link BloqueService}. Los avisos al
 * resto de usuarios los envia {@link NotificacionService}.
 * <p>
 * Mensaje que recibe: {@code MOVER|codigo|dia|hora|versionBase}<br>
 * Mensajes que responde: {@code ACTUALIZADO|...} a todos si se aplico,
 * {@code CONFLICTO|...} solo a quien envio si la version estaba desactualizada,
 * y {@code ERROR|motivo} si el mensaje no es valido.
 * </p>
 *
 * @version 1.0
 */
@Component
public class HorarioWebSocketController extends TextWebSocketHandler {

	@Autowired
	private BloqueService bloqueServ;

	@Autowired
	private NotificacionService notificacionServ;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		notificacionServ.registrar(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		notificacionServ.retirar(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
		String[] p = message.getPayload().split("\\|");
		if (p.length != 5 || !p[0].equals("MOVER")) {
			notificacionServ.enviar(session.getId(), "ERROR|formato");
			return;
		}

		MoverBloqueDTO dto = new MoverBloqueDTO();
		try {
			dto.setCodigo(p[1]);
			dto.setDia(p[2]);
			dto.setHora(Integer.parseInt(p[3]));
			dto.setVersionBase(Long.parseLong(p[4]));
		} catch (NumberFormatException e) {
			notificacionServ.enviar(session.getId(), "ERROR|numero");
			return;
		}

		try {
			ResultadoMovimientoDTO r = bloqueServ.moverSesion(dto);
			if (!r.isAplicado()) {
				notificacionServ.enviar(session.getId(), MensajeWsUtil.armar("CONFLICTO", r.getBloque()));
			}
		} catch (Exceptions.ValidationException e) {
			notificacionServ.enviar(session.getId(), "ERROR|" + e.getMessage());
		}
	}
}
