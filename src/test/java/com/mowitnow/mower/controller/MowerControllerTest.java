package com.mowitnow.mower.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mowitnow.mower.dto.BatchMowerRequest;
import com.mowitnow.mower.dto.MowerCommandRequest;
import com.mowitnow.mower.engine.Mower;
import com.mowitnow.mower.engine.Remote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MowerController.class)
class MowerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Remote remote;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/mower/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Mower service is running"));
    }

    @Test
    void testExecuteSingleMowerSuccess() throws Exception {
        // Préparation du mock
        Mower mockMower = mock(Mower.class);
        when(mockMower.getX()).thenReturn(1);
        when(mockMower.getY()).thenReturn(3);
        when(mockMower.getPointer()).thenReturn('N');
        when(mockMower.toString()).thenReturn("Mower: [name=test-mower-id, x=1, y=3, compass=Compass [N]]");
        when(mockMower.execute(anyString())).thenReturn(mockMower);
        
        when(remote.startMower(anyInt(), anyInt(), anyChar())).thenReturn(mockMower);
        
        // Préparation de la requête
        MowerCommandRequest request = new MowerCommandRequest("5 5", "1 2 N", "GAGAGAGAA");
        String requestJson = objectMapper.writeValueAsString(request);
        
        // Test
        mockMvc.perform(post("/api/mower/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(1))
                .andExpect(jsonPath("$.y").value(3))
                .andExpect(jsonPath("$.direction").value("N"))
                .andExpect(jsonPath("$.position").value("1 3 N"))
                .andExpect(jsonPath("$.mowerId").value("test-mower-id"));
        
        // Vérifications
        verify(remote).prepareMowing(5, 5);
        verify(remote).startMower(1, 2, 'N');
        verify(mockMower).execute("GAGAGAGAA");
    }

    @Test
    void testExecuteSingleMowerBadRequest() throws Exception {
        // Requête avec données invalides
        String invalidRequest = "{\"invalid\": \"data\"}";
        
        mockMvc.perform(post("/api/mower/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testExecuteSingleMowerWithException() throws Exception {
        // Mock qui lève une exception
        when(remote.startMower(anyInt(), anyInt(), anyChar())).thenThrow(new RuntimeException("Test exception"));
        
        MowerCommandRequest request = new MowerCommandRequest("5 5", "1 2 N", "GAGAGAGAA");
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/mower/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testExecuteBatchMowersSuccess() throws Exception {
        // Préparation des mocks
        Mower mockMower1 = mock(Mower.class);
        when(mockMower1.getX()).thenReturn(1);
        when(mockMower1.getY()).thenReturn(3);
        when(mockMower1.getPointer()).thenReturn('N');
        when(mockMower1.toString()).thenReturn("Mower: [name=mower-1, x=1, y=3, compass=Compass [N]]");
        when(mockMower1.execute(anyString())).thenReturn(mockMower1);
        
        Mower mockMower2 = mock(Mower.class);
        when(mockMower2.getX()).thenReturn(5);
        when(mockMower2.getY()).thenReturn(1);
        when(mockMower2.getPointer()).thenReturn('E');
        when(mockMower2.toString()).thenReturn("Mower: [name=mower-2, x=5, y=1, compass=Compass [E]]");
        when(mockMower2.execute(anyString())).thenReturn(mockMower2);
        
        when(remote.startMower(1, 2, 'N')).thenReturn(mockMower1);
        when(remote.startMower(3, 3, 'E')).thenReturn(mockMower2);
        
        // Préparation de la requête batch
        BatchMowerRequest request = new BatchMowerRequest();
        request.setLawnDimensions("5 5");
        request.setMowers(Arrays.asList(
            new MowerCommandRequest(null, "1 2 N", "GAGAGAGAA"),
            new MowerCommandRequest(null, "3 3 E", "AADAADADDA")
        ));
        
        String requestJson = objectMapper.writeValueAsString(request);
        
        // Test
        mockMvc.perform(post("/api/mower/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].x").value(1))
                .andExpect(jsonPath("$[0].y").value(3))
                .andExpect(jsonPath("$[0].direction").value("N"))
                .andExpect(jsonPath("$[1].x").value(5))
                .andExpect(jsonPath("$[1].y").value(1))
                .andExpect(jsonPath("$[1].direction").value("E"));
        
        // Vérifications
        verify(remote).prepareMowing(5, 5);
        verify(remote).startMower(1, 2, 'N');
        verify(remote).startMower(3, 3, 'E');
        verify(mockMower1).execute("GAGAGAGAA");
        verify(mockMower2).execute("AADAADADDA");
    }

    @Test
    void testExecuteBatchMowersWithException() throws Exception {
        // Mock qui lève une exception
        when(remote.startMower(anyInt(), anyInt(), anyChar())).thenThrow(new RuntimeException("Batch test exception"));
        
        BatchMowerRequest request = new BatchMowerRequest();
        request.setLawnDimensions("5 5");
        request.setMowers(Arrays.asList(
            new MowerCommandRequest(null, "1 2 N", "GAGAGAGAA")
        ));
        
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/mower/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testExecuteBatchMowersEmptyList() throws Exception {
        BatchMowerRequest request = new BatchMowerRequest();
        request.setLawnDimensions("5 5");
        request.setMowers(Arrays.asList());
        
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/mower/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}