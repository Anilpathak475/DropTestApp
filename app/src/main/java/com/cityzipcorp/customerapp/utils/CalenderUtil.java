package com.cityzipcorp.customerapp.utils;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by anilpathak on 03/11/17.
 */

public class CalenderUtil {


    public static String getTime(Date date) {
        String dateFormat = "hh:mm a";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    public static String getMonthAndDate(Date date) {
        String dateFormat = "MMM d";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    public static String getDay(Date date) {
        String dateFormat = "EEEE";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    public static String getCurrentTimeZone() {
        //get Calendar instance
        Calendar now = Calendar.getInstance();

        //get current TimeZone using getTimeZone method of Calendar class
        TimeZone timeZone = now.getTimeZone();
        return timeZone.getID();
    }

    public static List<CalendarDay> getAllDisabeldDay(Date date) {
        List<CalendarDay> allDays = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int myMonth = cal.get(Calendar.MONTH);

        while (myMonth == cal.get(Calendar.MONTH)) {
            if (cal.get(Calendar.DATE) < date.getDate() || (cal.get(Calendar.DATE) > date.getDate() + 13)) {
                CalendarDay calendarDay = CalendarDay.from(cal);
                allDays.add(calendarDay);
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return allDays;
    }
}