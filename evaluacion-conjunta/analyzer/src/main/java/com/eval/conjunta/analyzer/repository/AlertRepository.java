package com.eval.conjunta.analyzer.repository;

import com.eval.conjunta.analyzer.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    List<Alert> findBySensorIdOrderByTimestampDesc(String sensorId);
    
    List<Alert> findByTypeOrderByTimestampDesc(String type);
    
    List<Alert> findBySeverityOrderByTimestampDesc(String severity);
    
    @Query("SELECT a FROM Alert a WHERE a.timestamp >= :startTime AND a.timestamp <= :endTime ORDER BY a.timestamp DESC")
    List<Alert> findByTimestampBetween(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.sensorId = :sensorId AND a.timestamp >= :startTime")
    long countBySensorIdAndTimestampAfter(@Param("sensorId") String sensorId, @Param("startTime") Instant startTime);
    
    @Query("SELECT a FROM Alert a WHERE a.severity = 'CRITICAL' AND a.timestamp >= :startTime ORDER BY a.timestamp DESC")
    List<Alert> findCriticalAlertsAfter(@Param("startTime") Instant startTime);
}
