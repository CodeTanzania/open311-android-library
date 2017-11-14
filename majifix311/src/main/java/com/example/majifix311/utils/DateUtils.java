package com.example.majifix311.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This is used to manage date formats.
 */

public class DateUtils {
    private static SimpleDateFormat sdfMajiFixString =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static Calendar getCalendarFromMajiFixApiString(String fromServer) {
        if (fromServer == null) {
            return null;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdfMajiFixString.parse(fromServer));
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Calendar getCalendarFromDbMills(long fromDb) {
        if (fromDb == -1) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(fromDb));
        return calendar;
    }
}
