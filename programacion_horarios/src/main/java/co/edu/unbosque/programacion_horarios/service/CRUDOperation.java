package co.edu.unbosque.programacion_horarios.service;

import java.util.List;

/**
 * Interfaz generica con las operaciones CRUD basicas que implementan los
 * services del sistema.
 *
 * @param <T> Tipo del DTO que entra y sale del service.
 * @param <E> Tipo de la entidad con la que trabaja el service.
 * @version 1.0
 */
public interface CRUDOperation<T, E> {

	/**
	 * Crea un registro nuevo.
	 *
	 * @param data Datos del registro.
	 * @return El registro creado.
	 */
	public T create(T data);

	/**
	 * @return Todos los registros.
	 */
	public List<T> findAll();

	/**
	 * Elimina un registro por su id.
	 *
	 * @param id Identificador del registro.
	 */
	public void deleteById(Long id);

	/**
	 * Actualiza un registro existente.
	 *
	 * @param id      Identificador del registro.
	 * @param newData Datos nuevos.
	 * @return El registro actualizado.
	 */
	public T updateById(Long id, T newData);

	/**
	 * @return Cantidad de registros.
	 */
	public long count();

	/**
	 * @param id Identificador a consultar.
	 * @return {@code true} si existe un registro con ese id.
	 */
	public boolean exist(Long id);

}
