package com.zylex.livebetbot.model;

import javax.persistence.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unused", "WeakerAccess"})
@Entity
@Table(name = "game")
public class Game implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "first_team")
    private String firstTeam;

    @Column(name = "second_team")
    private String secondTeam;

    @Column(name = "half_time_score")
    private String halfTimeScore = "-1:-1";

    @Column(name = "final_score")
    private String finalScore = "-1:-1";

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<OverUnder> overUnderSet = new HashSet<>();

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

    public Set<OverUnder> getOverUnderSet() {
        return overUnderSet;
    }

    public void setOverUnderSet(Set<OverUnder> overUnderList) {
        this.overUnderSet = overUnderList;
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
        return String.format("%s %s - %s (%s) (%s) (%s)",
                DATE_TIME_FORMATTER.format(dateTime),
                firstTeam,
                secondTeam,
                halfTimeScore,
                finalScore,
                (ruleNumber == null
                        ? "NO_RULE"
                        : ruleNumber));
    }
}
