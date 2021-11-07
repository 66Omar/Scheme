package com.scheme.utilities;

public class EventTime {

    private int hour;
    private int minute;

    public EventTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getString() {
        String finalString = "";
        boolean isPM = false;
        if (hour > 12) {
            hour -= 12;
            isPM = true;
        }
        else if (hour == 12) {
            isPM = true;
        }
        String hourString = String.valueOf(hour);
        String minString = String.valueOf(minute);
        if (hourString.length() < 2) { finalString += "0"; }
        finalString += hourString;
        finalString += ":";
        if (minString.length() < 2) { finalString += "0"; }
        finalString += minString;
        if (isPM) { finalString += " PM"; } else { finalString += " AM"; }
        return finalString;
    }


}
