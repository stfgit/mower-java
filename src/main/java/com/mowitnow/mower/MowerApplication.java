package com.mowitnow.mower;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mowitnow.mower.engine.Lawn;

@SpringBootApplication
public class MowerApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(MowerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MowerApplication.class, args);
		
		Remote remote = new Remote();
		try {
			remote.executeCommands("classpath:commands.txt");
			Lawn.GRID.display();
			
		} catch (FileNotFoundException e) {
			LOGGER.error("File doesn't exist.", e);
		}
	}

}
