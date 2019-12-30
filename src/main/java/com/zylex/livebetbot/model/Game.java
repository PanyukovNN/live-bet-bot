package com.zylex.livebetbot.model;

import java.time.LocalDate;

public class Game {

    private LocalDate date;

    private String firstTeam;

    private String secondTeam;

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

    @Override
    public String toString() {
        return "Game{" +
                "date=" + date +
                ", firstTeam='" + firstTeam + '\'' +
                ", secondTeam='" + secondTeam + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
