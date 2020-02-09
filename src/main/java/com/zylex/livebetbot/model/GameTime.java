package com.zylex.livebetbot.model;

public class GameTime {

    private Half half;

    private int startTime;

    private int endTime;

    public GameTime(Half half, int startTime, int endTime) {
        this.half = half;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean checkTime(String time) {
        if (half == Half.HALF_TIME) {
            return time.contains(Half.HALF_TIME.type);
        }
        if (half == Half.FIRST_HALF && time.contains(Half.FIRST_HALF.type)) {
            return checkMinutesDiapason(time);
        }
        if (half == Half.SECOND_HALF && time.contains(Half.SECOND_HALF.type)) {
            return checkMinutesDiapason(time);
        }
        return false;
    }

    private boolean checkMinutesDiapason(String time) {
        String minutesItem = time.split(" ", 2)[1].replace("'", "");
        int minutes = Integer.parseInt(minutesItem);
        return startTime <= minutes || minutes <= endTime;
    }

    public enum Half {
        FIRST_HALF("1H"),
        HALF_TIME("HT"),
        SECOND_HALF("2H");

        String type;

        Half(String type) {
            this.type = type;
        }
    }
}
