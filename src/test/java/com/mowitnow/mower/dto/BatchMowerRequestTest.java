package com.mowitnow.mower.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class BatchMowerRequestTest {

    @Test
    void testDefaultConstructor() {
        BatchMowerRequest request = new BatchMowerRequest();
        
        assertThat(request.getLawnDimensions()).isNull();
        assertThat(request.getMowers()).isNull();
    }

    @Test
    void testParameterizedConstructor() {
        String lawnDimensions = "5 5";
        List<MowerCommandRequest> mowers = Arrays.asList(
            new MowerCommandRequest("5 5", "1 2 N", "GAGAGAGAA"),
            new MowerCommandRequest("5 5", "3 3 E", "AADAADADDA")
        );
        
        BatchMowerRequest request = new BatchMowerRequest(lawnDimensions, mowers);
        
        assertThat(request.getLawnDimensions()).isEqualTo(lawnDimensions);
        assertThat(request.getMowers()).hasSize(2);
        assertThat(request.getMowers().get(0).getMowerPosition()).isEqualTo("1 2 N");
        assertThat(request.getMowers().get(1).getMowerPosition()).isEqualTo("3 3 E");
    }

    @Test
    void testSettersAndGetters() {
        BatchMowerRequest request = new BatchMowerRequest();
        
        request.setLawnDimensions("10 8");
        
        List<MowerCommandRequest> mowers = Arrays.asList(
            new MowerCommandRequest("10 8", "0 0 N", "AAA"),
            new MowerCommandRequest("10 8", "5 5 S", "DDD")
        );
        request.setMowers(mowers);
        
        assertThat(request.getLawnDimensions()).isEqualTo("10 8");
        assertThat(request.getMowers()).hasSize(2);
        assertThat(request.getMowers().get(0).getCommands()).isEqualTo("AAA");
        assertThat(request.getMowers().get(1).getCommands()).isEqualTo("DDD");
    }

    @Test
    void testEmptyMowersList() {
        BatchMowerRequest request = new BatchMowerRequest("3 3", Arrays.asList());
        
        assertThat(request.getLawnDimensions()).isEqualTo("3 3");
        assertThat(request.getMowers()).isEmpty();
    }

    @Test
    void testSingleMowerInBatch() {
        MowerCommandRequest singleMower = new MowerCommandRequest("7 7", "2 2 W", "GDGD");
        BatchMowerRequest request = new BatchMowerRequest("7 7", Arrays.asList(singleMower));
        
        assertThat(request.getMowers()).hasSize(1);
        assertThat(request.getMowers().get(0).getMowerPosition()).isEqualTo("2 2 W");
        assertThat(request.getMowers().get(0).getCommands()).isEqualTo("GDGD");
    }
}