package com.mowitnow.mower.controller;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class WebControllerTest {

    @Test
    void testIndexMethod() {
        WebController controller = new WebController();
        String result = controller.index();
        
        assertThat(result).isEqualTo("forward:/index.html");
    }
}