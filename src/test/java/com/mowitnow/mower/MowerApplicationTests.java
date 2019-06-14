package com.mowitnow.mower;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mowitnow.mower.engine.Mower;
import com.mowitnow.mower.engine.Remote;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MowerApplicationTests {

  @Autowired
  private Remote remote;    

  @Test
  public final void testMowerStart() {
    remote.prepareMowing(5, 5);
    assertNotNull(remote.startMower(1, 1, 'N'));
    try {
      remote.startMower(6, 1, 'N');
      fail();
    } catch (IllegalStateException e) {
      // Fine
    }

  }

  @Test
  public final void testExecuteCommands() {
    List<Mower> mowers;

    try {
      remote.executeCommands(UUID.randomUUID().toString());
      fail();
    } catch (IOException ioException) {
      // Fine
    }
    try {
      remote.executeCommands("commandsBad.txt");
      fail();
    } catch (InputMismatchException exception) {
      // Fine
    } catch (IOException exception) {
      fail();
    }
    try {
      mowers = remote.executeCommands("commandsOK.txt");

      assertEquals(2, mowers.size());

      assertEquals(1, mowers.get(0).getX());
      assertEquals(3, mowers.get(0).getY());
      assertEquals('N', mowers.get(0).getPointer());

      assertEquals(5, mowers.get(1).getX());
      assertEquals(1, mowers.get(1).getY());
      assertEquals('E', mowers.get(1).getPointer());

    } catch (InputMismatchException exception) {
      fail();
    } catch (IOException exception) {
      fail();
    }
    try {
      mowers = remote.executeCommands("commandsOutOfLawn.txt");

      assertEquals(2, mowers.size());

      assertEquals(1, mowers.get(0).getX());
      assertEquals(5, mowers.get(0).getY());
      assertEquals('N', mowers.get(0).getPointer());

      assertEquals(5, mowers.get(1).getX());
      assertEquals(1, mowers.get(1).getY());
      assertEquals('E', mowers.get(1).getPointer());

    } catch (InputMismatchException exception) {
      fail();
    } catch (IOException exception) {
      fail();
    }
  }

  @Test
  public void testExecuteXebiaTest() {
    List<Mower> mowers;
    try {
      mowers = remote.executeCommands("commandsXebiaTest.txt");

      assertEquals(2, mowers.size());

      assertEquals(1, mowers.get(0).getX());
      assertEquals(3, mowers.get(0).getY());
      assertEquals('N', mowers.get(0).getPointer());

      assertEquals(5, mowers.get(1).getX());
      assertEquals(1, mowers.get(1).getY());
      assertEquals('E', mowers.get(1).getPointer());

    } catch (InputMismatchException exception) {
      fail();
    } catch (IOException exception) {
      fail();
    }
  }

}
