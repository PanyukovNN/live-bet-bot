package com.zylex.livebetbot.model;

public class TotalMoreLess {

    private MoreLess moreLess;

    private double size;

    private double coefficient;

    public TotalMoreLess(MoreLess moreLess, double size, double coefficient) {
        this.moreLess = moreLess;
        this.size = size;
        this.coefficient = coefficient;
    }

    public MoreLess getMoreLess() {
        return moreLess;
    }

    public double getSize() {
        return size;
    }

    public double getCoefficient() {
        return coefficient;
    }

    @Override
    public String toString() {
        return "TotalMoreLess{" +
                "moreLess=" + moreLess +
                ", size=" + size +
                ", coefficient=" + coefficient +
                '}';
    }
}
