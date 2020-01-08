package com.zylex.livebetbot.model;

/**
 * Over under model.
 */
public class OverUnder {

    private long id;

    private long gameId;

    private OverUnderType overUnderType;

    private double size;

    private double coefficient;

    public OverUnder(long id, long gameId, OverUnderType overUnderType, double size, double coefficient) {
        this.id = id;
        this.gameId = gameId;
        this.overUnderType = overUnderType;
        this.size = size;
        this.coefficient = coefficient;
    }

    public OverUnder() {
    }

    public OverUnderType getOverUnderType() {
        return overUnderType;
    }

    public double getSize() {
        return size;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OverUnder{" +
                "id=" + id +
                ", gameId=" + gameId +
                ", type=" + overUnderType +
                ", size=" + size +
                ", coefficient=" + coefficient +
                '}';
    }
}
