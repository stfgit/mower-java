package com.mowitnow.mower.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Lawn {	
	private static final Logger LOGGER = LoggerFactory.getLogger(Lawn.class);

	private int width = -1;
	private int height = -1;
	
	public Lawn() {
		// Empty
	}
	
	public void prepare(int topRightX, int topRightY) {
		LOGGER.debug("Prepare lawn. topRightX=" + topRightX + " topRightY=" + topRightY);
		width = topRightX + 1;
		height = topRightY + 1;
	}
	
	public boolean checkAccess(final int x, final int y) {
		return ((x < width) && (y < height));
	}
	

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "Lawn [width=" + width + ", height=" + height + "]";
	}
}
