package com.zylex.livebetbot.model;

import com.zylex.livebetbot.service.rule.RuleNumber;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Game {

    private long id;

    private LocalDate date;

    private String firstTeam;

    private String secondTeam;

    private Goal breakGoal = new Goal(-1, -1);

    private Goal finalGoal = new Goal(-1, -1);

    private List<Tml> tmlList;

    private String link;

    private RuleNumber ruleNumber;

    public Game(long id, LocalDate date, String firstTeam, String secondTeam, String link) {
        this.id = id;
        this.date = date;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.link = link;
    }

    public Game() {
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

    public List<Tml> getTmlList() {
        return tmlList;
    }

    public void setTmlList(List<Tml> TmlList) {
        this.tmlList = TmlList;
    }

    public RuleNumber getRuleNumber() {
        return ruleNumber;
    }

    public void setRuleNumber(RuleNumber ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(date, game.date) &&
                Objects.equals(firstTeam, game.firstTeam) &&
                Objects.equals(secondTeam, game.secondTeam) &&
                Objects.equals(link, game.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, firstTeam, secondTeam, link);
    }

    @Override
    public String toString() {
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return String.format("%s %s - %s (%d:%d) (%d:%d) (%s)",
                DATE_FORMATTER.format(date),
                firstTeam,
                secondTeam,
                breakGoal.getHomeGoals(),
                breakGoal.getAwayGoals(),
                finalGoal.getHomeGoals(),
                finalGoal.getAwayGoals(),
                (ruleNumber == null
                        ? " (NO RULE) "
                        : " (" + ruleNumber.toString() + ") "));
    }
}
