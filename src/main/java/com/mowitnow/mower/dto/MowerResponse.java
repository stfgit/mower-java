package com.mowitnow.mower.dto;

public class MowerResponse {
    private String mowerId;
    private int x;
    private int y;
    private char direction;
    private String position;

    public MowerResponse() {
    }

    public MowerResponse(String mowerId, int x, int y, char direction) {
        this.mowerId = mowerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.position = x + " " + y + " " + direction;
    }

    public String getMowerId() {
        return mowerId;
    }

    public void setMowerId(String mowerId) {
        this.mowerId = mowerId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public char getDirection() {
        return direction;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}