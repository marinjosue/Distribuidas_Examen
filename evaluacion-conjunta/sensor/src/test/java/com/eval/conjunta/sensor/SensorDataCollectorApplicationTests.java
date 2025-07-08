package com.eval.conjunta.sensor;

import com.eval.conjunta.sensor.dto.SensorReadingDto;
import com.eval.conjunta.sensor.service.SensorValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672"
})
class SensorDataCollectorApplicationTests {

	@Autowired
	private SensorValidationService validationService;

	@Test
	void contextLoads() {
		// Verificar que el contexto de Spring se carga correctamente
	}

	@Test
	void testTemperatureValidation() {
		// Temperatura válida
		validationService.validateSensorReading("temperature", 25.0);

		// Temperatura fuera de rango
		assertThrows(IllegalArgumentException.class, () -> 
			validationService.validateSensorReading("temperature", 70.0));

		assertThrows(IllegalArgumentException.class, () -> 
			validationService.validateSensorReading("temperature", -60.0));
	}

	@Test
	void testHumidityValidation() {
		// Humedad válida
		validationService.validateSensorReading("humidity", 50.0);

		// Humedad fuera de rango
		assertThrows(IllegalArgumentException.class, () -> 
			validationService.validateSensorReading("humidity", 150.0));

		assertThrows(IllegalArgumentException.class, () -> 
			validationService.validateSensorReading("humidity", -10.0));
	}

	@Test
	void testPressureValidation() {
		// Presión válida
		validationService.validateSensorReading("pressure", 1013.25);

		// Presión fuera de rango
		assertThrows(IllegalArgumentException.class, () -> 
			validationService.validateSensorReading("pressure", 1300.0));

		assertThrows(IllegalArgumentException.class, () -> 
			validationService.validateSensorReading("pressure", 500.0));
	}

	@Test
	void testSensorReadingDtoCreation() {
		SensorReadingDto dto = new SensorReadingDto();
		dto.setSensorId("S001");
		dto.setType("temperature");
		dto.setValue(25.5);
		dto.setTimestamp(Instant.now());

		// Verificar que los valores se establecen correctamente
		assert dto.getSensorId().equals("S001");
		assert dto.getType().equals("temperature");
		assert dto.getValue() == 25.5;
		assert dto.getTimestamp() != null;
	}
}
