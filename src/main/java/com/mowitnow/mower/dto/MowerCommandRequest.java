package com.mowitnow.mower.dto;

public class MowerCommandRequest {
    private String lawnDimensions;
    private String mowerPosition;
    private String commands;

    public MowerCommandRequest() {
    }

    public MowerCommandRequest(String lawnDimensions, String mowerPosition, String commands) {
        this.lawnDimensions = lawnDimensions;
        this.mowerPosition = mowerPosition;
        this.commands = commands;
    }

    public String getLawnDimensions() {
        return lawnDimensions;
    }

    public void setLawnDimensions(String lawnDimensions) {
        this.lawnDimensions = lawnDimensions;
    }

    public String getMowerPosition() {
        return mowerPosition;
    }

    public void setMowerPosition(String mowerPosition) {
        this.mowerPosition = mowerPosition;
    }

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }
}