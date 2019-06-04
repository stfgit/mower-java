package com.mowitnow.mower;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mowitnow.mower.engine.Remote;


@SpringBootApplication
public class MowerApplication implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(MowerApplication.class);
	
	@Autowired
	private Remote remote;

	@Override
	public void run(String... args) throws Exception {
		String commandsFilePath = "classpath:commands.txt";
		if (args.length > 0) {
			commandsFilePath = args[0];
		}
		try {
			remote.executeCommands(commandsFilePath);
			remote.displayState();
			
		} catch (FileNotFoundException e) {
			LOGGER.error("File doesn't exist.", e);
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(MowerApplication.class, args);
	}


}
