package ru.kvp24.util;

import java.util.Calendar;

public class TimeUtils {

    public static boolean isNightTime() {
        return Calendar.getInstance().get((Calendar.HOUR_OF_DAY)) >= 18 || Calendar.getInstance().get((Calendar.HOUR_OF_DAY)) <= 6;
    }

}
