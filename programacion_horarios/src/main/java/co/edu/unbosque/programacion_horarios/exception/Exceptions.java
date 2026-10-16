package co.edu.unbosque.programacion_horarios.exception;

/**
 * Clase central de validaciones de entrada y excepciones de negocio del sistema
 * de programacion de horarios.
 * <p>
 * Los metodos de validacion los usan los controllers como primera revision del
 * formato de los datos, y las clases internas se lanzan desde los services
 * cuando falla una regla de negocio.
 * </p>
 *
 * @version 1.0
 */
public class Exceptions {

	/**
	 * Revisa si un texto trae simbolos propios de HTML o scripts.
	 *
	 * @param text Texto a revisar.
	 * @return {@code true} si contiene alguno de los caracteres {@code < > & " ' / =}.
	 */
	public boolean containsHtmlSymbols(String text) {
		if (text == null || text.isBlank())
			return false;
		return text.matches(".*[<>&\"'/=].*");
	}

	/**
	 * Valida que un texto no sea nulo ni vacio y que no tenga simbolos HTML.
	 *
	 * @param value Texto a validar.
	 * @return {@code true} si el texto es seguro.
	 */
	public boolean isSafeText(String value) {
		if (value == null || value.isBlank())
			return false;
		return !containsHtmlSymbols(value);
	}

	/**
	 * Valida que un identificador sea positivo.
	 *
	 * @param id Identificador a validar.
	 * @return {@code true} si no es nulo y es mayor que cero.
	 */
	public boolean isValidId(Long id) {
		return id != null && id > 0;
	}

	// =========================================================================
	// EXCEPCIONES DE NEGOCIO
	// =========================================================================

	/**
	 * Se lanza cuando el recurso pedido no existe en la base de datos.
	 */
	public static class ResourceNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		/**
		 * @param message Descripcion del recurso que no se encontro.
		 */
		public ResourceNotFoundException(String message) {
			super(message);
		}
	}

	/**
	 * Se lanza cuando los datos recibidos no cumplen las reglas de validacion.
	 */
	public static class ValidationException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		/**
		 * @param message Descripcion de la regla que no se cumplio.
		 */
		public ValidationException(String message) {
			super(message);
		}
	}

	/**
	 * Se lanza cuando la operacion choca con el estado actual de los datos, por
	 * ejemplo un codigo de bloque repetido.
	 */
	public static class ConflictException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		/**
		 * @param message Motivo del conflicto.
		 */
		public ConflictException(String message) {
			super(message);
		}
	}
}
