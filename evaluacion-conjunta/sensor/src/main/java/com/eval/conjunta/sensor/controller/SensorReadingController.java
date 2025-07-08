package com.eval.conjunta.sensor.controller;

import com.eval.conjunta.sensor.dto.SensorReadingDto;
import com.eval.conjunta.sensor.model.SensorReading;
import com.eval.conjunta.sensor.service.SensorReadingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sensor-readings")
@CrossOrigin(origins = "*")
public class SensorReadingController {

    private static final Logger logger = LoggerFactory.getLogger(SensorReadingController.class);

    @Autowired
    private SensorReadingService sensorReadingService;

    /**
     * POST /sensor-readings: Recibe datos de sensores en formato JSON
     */
    @PostMapping
    public ResponseEntity<SensorReading> createSensorReading(@Valid @RequestBody SensorReadingDto sensorReadingDto) {
        try {
            logger.info("Received sensor reading request for sensor: {}", sensorReadingDto.getSensorId());
            SensorReading savedReading = sensorReadingService.saveSensorReading(sensorReadingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReading);
        } catch (Exception e) {
            logger.error("Error creating sensor reading: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /sensor-readings/{sensorId}: Devuelve historial de lecturas de un sensor
     */
    @GetMapping("/{sensorId}")
    public ResponseEntity<List<SensorReading>> getSensorReadingHistory(@PathVariable String sensorId) {
        try {
            logger.info("Retrieving sensor reading history for sensor: {}", sensorId);
            List<SensorReading> readings = sensorReadingService.getSensorReadingHistory(sensorId);
            if (readings.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(readings);
        } catch (Exception e) {
            logger.error("Error retrieving sensor reading history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /sensor-readings: Obtiene todas las lecturas de sensores
     */
    @GetMapping
    public ResponseEntity<List<SensorReading>> getAllSensorReadings() {
        try {
            logger.info("Retrieving all sensor readings");
            List<SensorReading> readings = sensorReadingService.getAllSensorReadings();
            if (readings.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(readings);
        } catch (Exception e) {
            logger.error("Error retrieving all sensor readings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /sensor-readings/{sensorId}/latest: Obtiene la lectura más reciente de un sensor
     */
    @GetMapping("/{sensorId}/latest")
    public ResponseEntity<SensorReading> getLatestSensorReading(@PathVariable String sensorId) {
        try {
            logger.info("Retrieving latest sensor reading for sensor: {}", sensorId);
            SensorReading latestReading = sensorReadingService.getLatestSensorReading(sensorId);
            if (latestReading == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(latestReading);
        } catch (Exception e) {
            logger.error("Error retrieving latest sensor reading: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /sensor-readings/{sensorId}/count: Cuenta las lecturas de un sensor
     */
    @GetMapping("/{sensorId}/count")
    public ResponseEntity<Long> countSensorReadings(@PathVariable String sensorId) {
        try {
            logger.info("Counting sensor readings for sensor: {}", sensorId);
            Long count = sensorReadingService.countSensorReadings(sensorId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error counting sensor readings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /sensor-readings/{sensorId}/range: Obtiene lecturas en un rango de tiempo
     */
    @GetMapping("/{sensorId}/range")
    public ResponseEntity<List<SensorReading>> getSensorReadingsByTimeRange(
            @PathVariable String sensorId,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime) {
        try {
            logger.info("Retrieving sensor readings for sensor: {} in time range", sensorId);
            Instant start = Instant.parse(startTime);
            Instant end = Instant.parse(endTime);
            List<SensorReading> readings = sensorReadingService.getSensorReadingsByTimeRange(sensorId, start, end);
            if (readings.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(readings);
        } catch (Exception e) {
            logger.error("Error retrieving sensor readings by time range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /sensor-readings/type/{type}: Obtiene lecturas por tipo de sensor
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<SensorReading>> getSensorReadingsByType(@PathVariable String type) {
        try {
            logger.info("Retrieving sensor readings by type: {}", type);
            List<SensorReading> readings = sensorReadingService.getSensorReadingsByType(type);
            if (readings.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(readings);
        } catch (Exception e) {
            logger.error("Error retrieving sensor readings by type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /sensor-readings/{id}: Elimina una lectura específica
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensorReading(@PathVariable Long id) {
        try {
            logger.info("Deleting sensor reading with ID: {}", id);
            Optional<SensorReading> existingReading = sensorReadingService.getSensorReadingById(id);
            if (existingReading.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            sensorReadingService.deleteSensorReading(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting sensor reading: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
