package co.edu.unbosque.programacion_horarios.util;

import co.edu.unbosque.programacion_horarios.dto.BloqueDTO;

/**
 * Utilidad para armar los mensajes de texto que se envian por WebSocket.
 * <p>
 * Formato: {@code TIPO|codigo|dia|hora|version}, por ejemplo
 * {@code ACTUALIZADO|B1|MAR|10|3}.
 * </p>
 *
 * @version 1.0
 */
public class MensajeWsUtil {

	private MensajeWsUtil() {
	}

	/**
	 * Arma el mensaje de un bloque con su tipo.
	 *
	 * @param tipo   Tipo de mensaje (ACTUALIZADO, CONFLICTO).
	 * @param bloque Bloque con los datos a enviar.
	 * @return Texto listo para enviar al cliente.
	 */
	public static String armar(String tipo, BloqueDTO bloque) {
		return tipo + "|" + bloque.getCodigo() + "|" + bloque.getDia() + "|" + bloque.getHora() + "|"
				+ bloque.getVersion();
	}
}
