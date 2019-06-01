package com.mowitnow.mower.engine;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Lawn {
	GRID();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Lawn.class);

	private int width = -1;
	private int height = -1;
	private List<Mower> mowers = new ArrayList<Mower>();
	
	public void prepare(int topRightX, int topRightY) {
		width = topRightX + 1;
		height = topRightY + 1;
		mowers.clear();
		LOGGER.debug("Lawn grid initialized.");
	}
	
	public void reset() {
		width = -1;
		height = -1;
		mowers.clear();
	}
	
	void addMower(final Mower mower) {
		mowers.add(mower);
	}
	
	public boolean checkAccess(int x, int y) {
		if (!isInitialized()) {
			LOGGER.error("Lawn grid not initialized.");
			throw new IllegalStateException("Lawn grid not initialized.");
		}
		return ((x < width) && (y < height));
	}
	
	public void display() {
		LOGGER.info("Lawn grid: " + width + "x" + height);
		LOGGER.info("Lawn mowers: ");
		for (final Mower mower : mowers) {
			LOGGER.info(mower.toString());
		}
	}
	
	private boolean isInitialized() {
		return((width > 0) && (height > 0));
	}

	public List<Mower> getMowers() {
		return mowers;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
