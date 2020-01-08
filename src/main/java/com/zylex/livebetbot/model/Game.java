package com.zylex.livebetbot.model;

import com.zylex.livebetbot.service.rule.RuleNumber;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Game {

    private long id;

    private LocalDateTime dateTime;

    private String firstTeam;

    private String secondTeam;

    private Goal breakGoal = new Goal(-1, -1);

    private Goal finalGoal = new Goal(-1, -1);

    private List<OverUnder> overUnderList;

    private String link;

    private RuleNumber ruleNumber = null;

    public Game(long id, LocalDateTime dateTime, String firstTeam, String secondTeam, String link) {
        this.id = id;
        this.dateTime = dateTime;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.link = link;
    }

    public Game(Game oldGame) {
        this.id = oldGame.getId();
        this.dateTime = oldGame.getDateTime();
        this.firstTeam = oldGame.getFirstTeam();
        this.secondTeam = oldGame.getSecondTeam();
        this.breakGoal = oldGame.getBreakGoal();
        this.finalGoal = oldGame.getFinalGoal();
        this.overUnderList = oldGame.overUnderList;
        this.link = oldGame.link;
        this.ruleNumber = oldGame.ruleNumber;
    }

    public Game() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
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

    public Goal getBreakGoal() {
        return breakGoal;
    }

    public void setBreakGoal(Goal breakGoal) {
        this.breakGoal = breakGoal;
    }

    public Goal getFinalGoal() {
        return finalGoal;
    }

    public void setFinalGoal(Goal finalGoal) {
        this.finalGoal = finalGoal;
    }

    public List<OverUnder> getOverUnderList() {
        return overUnderList;
    }

    public void setOverUnderList(List<OverUnder> overUnderList) {
        this.overUnderList = overUnderList;
    }

    public RuleNumber getRuleNumber() {
        return ruleNumber;
    }

    public void setRuleNumber(RuleNumber ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(link, game.link);
    }

    @Override
    public String toString() {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a yyyy.MM.dd");
        return String.format("%s %s - %s (%d:%d) (%d:%d) (%s)",
                DATE_TIME_FORMATTER.format(dateTime),
                firstTeam,
                secondTeam,
                breakGoal.getHomeGoals(),
                breakGoal.getAwayGoals(),
                finalGoal.getHomeGoals(),
                finalGoal.getAwayGoals(),
                (ruleNumber == null
                        ? "NO_RULE"
                        :ruleNumber.toString()));
    }
}
