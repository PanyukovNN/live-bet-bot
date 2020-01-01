package com.zylex.livebetbot.model;

/**
 * Total-more-less model.
 */
public class Tml {

    private long id;

    private long gameId;

    private MoreLess moreLess;

    private double size;

    private double coefficient;

    public Tml(long id, long gameId, MoreLess moreLess, double size, double coefficient) {
        this.id = id;
        this.gameId = gameId;
        this.moreLess = moreLess;
        this.size = size;
        this.coefficient = coefficient;
    }

    public Tml() {
    }

    public MoreLess getMoreLess() {
        return moreLess;
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
        return "Tml{" +
                "id=" + id +
                ", gameId=" + gameId +
                ", moreLess=" + moreLess +
                ", size=" + size +
                ", coefficient=" + coefficient +
                '}';
    }
}
