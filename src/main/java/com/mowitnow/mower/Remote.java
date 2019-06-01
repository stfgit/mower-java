package com.mowitnow.mower;

import java.io.FileNotFoundException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.mowitnow.mower.engine.Lawn;
import com.mowitnow.mower.engine.Mower;

public class Remote {
	private static final Logger LOGGER = LoggerFactory.getLogger(Remote.class);

	public Remote() {
		Lawn.GRID.reset(); // Ugly FIXME
	}

	public void executeCommands(final String filePath) throws FileNotFoundException {
		final Scanner scanner = new Scanner(ResourceUtils.getFile(filePath));
		
		try {
			// Prepare Lawn
			final int topRightX = scanner.nextInt();
			final int topRightY = scanner.nextInt();
			
			prepareLawn(topRightX, topRightY);
			
			scanner.nextLine();
			
			// Mowers control
			int x, y;
			char pointer;
			String commands;
			for(;;) {
				x = scanner.nextInt();
				y = scanner.nextInt();
				pointer = (char) scanner.next().trim().charAt(0);
				
				scanner.nextLine();
				commands = scanner.nextLine();
				
				startMower(x, y, pointer).execute(commands);
					
				if (!scanner.hasNextLine()) {
					break;
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Badly formatted file: " + filePath, exception);
			throw exception;
		} finally {
			scanner.close();
		}
	}

	public void prepareLawn(int topRightX, int topRightY) {
		LOGGER.debug("Prepare lawn. topRightX=" + topRightX + " topRightY=" + topRightY);
		Lawn.GRID.prepare(topRightX, topRightY);
	}
	
	public Mower startMower(int x, int y, char pointer) {
		LOGGER.debug("Start mower. x=" + x + " y=" + y + " pointer=" + pointer);
		return new Mower(x, y, pointer);
	}
	
}
