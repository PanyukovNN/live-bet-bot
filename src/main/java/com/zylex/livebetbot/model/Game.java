package com.zylex.livebetbot.model;

import com.zylex.livebetbot.service.rule.RuleNumber;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Game {

    private LocalDate date;

    private String firstTeam;

    private String secondTeam;

    private Goal breakGoals;

    private Goal finalGoal;

    private List<Tml> tmlList;

    private String link;

    private RuleNumber ruleNumber;

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

    public Goal getBreakGoals() {
        return breakGoals;
    }

    public void setBreakGoals(Goal breakGoals) {
        this.breakGoals = breakGoals;
    }

    public Goal getFinalGoal() {
        return finalGoal;
    }

    public void setFinalGoal(Goal finalGoal) {
        this.finalGoal = finalGoal;
    }

    public List<Tml> getTotalMoreLessList() {
        return tmlList;
    }

    public void setTotalMoreLessList(List<Tml> TmlList) {
        this.tmlList = TmlList;
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
        return "Game{" +
                "date=" + date +
                ", firstTeam='" + firstTeam + '\'' +
                ", secondTeam='" + secondTeam + '\'' +
                ", breakGoals=" + breakGoals +
                ", finalGoal=" + finalGoal +
                ", TmlList=" + tmlList +
                ", link='" + link + '\'' +
                ", ruleNumber=" + ruleNumber +
                '}';
    }
}
