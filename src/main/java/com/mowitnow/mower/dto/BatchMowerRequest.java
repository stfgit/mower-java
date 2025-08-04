package com.mowitnow.mower.dto;

import java.util.List;

public class BatchMowerRequest {
    private String lawnDimensions;
    private List<MowerCommandRequest> mowers;

    public BatchMowerRequest() {
    }

    public BatchMowerRequest(String lawnDimensions, List<MowerCommandRequest> mowers) {
        this.lawnDimensions = lawnDimensions;
        this.mowers = mowers;
    }

    public String getLawnDimensions() {
        return lawnDimensions;
    }

    public void setLawnDimensions(String lawnDimensions) {
        this.lawnDimensions = lawnDimensions;
    }

    public List<MowerCommandRequest> getMowers() {
        return mowers;
    }

    public void setMowers(List<MowerCommandRequest> mowers) {
        this.mowers = mowers;
    }
}