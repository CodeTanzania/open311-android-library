package com.github.codetanzania.open311.android.library.utils;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This is used to manage date formats.
 */

public class DateUtils {
    private static SimpleDateFormat sdfMajiFixString =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    private static SimpleDateFormat sdfDisplayShort =
            new SimpleDateFormat("MMM dd HH:mm", Locale.getDefault());

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

    public static String formatForDisplay(@NonNull Calendar date) {
        return sdfDisplayShort.format(date.getTime());
    }

    public static void setCalendarInParcel(Parcel out, Calendar cal) {
        out.writeLong(cal == null ? -1 : cal.getTimeInMillis());
    }

    public static Calendar getCalendarFromParcel(Parcel in) {
        long mills = in.readLong();
        if (mills != -1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mills);
            return cal;
        }
        return null;
    }
}
