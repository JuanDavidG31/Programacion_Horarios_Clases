package co.edu.unbosque.programacion_horarios.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.programacion_horarios.dto.BloqueDTO;
import co.edu.unbosque.programacion_horarios.dto.MoverBloqueDTO;
import co.edu.unbosque.programacion_horarios.dto.ResultadoMovimientoDTO;
import co.edu.unbosque.programacion_horarios.exception.Exceptions;
import co.edu.unbosque.programacion_horarios.model.Bloque;
import co.edu.unbosque.programacion_horarios.model.Dia;
import co.edu.unbosque.programacion_horarios.repository.BloqueRepository;

/**
 * Service con la logica de negocio de los bloques del horario: el CRUD basico y
 * el movimiento de un bloque con control de versiones.
 *
 * @version 1.0
 * @see CRUDOperation
 * @see BloqueRepository
 */
@Service
public class BloqueService implements CRUDOperation<BloqueDTO, Bloque> {

	/** Dia en el que queda un bloque que se mueve por primera vez sin existir. */
	private static final Dia DIA_INICIAL = Dia.LUN;

	/** Hora en la que queda un bloque que se mueve por primera vez sin existir. */
	private static final int HORA_INICIAL = 8;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private BloqueRepository bloqueRepo;

	@Autowired
	private NotificacionService notificacionServ;

	@Autowired
	private ReglasHorarioService reglasServ;

	@Override
	@Transactional
	public BloqueDTO create(BloqueDTO data) {
		if (data.getCodigo() == null || data.getCodigo().isBlank()) {
			throw new Exceptions.ValidationException("El codigo del bloque es obligatorio.");
		}
		if (data.getDia() == null) {
			throw new Exceptions.ValidationException("El dia del bloque es obligatorio.");
		}
		if (!new Exceptions().isSafeText(data.getCurso())) {
			throw new Exceptions.ValidationException("El curso es obligatorio y no puede tener caracteres especiales.");
		}
		reglasServ.validarFranja(data.getDia(), data.getHora());
		if (bloqueRepo.existsByCodigo(data.getCodigo())) {
			throw new Exceptions.ConflictException("Ya existe un bloque con el codigo " + data.getCodigo());
		}
		reglasServ.validarMaxSesiones(data.getCurso(), bloqueRepo.countByCurso(data.getCurso()));

		Bloque entity = modelMapper.map(data, Bloque.class);
		entity.setId(null);
		entity.setVersion(null);
		entity.setCreatedAt(LocalDateTime.now());
		entity.setUpdatedAt(null);

		bloqueRepo.saveAndFlush(entity);
		return modelMapper.map(entity, BloqueDTO.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BloqueDTO> findAll() {
		List<Bloque> entityList = bloqueRepo.findAll();
		List<BloqueDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> dtoList.add(modelMapper.map(entity, BloqueDTO.class)));
		return dtoList;
	}

	@Transactional(readOnly = true)
	public BloqueDTO findById(Long id) {
		Bloque entity = bloqueRepo.findById(id)
				.orElseThrow(() -> new Exceptions.ResourceNotFoundException("Bloque no encontrado con ID: " + id));
		return modelMapper.map(entity, BloqueDTO.class);
	}

	@Transactional(readOnly = true)
	public List<BloqueDTO> findByDia(Dia dia) {
		List<BloqueDTO> dtoList = new ArrayList<>();
		bloqueRepo.findByDia(dia).forEach(entity -> dtoList.add(modelMapper.map(entity, BloqueDTO.class)));
		return dtoList;
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		if (id == null) {
			throw new Exceptions.ValidationException("El ID del bloque no puede ser nulo.");
		}
		Bloque bloque = bloqueRepo.findById(id).orElseThrow(
				() -> new Exceptions.ResourceNotFoundException("Bloque no encontrado para eliminar con ID: " + id));
		bloqueRepo.delete(bloque);
	}

	@Override
	@Transactional
	public BloqueDTO updateById(Long id, BloqueDTO newData) {
		if (id == null) {
			throw new Exceptions.ValidationException("El ID del bloque no puede ser nulo.");
		}
		Bloque existing = bloqueRepo.findById(id)
				.orElseThrow(() -> new Exceptions.ResourceNotFoundException("Bloque no encontrado con ID: " + id));

		if (newData.getCodigo() != null && !newData.getCodigo().equals(existing.getCodigo())
				&& bloqueRepo.existsByCodigo(newData.getCodigo())) {
			throw new Exceptions.ConflictException("Ya existe un bloque con el codigo " + newData.getCodigo());
		}
		if (newData.getDia() != null || newData.getHora() != null) {
			Dia diaFinal = newData.getDia() != null ? newData.getDia() : existing.getDia();
			Integer horaFinal = newData.getHora() != null ? newData.getHora() : existing.getHora();
			reglasServ.validarFranja(diaFinal, horaFinal);
		}
		if (newData.getCurso() != null && !newData.getCurso().equals(existing.getCurso())) {
			if (!new Exceptions().isSafeText(newData.getCurso())) {
				throw new Exceptions.ValidationException("El curso no puede tener caracteres especiales.");
			}
			reglasServ.validarMaxSesiones(newData.getCurso(), bloqueRepo.countByCurso(newData.getCurso()));
			existing.setCurso(newData.getCurso());
		}

		if (newData.getCodigo() != null) {
			existing.setCodigo(newData.getCodigo());
		}
		if (newData.getDia() != null) {
			existing.setDia(newData.getDia());
		}
		if (newData.getHora() != null) {
			existing.setHora(newData.getHora());
		}
		existing.setUpdatedAt(LocalDateTime.now());

		// saveAndFlush para que la version nueva quede lista antes de armar la respuesta
		bloqueRepo.saveAndFlush(existing);
		BloqueDTO dto = modelMapper.map(existing, BloqueDTO.class);
		notificacionServ.notificarCambio(dto);
		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public long count() {
		return bloqueRepo.count();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean exist(Long id) {
		return bloqueRepo.existsById(id);
	}

	/**
	 * Mueve un bloque a otro dia y hora, siempre que el usuario haya partido de la
	 * version vigente. Si la version no coincide (alguien lo movio antes) no se
	 * guarda nada y se devuelve el estado actual del bloque.
	 * <p>
	 * Este metodo no lleva {@code @Transactional} a proposito: asi el guardado
	 * ocurre dentro del repositorio y el fallo por version se puede atrapar aqui
	 * mismo para responder con el bloque vigente.
	 * </p>
	 *
	 * @param datos Codigo del bloque, version base y destino.
	 * @return Resultado indicando si se aplico y el bloque resultante.
	 * @throws Exceptions.ValidationException Si el dia, la hora o la version no son
	 *                                        validos.
	 */
	public ResultadoMovimientoDTO moverSesion(MoverBloqueDTO datos) {
		Exceptions validador = new Exceptions();
		if (!validador.isSafeText(datos.getCodigo())) {
			throw new Exceptions.ValidationException("El codigo del bloque no es valido.");
		}
		if (datos.getVersionBase() == null) {
			throw new Exceptions.ValidationException("La version base es obligatoria.");
		}
		Dia dia = convertirDia(datos.getDia());
		reglasServ.validarFranja(dia, datos.getHora());

		Bloque actual = obtenerOCrear(datos.getCodigo());
		if (!actual.getVersion().equals(datos.getVersionBase())) {
			return new ResultadoMovimientoDTO(false, modelMapper.map(actual, BloqueDTO.class));
		}

		actual.setDia(dia);
		actual.setHora(datos.getHora());
		actual.setUpdatedAt(LocalDateTime.now());

		try {
			Bloque guardado = bloqueRepo.saveAndFlush(actual);
			BloqueDTO dto = modelMapper.map(guardado, BloqueDTO.class);
			notificacionServ.notificarCambio(dto);
			return new ResultadoMovimientoDTO(true, dto);
		} catch (OptimisticLockingFailureException e) {
			// otro usuario guardo entre la lectura y el guardado, se devuelve lo vigente
			Bloque vigente = bloqueRepo.findByCodigo(datos.getCodigo()).orElse(actual);
			return new ResultadoMovimientoDTO(false, modelMapper.map(vigente, BloqueDTO.class));
		}
	}

	/**
	 * Busca el bloque por codigo y, si no existe, lo crea en la posicion inicial
	 * (lunes 8:00, version 0).
	 */
	private Bloque obtenerOCrear(String codigo) {
		Optional<Bloque> existente = bloqueRepo.findByCodigo(codigo);
		if (existente.isPresent()) {
			return existente.get();
		}
		try {
			Bloque nuevo = new Bloque(codigo, DIA_INICIAL, HORA_INICIAL);
			nuevo.setCreatedAt(LocalDateTime.now());
			return bloqueRepo.saveAndFlush(nuevo);
		} catch (DataIntegrityViolationException e) {
			// otro hilo lo creo al mismo tiempo
			return bloqueRepo.findByCodigo(codigo)
					.orElseThrow(() -> new Exceptions.ResourceNotFoundException("Bloque no encontrado: " + codigo));
		}
	}

	private Dia convertirDia(String texto) {
		if (texto == null) {
			throw new Exceptions.ValidationException("El dia es obligatorio.");
		}
		try {
			return Dia.valueOf(texto.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new Exceptions.ValidationException("El dia '" + texto + "' no es valido.");
		}
	}
}
