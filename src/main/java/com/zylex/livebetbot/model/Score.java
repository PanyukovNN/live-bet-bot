package com.zylex.livebetbot.model;

public class Score {

    private int homeGoals;

    private int awayGoals;

    public Score(int homeGoals, int awayGoals) {
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    @Override
    public String toString() {
        return String.format("%d:%d",homeGoals, awayGoals);
    }
}
