package com.mowitnow.mower.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MowerResponseTest {

    @Test
    void testDefaultConstructor() {
        MowerResponse response = new MowerResponse();
        
        assertThat(response.getMowerId()).isNull();
        assertThat(response.getX()).isEqualTo(0);
        assertThat(response.getY()).isEqualTo(0);
        assertThat(response.getDirection()).isEqualTo('\u0000');
        assertThat(response.getPosition()).isNull();
    }

    @Test
    void testParameterizedConstructor() {
        String mowerId = "test-mower-123";
        int x = 3;
        int y = 4;
        char direction = 'N';
        
        MowerResponse response = new MowerResponse(mowerId, x, y, direction);
        
        assertThat(response.getMowerId()).isEqualTo(mowerId);
        assertThat(response.getX()).isEqualTo(x);
        assertThat(response.getY()).isEqualTo(y);
        assertThat(response.getDirection()).isEqualTo(direction);
        assertThat(response.getPosition()).isEqualTo("3 4 N");
    }

    @Test
    void testSettersAndGetters() {
        MowerResponse response = new MowerResponse();
        
        response.setMowerId("mower-456");
        response.setX(7);
        response.setY(2);
        response.setDirection('E');
        response.setPosition("7 2 E");
        
        assertThat(response.getMowerId()).isEqualTo("mower-456");
        assertThat(response.getX()).isEqualTo(7);
        assertThat(response.getY()).isEqualTo(2);
        assertThat(response.getDirection()).isEqualTo('E');
        assertThat(response.getPosition()).isEqualTo("7 2 E");
    }

    @Test
    void testPositionFormatting() {
        MowerResponse response = new MowerResponse("test", 1, 3, 'S');
        
        assertThat(response.getPosition()).isEqualTo("1 3 S");
    }

    @Test
    void testAllDirections() {
        MowerResponse north = new MowerResponse("n", 0, 0, 'N');
        MowerResponse east = new MowerResponse("e", 1, 1, 'E');
        MowerResponse south = new MowerResponse("s", 2, 2, 'S');
        MowerResponse west = new MowerResponse("w", 3, 3, 'W');
        
        assertThat(north.getPosition()).isEqualTo("0 0 N");
        assertThat(east.getPosition()).isEqualTo("1 1 E");
        assertThat(south.getPosition()).isEqualTo("2 2 S");
        assertThat(west.getPosition()).isEqualTo("3 3 W");
    }
}