package co.edu.unbosque.programacion_horarios.dto;

/**
 * DTO con los datos que llegan cuando un usuario mueve un bloque a otro dia u
 * hora. Incluye la version que el usuario tenia del bloque para poder detectar
 * si alguien mas lo movio antes.
 *
 * @version 1.0
 */
public class MoverBloqueDTO {

	/** Codigo del bloque que se quiere mover. */
	private String codigo;

	/** Version del bloque que el usuario estaba viendo. */
	private Long versionBase;

	/** Dia destino (LUN, MAR, MIE, JUE, VIE, SAB). */
	private String dia;

	/** Hora destino en formato de 24 horas. */
	private Integer hora;

	public MoverBloqueDTO() {
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Long getVersionBase() {
		return versionBase;
	}

	public void setVersionBase(Long versionBase) {
		this.versionBase = versionBase;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public Integer getHora() {
		return hora;
	}

	public void setHora(Integer hora) {
		this.hora = hora;
	}
}
