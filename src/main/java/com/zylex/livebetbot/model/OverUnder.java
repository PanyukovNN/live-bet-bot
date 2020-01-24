package com.zylex.livebetbot.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Over under model.
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "over_under")
public class OverUnder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "type")
    private String overUnderType;

    @Column(name = "size")
    private double size;

    @Column(name = "coefficient")
    private double coefficient;

    public OverUnder(String overUnderType, double size, double coefficient) {
        this.overUnderType = overUnderType;
        this.size = size;
        this.coefficient = coefficient;
    }

    public OverUnder() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getOverUnderType() {
        return overUnderType;
    }

    public void setOverUnderType(String overUnderType) {
        this.overUnderType = overUnderType;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverUnder overUnder = (OverUnder) o;
        return Double.compare(overUnder.size, size) == 0 &&
                Double.compare(overUnder.coefficient, coefficient) == 0 &&
                Objects.equals(overUnderType, overUnder.overUnderType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(overUnderType, size, coefficient);
    }

    @Override
    public String toString() {
        return "OverUnder{" +
                "id=" + id +
                ", gameId=" + game.getId() +
                ", type=" + overUnderType +
                ", size=" + size +
                ", coefficient=" + coefficient +
                '}';
    }
}
