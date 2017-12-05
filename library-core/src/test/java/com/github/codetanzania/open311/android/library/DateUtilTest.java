package com.github.codetanzania.open311.android.library;

import com.github.codetanzania.open311.android.library.utils.DateUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Calendar;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * This is used to test string to date object conversions.
 */

@RunWith(JUnit4.class)
public class DateUtilTest {

    @Test
    public void testDateCreatedConversionFromString() throws ParseException {
        String fromServer = "2015-10-22T09:03:46.845Z";

        Calendar calendar = DateUtils.getCalendarFromMajiFixApiString(fromServer);

        testCalendar(calendar, 2015, Calendar.OCTOBER, 22, 9, 3, 46);
    }

    @Test
    public void testDateCreatedConversionFromMills() throws ParseException {
        String fromServer = "2015-10-22T09:03:46.845Z";
        Calendar calendar = DateUtils.getCalendarFromMajiFixApiString(fromServer);
        long mills = calendar.getTimeInMillis();

        Calendar fromMills = DateUtils.getCalendarFromDbMills(mills);

        testCalendar(fromMills, 2015, Calendar.OCTOBER, 22, 9, 3, 46);
    }

    @Test
    public void testDisplayFormat() {
        String fromServer = "2015-10-22T09:03:46.845Z";
        Calendar calendar = DateUtils.getCalendarFromMajiFixApiString(fromServer);

        String forDisplay = DateUtils.formatForDisplay(calendar);
        assertEquals("Oct 22 09:03", forDisplay);
    }

    public static void testCalendar(Calendar cal, int year, int month, int date,
                                    int hour, int min, int sec) {
        assertEquals(year, cal.get(Calendar.YEAR));
        assertEquals(month, cal.get(Calendar.MONTH));
        assertEquals(date, cal.get(Calendar.DATE));
        assertEquals(hour, cal.get(Calendar.HOUR));
        assertEquals(min, cal.get(Calendar.MINUTE));
        assertEquals(sec, cal.get(Calendar.SECOND));
    }
}
