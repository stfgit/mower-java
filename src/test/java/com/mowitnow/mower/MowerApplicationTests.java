package com.mowitnow.mower;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mowitnow.mower.engine.Remote;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MowerApplicationTests {

	@Autowired
	private Remote remote;
	
	@Test
	public void testStartMower() {
		remote.prepareLawn(5, 5);
		assertNotNull(remote.startMower(1, 1, 'N'));
		try {
			remote.startMower(6, 1, 'N');
			fail();
		} catch (IllegalStateException e) {
			// Fine
		}
			
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
		} catch (InputMismatchException exception) {
			// Fine
		} catch (FileNotFoundException exception) {
			fail();
		}
		try {
			remote.executeCommands("classpath:commandsOK.txt");
			remote.displayState();
			
			assertEquals(2, remote.getMowers().size());
			
			assertEquals(1, remote.getMowers().get(0).getX());
			assertEquals(3, remote.getMowers().get(0).getY());
			assertEquals('N', remote.getMowers().get(0).getPointer());
			
			assertEquals(5, remote.getMowers().get(1).getX());
			assertEquals(1, remote.getMowers().get(1).getY());
			assertEquals('E', remote.getMowers().get(1).getPointer());

		} catch (InputMismatchException exception) {
			fail();
		} catch (FileNotFoundException exception) {
			fail();
		}
		try {
			remote.executeCommands("classpath:commandsOutOfLawn.txt");
			remote.displayState();
			
			assertEquals(2, remote.getMowers().size());
			
			assertEquals(1, remote.getMowers().get(0).getX());
			assertEquals(5, remote.getMowers().get(0).getY());
			assertEquals('N', remote.getMowers().get(0).getPointer());
			
			assertEquals(5, remote.getMowers().get(1).getX());
			assertEquals(1, remote.getMowers().get(1).getY());
			assertEquals('E', remote.getMowers().get(1).getPointer());

		} catch (InputMismatchException exception) {
			fail();
		} catch (FileNotFoundException exception) {
			fail();
		}
		try {
			remote.executeCommands("classpath:commands.txt");
			remote.displayState();
			
			assertEquals(2, remote.getMowers().size());
			
			assertEquals(1, remote.getMowers().get(0).getX());
			assertEquals(3, remote.getMowers().get(0).getY());
			assertEquals('N', remote.getMowers().get(0).getPointer());
			
			assertEquals(5, remote.getMowers().get(1).getX());
			assertEquals(1, remote.getMowers().get(1).getY());
			assertEquals('E', remote.getMowers().get(1).getPointer());

		} catch (InputMismatchException exception) {
			fail();
		} catch (FileNotFoundException exception) {
			fail();
		}
	}

}
