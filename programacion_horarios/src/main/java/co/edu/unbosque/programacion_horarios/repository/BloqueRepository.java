package co.edu.unbosque.programacion_horarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.programacion_horarios.model.Bloque;
import co.edu.unbosque.programacion_horarios.model.Dia;

/**
 * Repositorio JPA de la entidad {@link Bloque}.
 *
 * @version 1.0
 */
public interface BloqueRepository extends JpaRepository<Bloque, Long> {

	/** Busca un bloque por su codigo unico. */
	Optional<Bloque> findByCodigo(String codigo);

	/** Indica si ya existe un bloque con ese codigo. */
	boolean existsByCodigo(String codigo);

	/** Cuenta las sesiones que tiene un curso. */
	long countByCurso(String curso);

	/** Lista los bloques programados en un dia. */
	List<Bloque> findByDia(Dia dia);
}
