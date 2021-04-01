package ru.kvp24.util;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtils {

    public static boolean isNightTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Samara"));
        return cal.get(Calendar.HOUR_OF_DAY) >= 18 || cal.get(Calendar.HOUR_OF_DAY) < 6;
    }
}
