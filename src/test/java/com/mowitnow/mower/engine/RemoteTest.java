package com.mowitnow.mower.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mowitnow.mower.Remote;
import com.mowitnow.mower.engine.Lawn;

public class RemoteTest {

	private Remote remote;
	
    @Before
    public void reset() {
    	remote = new Remote();
    }

	@Test
	public void testExecuteCommands() {
		try {
			remote.executeCommands(UUID.randomUUID().toString());
			fail();
		} catch (FileNotFoundException fileNotFoundException) {
			// Fine
		}
		try {
			remote.executeCommands("classpath:commandsBad.txt");
			fail();
		} catch (Exception exception) {
			// Fine
		}
		try {
			remote.executeCommands("classpath:commands.txt");
			Lawn.GRID.display();
			
			assertEquals(2, Lawn.GRID.getMowers().size());
			
			assertEquals(1, Lawn.GRID.getMowers().get(0).getX());
			assertEquals(3, Lawn.GRID.getMowers().get(0).getY());
			assertEquals('N', Lawn.GRID.getMowers().get(0).getCompass().getPointer());
			
			assertEquals(5, Lawn.GRID.getMowers().get(1).getX());
			assertEquals(1, Lawn.GRID.getMowers().get(1).getY());
			assertEquals('E', Lawn.GRID.getMowers().get(1).getCompass().getPointer());

		} catch (Exception exception) {
			fail();
		}
	}

	@Test
	public void testPrepareLawn() {
		try {
			Lawn.GRID.checkAccess(0, 0);
			fail();
		} catch (IllegalStateException illegalStateException) {
			// Fine, Lawn grid not initialized
		}

		remote.prepareLawn(5, 5);
		assertEquals(6, Lawn.GRID.getWidth());
		assertEquals(6, Lawn.GRID.getHeight());
		assertTrue(Lawn.GRID.checkAccess(5, 5));
		assertFalse(Lawn.GRID.checkAccess(6, 6));
	}

	@Test
	public void testStartMower() {
		try {
			remote.startMower(1, 1, 'N');
			fail();
		} catch (Exception exception) {
			// Fine, Lawn grid not initialized
		}
		remote.prepareLawn(5, 5);
		assertNotNull(remote.startMower(1, 1, 'N'));
	}

}
