package com.mowitnow.mower.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MowerCommandRequestTest {

    @Test
    void testDefaultConstructor() {
        MowerCommandRequest request = new MowerCommandRequest();
        
        assertThat(request.getLawnDimensions()).isNull();
        assertThat(request.getMowerPosition()).isNull();
        assertThat(request.getCommands()).isNull();
    }

    @Test
    void testParameterizedConstructor() {
        String lawnDimensions = "5 5";
        String mowerPosition = "1 2 N";
        String commands = "GAGAGAGAA";
        
        MowerCommandRequest request = new MowerCommandRequest(lawnDimensions, mowerPosition, commands);
        
        assertThat(request.getLawnDimensions()).isEqualTo(lawnDimensions);
        assertThat(request.getMowerPosition()).isEqualTo(mowerPosition);
        assertThat(request.getCommands()).isEqualTo(commands);
    }

    @Test
    void testSetters() {
        MowerCommandRequest request = new MowerCommandRequest();
        
        request.setLawnDimensions("10 10");
        request.setMowerPosition("3 4 E");
        request.setCommands("AADAA");
        
        assertThat(request.getLawnDimensions()).isEqualTo("10 10");
        assertThat(request.getMowerPosition()).isEqualTo("3 4 E");
        assertThat(request.getCommands()).isEqualTo("AADAA");
    }

    @Test
    void testGetters() {
        MowerCommandRequest request = new MowerCommandRequest("7 7", "2 3 S", "DGAGA");
        
        assertThat(request.getLawnDimensions()).isEqualTo("7 7");
        assertThat(request.getMowerPosition()).isEqualTo("2 3 S");
        assertThat(request.getCommands()).isEqualTo("DGAGA");
    }
}