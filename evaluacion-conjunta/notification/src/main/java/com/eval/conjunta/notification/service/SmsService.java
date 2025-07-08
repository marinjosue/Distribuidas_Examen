package com.eval.conjunta.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    
    @Value("${app.notification.sms.enabled:true}")
    private boolean smsEnabled;
    
    public boolean sendSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            logger.info("SMS deshabilitado, simulando envío a: {}", phoneNumber);
            return true;
        }
        
        try {
            logger.info("Enviando SMS a: {}", phoneNumber);
            
            // Simular envío de SMS
            logger.info("SMS simulado enviado exitosamente a: {}", phoneNumber);
            logger.info("Contenido del SMS: {}", message);
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error al enviar SMS a {}: {}", phoneNumber, e.getMessage(), e);
            return false;
        }
    }
}
