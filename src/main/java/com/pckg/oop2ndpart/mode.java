package com.pckg.oop2ndpart;

/**
 * enum for argument user enters in terminal when presenting events
 */

public enum mode {
    all,
    day,
    week,
    month,
    pastday,
    pastweek,
    pastmonth,
    todo,
    due;

    public static String modeString(mode mode){
        String str =
        switch (mode){
            case all -> "events";
            case day -> "events for today";
            case week -> "events for this week";
            case month -> "events for this month";
            case pastday -> "events up to this moment today";
            case pastweek -> "events up to this moment this week";
            case pastmonth -> "events up to this moment this month";
            case todo -> "unfinished tasks that their due date has not passed";
            case due -> "unfinished tasks that their due date has passed";
        };
        return "There are no " + str;
    }
}


