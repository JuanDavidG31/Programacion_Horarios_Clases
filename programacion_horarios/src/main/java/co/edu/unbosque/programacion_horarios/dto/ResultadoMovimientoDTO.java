package co.edu.unbosque.programacion_horarios.dto;

/**
 * DTO con el resultado de intentar mover un bloque. Si {@code aplicado} es
 * verdadero el bloque trae la posicion nueva; si es falso hubo un conflicto de
 * version y el bloque trae el estado vigente en el servidor.
 *
 * @version 1.0
 * @see BloqueDTO
 */
public class ResultadoMovimientoDTO {

	/** Indica si el movimiento se guardo. */
	private boolean aplicado;

	/** Estado del bloque despues de la operacion. */
	private BloqueDTO bloque;

	public ResultadoMovimientoDTO() {
	}

	public ResultadoMovimientoDTO(boolean aplicado, BloqueDTO bloque) {
		this.aplicado = aplicado;
		this.bloque = bloque;
	}

	public boolean isAplicado() {
		return aplicado;
	}

	public void setAplicado(boolean aplicado) {
		this.aplicado = aplicado;
	}

	public BloqueDTO getBloque() {
		return bloque;
	}

	public void setBloque(BloqueDTO bloque) {
		this.bloque = bloque;
	}
}
