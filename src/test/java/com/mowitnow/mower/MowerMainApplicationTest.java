package com.mowitnow.mower;

import com.mowitnow.mower.engine.Remote;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MowerMainApplicationTest {

    @Mock
    private Remote remote;

    @InjectMocks
    private MowerApplication mowerApplication;

    @Test
    void testRunWithNoArguments() throws Exception {
        mowerApplication.run();
        
        verify(remote).executeCommands("commandsTest.txt");
    }

    @Test
    void testRunWithArguments() throws Exception {
        String[] args = {"/path/to/custom/file.txt"};
        
        mowerApplication.run(args);
        
        verify(remote).executeCommands("file:/path/to/custom/file.txt");
    }

    @Test
    void testRunWithIOException() throws Exception {
        doThrow(new IOException("File not found")).when(remote).executeCommands(anyString());
        
        // Should not throw exception, just log error
        mowerApplication.run();
        
        verify(remote).executeCommands("commandsTest.txt");
    }

    @Test
    void testMainMethod() {
        // Test that main method can be called without exception
        // This is primarily for coverage
        String[] args = {};
        
        // We can't easily test the main method without starting the full Spring context,
        // but we can at least ensure the method exists and is callable
        try {
            // Just verify the main method exists and can be invoked via reflection
            MowerApplication.class.getDeclaredMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Main method should exist", e);
        }
    }

    @Test
    void testRunWithMultipleArguments() throws Exception {
        String[] args = {"/first/file.txt", "ignored", "arguments"};
        
        mowerApplication.run(args);
        
        // Only first argument should be used
        verify(remote).executeCommands("file:/first/file.txt");
    }

    @Test
    void testRunWithEmptyStringArgument() throws Exception {
        String[] args = {""};
        
        mowerApplication.run(args);
        
        verify(remote).executeCommands("file:");
    }
}