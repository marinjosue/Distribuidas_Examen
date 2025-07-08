package com.eval.conjunta.sensor.repository;

import com.eval.conjunta.sensor.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    
    /**
     * Encuentra todas las lecturas de un sensor específico ordenadas por timestamp descendente
     */
    List<SensorReading> findBySensorIdOrderByTimestampDesc(String sensorId);
    
    /**
     * Encuentra todas las lecturas de un sensor específico en un rango de tiempo
     */
    @Query("SELECT sr FROM SensorReading sr WHERE sr.sensorId = :sensorId AND sr.timestamp BETWEEN :startTime AND :endTime ORDER BY sr.timestamp DESC")
    List<SensorReading> findBySensorIdAndTimestampBetween(
            @Param("sensorId") String sensorId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );
    
    /**
     * Encuentra todas las lecturas de un tipo específico de sensor
     */
    List<SensorReading> findByTypeOrderByTimestampDesc(String type);
    
    /**
     * Encuentra la lectura más reciente de un sensor
     */
    @Query("SELECT sr FROM SensorReading sr WHERE sr.sensorId = :sensorId ORDER BY sr.timestamp DESC LIMIT 1")
    SensorReading findLatestBySensorId(@Param("sensorId") String sensorId);
    
    /**
     * Cuenta el número total de lecturas por sensor
     */
    @Query("SELECT COUNT(sr) FROM SensorReading sr WHERE sr.sensorId = :sensorId")
    Long countBySensorId(@Param("sensorId") String sensorId);
}
