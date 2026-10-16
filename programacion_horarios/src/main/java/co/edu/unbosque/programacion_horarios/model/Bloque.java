package co.edu.unbosque.programacion_horarios.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Entidad que representa la tabla "bloques". Cada bloque es una sesion de clase
 * que ocupa una hora de un dia de la semana dentro del horario.
 * <p>
 * El campo {@code version} lo maneja JPA con {@link Version}: cada vez que se
 * actualiza el bloque la version sube en uno, y si dos usuarios intentan
 * guardar a partir de la misma version solo el primero lo logra. Asi se evita
 * que un usuario pise sin querer el movimiento de otro.
 * </p>
 *
 * @version 1.0
 * @see Dia
 */
@Entity
@Table(name = "bloques")
public class Bloque {

	/** Llave primaria autoincremental del bloque. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Codigo unico con el que el cliente identifica el bloque (ej. B1). */
	@Column(unique = true, nullable = false)
	private String codigo;

	/** Curso al que pertenece la sesion (ej. Calculo I). */
	private String curso;

	/** Dia de la semana en el que esta programado el bloque. */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Dia dia;

	/** Hora de inicio del bloque, en formato de 24 horas (ej. 8, 14). */
	@Column(nullable = false)
	private Integer hora;

	/** Version del registro, se incrementa en cada actualizacion. */
	@Version
	private Long version;

	/** Fecha y hora en la que se creo el bloque. */
	private LocalDateTime createdAt;

	/** Fecha y hora de la ultima modificacion, nula si nunca se ha modificado. */
	private LocalDateTime updatedAt;

	public Bloque() {
	}

	public Bloque(String codigo, Dia dia, Integer hora) {
		this.codigo = codigo;
		this.dia = dia;
		this.hora = hora;
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
