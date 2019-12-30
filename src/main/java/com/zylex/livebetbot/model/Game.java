package com.zylex.livebetbot.model;

import java.time.LocalDate;

public class Game {

    private LocalDate date;

    private String firstTeam;

    private String secondTeam;

    private int homeGoalsOnBreak;

    private int awayGoalsOnBreak;

    private int homeGoalsFinal;

    private int awayGoalsFinal;

    private String link;

    public Game(LocalDate date, String firstTeam, String secondTeam, String link) {
        this.date = date;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.link = link;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getFirstTeam() {
        return firstTeam;
    }

    public String getSecondTeam() {
        return secondTeam;
    }

    public String getLink() {
        return link;
    }

    public int getHomeGoalsOnBreak() {
        return homeGoalsOnBreak;
    }

    public void setHomeGoalsOnBreak(int homeGoalsOnBreak) {
        this.homeGoalsOnBreak = homeGoalsOnBreak;
    }

    public int getAwayGoalsOnBreak() {
        return awayGoalsOnBreak;
    }

    public void setAwayGoalsOnBreak(int awayGoalsOnBreak) {
        this.awayGoalsOnBreak = awayGoalsOnBreak;
    }

    public int getHomeGoalsFinal() {
        return homeGoalsFinal;
    }

    public void setHomeGoalsFinal(int homeGoalsFinal) {
        this.homeGoalsFinal = homeGoalsFinal;
    }

    public int getAwayGoalsFinal() {
        return awayGoalsFinal;
    }

    public void setAwayGoalsFinal(int awayGoalsFinal) {
        this.awayGoalsFinal = awayGoalsFinal;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setFirstTeam(String firstTeam) {
        this.firstTeam = firstTeam;
    }

    public void setSecondTeam(String secondTeam) {
        this.secondTeam = secondTeam;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
