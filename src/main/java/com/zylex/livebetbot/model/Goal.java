package com.zylex.livebetbot.model;

public class Goal {

    private int homeGoals;

    private int awayGoals;

    public Goal(int homeGoals, int awayGoals) {
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
        return "Goals{" +
                "homeGoals=" + homeGoals +
                ", awayGoals=" + awayGoals +
                '}';
    }
}
