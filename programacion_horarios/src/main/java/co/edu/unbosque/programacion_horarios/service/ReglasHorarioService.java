package co.edu.unbosque.programacion_horarios.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.programacion_horarios.configuration.ParametrosHorario;
import co.edu.unbosque.programacion_horarios.exception.Exceptions;
import co.edu.unbosque.programacion_horarios.model.Dia;

/**
 * Service con las restricciones duras del horario: franja de oferta, pausa de
 * almuerzo, duracion de la sesion y maximo de sesiones por curso. Los valores
 * salen de {@link ParametrosHorario}, no estan escritos en el codigo.
 *
 * @version 1.0
 * @see ParametrosHorario
 */
@Service
public class ReglasHorarioService {

	@Autowired
	private ParametrosHorario params;

	public ParametrosHorario getParametros() {
		return params;
	}

	/**
	 * Verifica que una sesion que empieza a la hora indicada cabe en la franja de
	 * oferta del dia y no cruza la pausa de almuerzo.
	 *
	 * @param dia  Dia de la sesion.
	 * @param hora Hora de inicio en formato de 24 horas.
	 * @throws Exceptions.ValidationException Si alguna regla no se cumple.
	 */
	public void validarFranja(Dia dia, Integer hora) {
		if (dia == null || hora == null) {
			throw new Exceptions.ValidationException("El dia y la hora son obligatorios.");
		}
		int fin = hora + params.getDuracionSesion();
		int cierre = (dia == Dia.SAB) ? params.getSabadoFin() : params.getHoraFin();

		if (hora < params.getHoraInicio()) {
			throw new Exceptions.ValidationException("Las clases empiezan a las " + params.getHoraInicio() + ":00.");
		}
		if (fin > cierre) {
			throw new Exceptions.ValidationException("Una sesion de " + params.getDuracionSesion()
					+ " horas que empieza a las " + hora + ":00 termina a las " + fin + ":00, y el " + dia
					+ " se programa hasta las " + cierre + ":00.");
		}
		if (hora < params.getAlmuerzoFin() && fin > params.getAlmuerzoInicio()) {
			throw new Exceptions.ValidationException("No se programan sesiones entre las "
					+ params.getAlmuerzoInicio() + ":00 y las " + params.getAlmuerzoFin() + ":00.");
		}
	}

	/**
	 * Verifica que el curso todavia pueda recibir una sesion mas en la semana.
	 *
	 * @param curso            Nombre del curso.
	 * @param sesionesActuales Cantidad de sesiones que ya tiene.
	 * @throws Exceptions.ValidationException Si ya alcanzo el maximo.
	 */
	public void validarMaxSesiones(String curso, long sesionesActuales) {
		if (sesionesActuales >= params.getMaxSesionesSemana()) {
			throw new Exceptions.ValidationException("El curso " + curso + " ya tiene el maximo de "
					+ params.getMaxSesionesSemana() + " sesiones por semana.");
		}
	}
}
