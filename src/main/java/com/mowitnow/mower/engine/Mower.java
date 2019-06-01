package com.mowitnow.mower.engine;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Mower {
	private static final Logger LOGGER = LoggerFactory.getLogger(Mower.class);

	
	private int x, y;
	private Compass compass;
	private String name;

	public Mower(int x, int y, char pointer) {
		this(x, y, Compass.byPointer(pointer), UUID.randomUUID().toString());
	}

	public Mower(int x, int y, Compass compass, String name) {
		if (Lawn.GRID.checkAccess(x, y)) {
			this.x = x;
			this.y = y;
			this.compass = compass;
			this.name = name;
			Lawn.GRID.addMower(this);
			LOGGER.debug("Mower started: name=" + name);
		} else {
			throw new IllegalStateException("Bad start position: x=" + x + ", y=" + x);
		}
	}

	private void left() {
		compass = compass.left();
		LOGGER.debug("Mower turned left: " + toString());
	}
	
	private void right() {
		compass = compass.right();
		LOGGER.debug("Mower turned right: " + toString());
	}
	
	private boolean forward() {
		final int toX = x + compass.getDeltaX();
		final int toY = y + compass.getDeltaY();
		
		if (Lawn.GRID.checkAccess(toX, toY)) {
			x = toX;
			y = toY;
			LOGGER.debug("Mower moved: " + toString());
			return true;
		} else {
			LOGGER.warn("Mower blocked: " + toString());
			return false;
		}
		
	}

	public void execute(String commands) {
		for (char command : commands.toCharArray()) {
			LOGGER.debug("Mower " + name + " received '" + command + "'");
			switch (command) {
			case 'G':
				left();
				break;
			case 'D':
				right();
				break;
			case 'A':
				forward();
				break;
			default:
				LOGGER.warn("Unknown command: '" + command + "'");
			}
		}
	}

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Compass getCompass() {
		return compass;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Mower: [name=" + name + ", x=" + x + ", y=" + y + ", compass=" + compass.toString() + "]";
	}

}
