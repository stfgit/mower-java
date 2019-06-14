package com.mowitnow.mower.engine;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public final class Mower {
  private static final Logger LOGGER = LoggerFactory.getLogger(Mower.class);

  private static final char LEFT = 'G';
  private static final char RIGHT = 'D';
  private static final char FORWARD = 'A';

  @Autowired
  private Lawn lawn;
  private int x;
  private int y;
  private Compass compass;
  private final String name;

  public Mower() {
    this.name = UUID.randomUUID().toString();
  }

  Mower start(int x, int y, char pointer) {
    LOGGER.debug("Start mower. x={} y={} pointer={}", x, y, pointer);
    if (lawn.checkAccess(x, y)) {
      this.x = x;
      this.y = y;
      this.compass = Compass.byPointer(pointer);
      LOGGER.debug("Mower started: name={}", name);
    } else {
      throw new IllegalStateException("Bad start position: x=" + x + ", y=" + y);
    }
    return this;
  }

  private void left() {
    compass = compass.left();
    LOGGER.debug("Mower turned left: {}", toString());
  }

  private void right() {
    compass = compass.right();
    LOGGER.debug("Mower turned right: {}", toString());
  }

  private boolean forward() {
    final int toX = x + compass.getDeltaX();
    final int toY = y + compass.getDeltaY();

    if (lawn.checkAccess(toX, toY)) {
      x = toX;
      y = toY;
      LOGGER.debug("Mower moved: {}", toString());
      return true;
    } else {
      LOGGER.warn("Mower blocked: {}", toString());
      return false;
    }

  }

  Mower execute(final String commands) {
    for (final char command : commands.toCharArray()) {
      LOGGER.debug("Mower {} received '{}'", name, command);
      switch (command) {
        case LEFT:
          left();
          break;
        case RIGHT:
          right();
          break;
        case FORWARD:
          forward();
          break;
        default:
          LOGGER.warn("Unknown command: '{}'", command);
      }
    }
    LOGGER.info("Mower {} executed '{}'\n ==> {}", name, commands, toString());
    return this;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  Compass getCompass() {
    return compass;
  }

  public char getPointer() {
    return compass.getPointer();
  }

  @Override
  public String toString() {
    return "Mower: [name=" + name + ", x=" + x + ", y=" + y + ", compass=" + compass.toString() + "]";
  }

}
