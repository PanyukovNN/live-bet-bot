package com.zylex.livebetbot.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
@Table(name = "game")
public class Game implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    private League league;

    @Column(name = "first_team")
    private String firstTeam;

    @Column(name = "second_team")
    private String secondTeam;

    @Column(name = "half_time_score")
    private String halfTimeScore = "-1:-1";

    @Column(name = "final_score")
    private String finalScore = "-1:-1";

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OverUnder> overUnderList = new ArrayList<>();

    @Column(name = "link")
    private String link;

    @Column(name = "rule_number")
    private String ruleNumber;

    public Game(LocalDateTime dateTime, String firstTeam, String secondTeam, String link) {
        this.dateTime = dateTime;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.link = link;
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

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public String getFirstTeam() {
        return firstTeam;
    }

    public void setFirstTeam(String firstTeam) {
        this.firstTeam = firstTeam;
    }

    public String getSecondTeam() {
        return secondTeam;
    }

    public void setSecondTeam(String secondTeam) {
        this.secondTeam = secondTeam;
    }

    public String getHalfTimeScore() {
        return halfTimeScore;
    }

    public void setHalfTimeScore(String halfTimeScore) {
        this.halfTimeScore = halfTimeScore;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public List<OverUnder> getOverUnderList() {
        return overUnderList;
    }

    public void setOverUnderList(List<OverUnder> overUnderList) {
        this.overUnderList = overUnderList;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRuleNumber() {
        return ruleNumber;
    }

    public void setRuleNumber(String ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(link, game.link) &&
                Objects.equals(ruleNumber, game.ruleNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, ruleNumber);
    }

    @Override
    public String toString() {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a yyyy.MM.dd");
        return String.format("%s %s %s \"%s\" - \"%s\" (%s) (%s) (%s)",
                DATE_TIME_FORMATTER.format(dateTime),
                country.getName(),
                league.getName(),
                firstTeam,
                secondTeam,
                halfTimeScore,
                finalScore,
                (ruleNumber == null
                        ? "NO_RULE"
                        : ruleNumber));
    }
}
