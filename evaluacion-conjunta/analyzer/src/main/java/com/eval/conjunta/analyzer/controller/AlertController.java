package com.eval.conjunta.analyzer.controller;

import com.eval.conjunta.analyzer.model.Alert;
import com.eval.conjunta.analyzer.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@CrossOrigin(origins = "*")
public class AlertController {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);
    
    @Autowired
    private AlertService alertService;
    
    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {
        logger.info("Obteniendo todas las alertas");
        try {
            List<Alert> alerts = alertService.getAllAlerts();
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            logger.error("Error al obtener alertas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<List<Alert>> getAlertsBySensor(@PathVariable String sensorId) {
        logger.info("Obteniendo alertas para el sensor: {}", sensorId);
        try {
            List<Alert> alerts = alertService.getAlertsBySensor(sensorId);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            logger.error("Error al obtener alertas del sensor {}: {}", sensorId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Alert>> getAlertsByType(@PathVariable String type) {
        logger.info("Obteniendo alertas por tipo: {}", type);
        try {
            List<Alert> alerts = alertService.getAlertsByType(type);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            logger.error("Error al obtener alertas por tipo {}: {}", type, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<Alert>> getAlertsBySeverity(@PathVariable String severity) {
        logger.info("Obteniendo alertas por severidad: {}", severity);
        try {
            List<Alert> alerts = alertService.getAlertsBySeverity(severity);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            logger.error("Error al obtener alertas por severidad {}: {}", severity, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/critical")
    public ResponseEntity<List<Alert>> getCriticalAlerts() {
        logger.info("Obteniendo alertas críticas");
        try {
            List<Alert> alerts = alertService.getCriticalAlerts();
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            logger.error("Error al obtener alertas críticas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
