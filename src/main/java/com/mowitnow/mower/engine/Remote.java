package com.mowitnow.mower.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public final class Remote {
  private static final Logger LOGGER = LoggerFactory.getLogger(Remote.class);

  @Autowired
  private Lawn lawn;

  public Remote() {
    // Empty
  }

  /**
   * Execute commands from file (file system or resources).
   * @param filePath The file path if it's in the jar resources or a "file:" URL
   *                 if it's on the file system. ("commandsXebiaTest.txt" or
   *                 "file:/tmp/commandsXebiaTest.txt")
   * @return List of started mowers.
   * @throws IOException
   * File opening issue.
   */
  public List<Mower> executeCommands(final String filePath) throws IOException {
    List<Mower> mowers = new ArrayList<Mower>();
    Scanner scanner = null;

    LOGGER.info("======== Executing commands file: {} =========", filePath);
    try {
      ClassPathResource classPathResource = new ClassPathResource(filePath);
      scanner = new Scanner(classPathResource.getInputStream());

      // Prepare Lawn
      final int topRightX = scanner.nextInt();
      final int topRightY = scanner.nextInt();

      prepareMowing(topRightX, topRightY);

      scanner.nextLine();

      // Mowers control
      int x, y;
      char pointer;
      String commands;
      for (;;) {
        x = scanner.nextInt();
        y = scanner.nextInt();
        pointer = (char) scanner.next().trim().charAt(0);

        scanner.nextLine();
        commands = scanner.nextLine();

        mowers.add(startMower(x, y, pointer).execute(commands));

        if (!scanner.hasNextLine()) {
          break;
        }
      }
    } catch (IOException exception) {
      LOGGER.error("Failed to read file: {}", filePath);
      throw exception;
    } finally {
      if (null != scanner) {
        scanner.close();
      }
    }
    LOGGER.info("======== Execution done =========");
    return mowers;
  }

  /**
   * Sets lawn's grid.
   * @param topRightX 0 based
   * @param topRightY 0 based
   */
  public void prepareMowing(int topRightX, int topRightY) {
    lawn.prepare(topRightX, topRightY);
  }

  /**
   * Creates and puts mower in place.
   * @param x       0 based
   * @param y       0 based
   * @param pointer 'N', 'E', 'S' or 'W'
   * @return The started mower.
   */
  public Mower startMower(int x, int y, char pointer) {
    Mower mower = new Mower(lawn);
    return mower.start(x, y, pointer);
  }

}
