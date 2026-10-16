package co.edu.unbosque.programacion_horarios.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Parametros de las reglas del horario. Los valores se leen de
 * application.properties (prefijo {@code horario.}) y si no estan definidos se
 * usan los del caso de estudio. Asi la franja de oferta, el almuerzo y el
 * limite de sesiones se pueden cambiar sin tocar el codigo.
 * <p>
 * Todas las horas estan en formato de 24 horas.
 * </p>
 *
 * @version 1.0
 */
@Component
public class ParametrosHorario {

	/** Hora a la que empieza la oferta (lunes a sabado). */
	@Value("${horario.hora-inicio:7}")
	private int horaInicio;

	/** Hora a la que termina la oferta de lunes a viernes. */
	@Value("${horario.hora-fin:22}")
	private int horaFin;

	/** Hora a la que termina la oferta los sabados. */
	@Value("${horario.sabado-fin:13}")
	private int sabadoFin;

	/** Hora a la que empieza la pausa de almuerzo, en la que no se agenda. */
	@Value("${horario.almuerzo-inicio:12}")
	private int almuerzoInicio;

	/** Hora a la que termina la pausa de almuerzo. */
	@Value("${horario.almuerzo-fin:13}")
	private int almuerzoFin;

	/** Duracion en horas de cada sesion. */
	@Value("${horario.duracion-sesion:2}")
	private int duracionSesion;

	/** Maximo de sesiones por semana que puede tener un curso. */
	@Value("${horario.max-sesiones-semana:4}")
	private int maxSesionesSemana;

	public int getHoraInicio() {
		return horaInicio;
	}

	public int getHoraFin() {
		return horaFin;
	}

	public int getSabadoFin() {
		return sabadoFin;
	}

	public int getAlmuerzoInicio() {
		return almuerzoInicio;
	}

	public int getAlmuerzoFin() {
		return almuerzoFin;
	}

	public int getDuracionSesion() {
		return duracionSesion;
	}

	public int getMaxSesionesSemana() {
		return maxSesionesSemana;
	}
}
