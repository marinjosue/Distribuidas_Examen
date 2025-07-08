package com.eval.conjunta.sensor.controller;

import com.eval.conjunta.sensor.dto.SensorReadingDto;
import com.eval.conjunta.sensor.service.SensorReadingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SensorReadingService.class)
public class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorReadingService sensorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateSensorReadingWithValidData() throws Exception {
        SensorReadingDto dto = new SensorReadingDto();
        dto.setSensorId("S001");
        dto.setType("temperature");
        dto.setValue(25.5);
        dto.setTimestamp(Instant.now());

        mockMvc.perform(post("/sensor-readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateSensorReadingWithInvalidData() throws Exception {
        SensorReadingDto dto = new SensorReadingDto();
        // Missing required fields
        dto.setValue(25.5);

        mockMvc.perform(post("/sensor-readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
