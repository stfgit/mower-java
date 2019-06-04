package com.mowitnow.mower.engine;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class Remote {
	private static final Logger LOGGER = LoggerFactory.getLogger(Remote.class);

	@Autowired
	private Lawn lawn;
	private List<Mower> mowers = new ArrayList<Mower>();
	
	public Remote() {
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
			LOGGER.error("Failed to read file: {}", filePath);
			throw exception;
		} finally {
			scanner.close();
		}
	}

	public void prepareLawn(int topRightX, int topRightY) {
		mowers.clear();
		lawn.prepare(topRightX, topRightY);
	}
	
	public Mower startMower(int x, int y, char pointer) {
		Mower mower = new Mower(lawn);
		mowers.add(mower);
		return mower.start(x, y, pointer);
	}
	
	public void displayState() {
		LOGGER.info(lawn.toString());
		LOGGER.info("Active mowers: ");
		for (final Mower mower : mowers) {
			LOGGER.info(mower.toString());
		}
	}

	public List<Mower> getMowers() {
		return mowers;
	}

	@Override
	public String toString() {
		return "Remote [lawn=" + lawn + ", mowers=" + mowers + "]";
	}
	
}
