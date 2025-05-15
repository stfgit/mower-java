package com.mowitnow.mower.engine;

enum Compass {

  EAST('E', 1, 0) {

    @Override
    Compass left() {
      return NORTH;
    }

    @Override
    Compass right() {
      return SOUTH;
    }

  },
  NORTH('N', 0, 1) {

    @Override
    Compass left() {
      return WEST;
    }

    @Override
    Compass right() {
      return EAST;
    }

  },
  WEST('W', -1, 0) {

    @Override
    Compass left() {
      return SOUTH;
    }

    @Override
    Compass right() {
      return NORTH;
    }

  },
  SOUTH('S', 0, -1) {

    @Override
    Compass left() {
      return EAST;
    }

    @Override
    Compass right() {
      return WEST;
    }

  },
  VOID('V', 0, 0) {

    @Override
    Compass left() {
      return VOID;
    }

    @Override
    Compass right() {
      return VOID;
    }

  };

  private final char pointer;
  private final int deltaX;
  private final int deltaY;

  private Compass(char pointer, int deltaX, int deltaY) {
    this.pointer = pointer;
    this.deltaX = deltaX;
    this.deltaY = deltaY;
  }

  abstract Compass left();

  abstract Compass right();

  static final Compass byPointer(char pointer) {
    switch (pointer) {
      case 'E':
        return Compass.EAST;
      case 'N':
        return Compass.NORTH;
      case 'W':
        return Compass.WEST;
      case 'S':
        return Compass.SOUTH;
      default:
        System.out.println("WARN - Unknown cardinal point: " + pointer);
        return Compass.VOID;
    }
  }

  final int getDeltaX() {
    return deltaX;
  }

  final int getDeltaY() {
    return deltaY;
  }

  public final char getPointer() {
    return pointer;
  }

  @Override
  public String toString() {
    return "Compass [" + pointer + "]";
  }

}
