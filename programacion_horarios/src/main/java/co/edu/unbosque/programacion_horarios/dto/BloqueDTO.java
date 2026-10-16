package co.edu.unbosque.programacion_horarios.dto;

import java.time.LocalDateTime;

import co.edu.unbosque.programacion_horarios.model.Dia;

/**
 * DTO con la informacion de un bloque de clase que se envia y recibe del
 * cliente. Es el equivalente a la entidad {@code Bloque} sin exponer
 * directamente la clase de persistencia.
 *
 * @version 1.0
 * @see Dia
 */
public class BloqueDTO {

	/** Identificador interno del bloque. */
	private Long id;

	/** Codigo unico del bloque (ej. B1). */
	private String codigo;

	/** Curso al que pertenece la sesion. */
	private String curso;

	/** Dia de la semana programado. */
	private Dia dia;

	/** Hora de inicio en formato de 24 horas. */
	private Integer hora;

	/** Version actual del bloque, el cliente la usa como version base al mover. */
	private Long version;

	/** Fecha de creacion del bloque. */
	private LocalDateTime createdAt;

	/** Fecha de la ultima modificacion. */
	private LocalDateTime updatedAt;

	public BloqueDTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public Dia getDia() {
		return dia;
	}

	public void setDia(Dia dia) {
		this.dia = dia;
	}

	public Integer getHora() {
		return hora;
	}

	public void setHora(Integer hora) {
		this.hora = hora;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
