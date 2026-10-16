package co.edu.unbosque.programacion_horarios;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProgramacionHorariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgramacionHorariosApplication.class, args);
	}
	@Bean
	ModelMapper getModelMapper() {
		return new ModelMapper();
	}
}
