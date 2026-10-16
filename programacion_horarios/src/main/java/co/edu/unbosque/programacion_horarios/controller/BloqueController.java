package co.edu.unbosque.programacion_horarios.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.programacion_horarios.dto.BloqueDTO;
import co.edu.unbosque.programacion_horarios.dto.MoverBloqueDTO;
import co.edu.unbosque.programacion_horarios.dto.ResultadoMovimientoDTO;
import co.edu.unbosque.programacion_horarios.exception.Exceptions;
import co.edu.unbosque.programacion_horarios.model.Dia;
import co.edu.unbosque.programacion_horarios.service.BloqueService;
import co.edu.unbosque.programacion_horarios.service.ReglasHorarioService;

/**
 * Controller REST de los bloques del horario. Expone el CRUD y el movimiento de
 * un bloque con control de versiones.
 *
 * @version 1.0
 * @see BloqueService
 */
@RestController
@RequestMapping("/bloques")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:4200" })
public class BloqueController {

	@Autowired
	private BloqueService bloqueServ;

	@Autowired
	private ReglasHorarioService reglasServ;

	/**
	 * Devuelve los parametros del horario (franja de oferta, almuerzo, duracion de
	 * la sesion, maximo de sesiones) para que el cliente arme la grilla con los
	 * mismos valores que usa el servidor.
	 */
	@GetMapping("/parametros")
	public ResponseEntity<?> getParametros() {
		return ResponseEntity.ok(reglasServ.getParametros());
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody BloqueDTO dto) {
		Exceptions exValidator = new Exceptions();
		if (!exValidator.isSafeText(dto.getCodigo())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "El codigo del bloque es obligatorio y no puede tener caracteres especiales",
							"success", false));
		}
		try {
			BloqueDTO creado = bloqueServ.create(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(creado);
		} catch (Exceptions.ConflictException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage(), "success", false));
		} catch (Exceptions.ValidationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", e.getMessage(), "success", false));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "Error interno al crear el bloque", "success", false));
		}
	}

	@GetMapping
	public ResponseEntity<?> getAll() {
		try {
			return ResponseEntity.ok(bloqueServ.findAll());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "Error al obtener los bloques", "success", false));
		}
	}

	@GetMapping("/{id:\\d+}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(bloqueServ.findById(id));
		} catch (Exceptions.ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", e.getMessage(), "success", false));
		}
	}

	@GetMapping("/dia/{dia}")
	public ResponseEntity<?> getByDia(@PathVariable String dia) {
		try {
			Dia d = Dia.valueOf(dia.trim().toUpperCase());
			return ResponseEntity.ok(bloqueServ.findByDia(d));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "El dia '" + dia + "' no es valido", "success", false));
		}
	}

	@GetMapping("/count")
	public ResponseEntity<?> countAll() {
		Long count = bloqueServ.count();
		if (count == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(count);
		}
		return ResponseEntity.ok(count);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateById(@PathVariable Long id, @RequestBody BloqueDTO dto) {
		Exceptions exValidator = new Exceptions();
		if (dto.getCodigo() != null && !exValidator.isSafeText(dto.getCodigo())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", "El codigo contiene caracteres no permitidos", "success", false));
		}
		try {
			bloqueServ.updateById(id, dto);
			return ResponseEntity.ok(Map.of("message", "Bloque actualizado correctamente", "success", true));
		} catch (Exceptions.ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", e.getMessage(), "success", false));
		} catch (Exceptions.ConflictException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage(), "success", false));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", e.getMessage(), "success", false));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@PathVariable Long id) {
		try {
			bloqueServ.deleteById(id);
			return ResponseEntity.ok(Map.of("message", "Bloque eliminado con exito", "success", true));
		} catch (Exceptions.ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", e.getMessage(), "success", false));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", e.getMessage(), "success", false));
		}
	}

	/**
	 * Mueve un bloque a otro dia y hora. Responde 200 con el bloque nuevo si se
	 * aplico, o 409 con el bloque vigente si otro usuario lo movio antes.
	 */
	@PostMapping("/mover")
	public ResponseEntity<?> mover(@RequestBody MoverBloqueDTO dto) {
		try {
			ResultadoMovimientoDTO resultado = bloqueServ.moverSesion(dto);
			if (resultado.isAplicado()) {
				return ResponseEntity.ok(resultado);
			}
			return ResponseEntity.status(HttpStatus.CONFLICT).body(resultado);
		} catch (Exceptions.ValidationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("message", e.getMessage(), "success", false));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "Error interno al mover el bloque", "success", false));
		}
	}
}
