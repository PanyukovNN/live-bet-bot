package com.zylex.livebetbot.model;

import java.time.LocalDate;

public class Game {

    private LocalDate date;

    private String firstTeam;

    private String secondTeam;

    private String link;

    public Game(LocalDate date, String firstTeam, String secondTeam) {
        this.date = date;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }
}
