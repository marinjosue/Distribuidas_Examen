package com.eval.conjunta.sensor.service;

import com.eval.conjunta.sensor.dto.SensorReadingDto;
import com.eval.conjunta.sensor.model.SensorReading;
import com.eval.conjunta.sensor.repository.SensorReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SensorReadingService {

    private static final Logger logger = LoggerFactory.getLogger(SensorReadingService.class);

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    /**
     * Guarda una nueva lectura de sensor
     */
    public SensorReading saveSensorReading(SensorReadingDto sensorReadingDto) {
        logger.info("Saving sensor reading for sensor: {}", sensorReadingDto.getSensorId());
        
        SensorReading sensorReading = new SensorReading();
        sensorReading.setSensorId(sensorReadingDto.getSensorId());
        sensorReading.setType(sensorReadingDto.getType());
        sensorReading.setValue(sensorReadingDto.getValue());
        sensorReading.setTimestamp(sensorReadingDto.getTimestamp() != null ? 
                                  sensorReadingDto.getTimestamp() : Instant.now());
        
        SensorReading savedReading = sensorReadingRepository.save(sensorReading);
        logger.info("Sensor reading saved with ID: {}", savedReading.getId());
        
        return savedReading;
    }

    /**
     * Obtiene el historial de lecturas de un sensor específico
     */
    @Transactional(readOnly = true)
    public List<SensorReading> getSensorReadingHistory(String sensorId) {
        logger.info("Retrieving sensor reading history for sensor: {}", sensorId);
        return sensorReadingRepository.findBySensorIdOrderByTimestampDesc(sensorId);
    }

    /**
     * Obtiene todas las lecturas de sensores
     */
    @Transactional(readOnly = true)
    public List<SensorReading> getAllSensorReadings() {
        logger.info("Retrieving all sensor readings");
        return sensorReadingRepository.findAll();
    }

    /**
     * Obtiene una lectura específica por ID
     */
    @Transactional(readOnly = true)
    public Optional<SensorReading> getSensorReadingById(Long id) {
        logger.info("Retrieving sensor reading with ID: {}", id);
        return sensorReadingRepository.findById(id);
    }

    /**
     * Obtiene las lecturas de un sensor en un rango de tiempo
     */
    @Transactional(readOnly = true)
    public List<SensorReading> getSensorReadingsByTimeRange(String sensorId, Instant startTime, Instant endTime) {
        logger.info("Retrieving sensor readings for sensor: {} between {} and {}", 
                   sensorId, startTime, endTime);
        return sensorReadingRepository.findBySensorIdAndTimestampBetween(sensorId, startTime, endTime);
    }

    /**
     * Obtiene la lectura más reciente de un sensor
     */
    @Transactional(readOnly = true)
    public SensorReading getLatestSensorReading(String sensorId) {
        logger.info("Retrieving latest sensor reading for sensor: {}", sensorId);
        return sensorReadingRepository.findLatestBySensorId(sensorId);
    }

    /**
     * Cuenta el número total de lecturas de un sensor
     */
    @Transactional(readOnly = true)
    public Long countSensorReadings(String sensorId) {
        logger.info("Counting sensor readings for sensor: {}", sensorId);
        return sensorReadingRepository.countBySensorId(sensorId);
    }

    /**
     * Obtiene lecturas por tipo de sensor
     */
    @Transactional(readOnly = true)
    public List<SensorReading> getSensorReadingsByType(String type) {
        logger.info("Retrieving sensor readings by type: {}", type);
        return sensorReadingRepository.findByTypeOrderByTimestampDesc(type);
    }

    /**
     * Elimina una lectura específica
     */
    public void deleteSensorReading(Long id) {
        logger.info("Deleting sensor reading with ID: {}", id);
        sensorReadingRepository.deleteById(id);
    }
}
