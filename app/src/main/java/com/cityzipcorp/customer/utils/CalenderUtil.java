package com.cityzipcorp.customer.utils;

import com.cityzipcorp.customer.model.Schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by anilpathak on 03/11/17.
 */

public class CalenderUtil {

    private static String dateFormat = "yyyy-MM-dd";

    public static String getTime(Date date) {
        String dateFormat = "hh:mm a";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    public static String getAMPMValueFromDate(Date date) {
        String dateFormat = "a";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    public static String getPassDateFromDate(Date date) {
        String formatDate = "dd MMM yyyy";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(formatDate, Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    public static String getMonthAndDate(Date date) {
        String dateFormat = "MMM d";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    public static String getMonth(Date date) {
        String dateFormat = "MMMM";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    public static String getFullMonthName(Date date) {
        String dateFormat = "MMM";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    public static String getDay(Date date) {
        String dateFormat = "EEEE";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    public static String getCurrentTimeZone() {
        Calendar now = Calendar.getInstance();
        TimeZone timeZone = now.getTimeZone();
        return timeZone.getID();
    }

    public static List<LinkedHashMap<String, Schedule>> getAllDays(Date date, List<Schedule> scheduleList) {

        List<LinkedHashMap<String, Schedule>> allDays = new ArrayList<>(3);

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(date);
        currentDate.set(Calendar.DAY_OF_MONTH, scheduleList.get(0).getDate().getDate());
        int day = currentDate.getFirstDayOfWeek();
        currentDate.set(Calendar.DAY_OF_MONTH, day);
        currentDate.setFirstDayOfWeek(Calendar.SUNDAY);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        while (allDays.size() < 3) {
            LinkedHashMap<String, Schedule> scheduleHashMap = new LinkedHashMap<>();
            for (int i = 1; i < 8; i++) {
                Schedule schedule = null;
                for (Schedule scheduleFromList : scheduleList) {
                    Calendar dateFromSchedule = Calendar.getInstance();
                    dateFromSchedule.setTime(scheduleFromList.getDate());
                    if (currentDate.get(Calendar.DATE) == dateFromSchedule.get(Calendar.DATE)) {
                        if (currentDate.get(Calendar.MONTH) == dateFromSchedule.get(Calendar.MONTH)) {
                            schedule = scheduleFromList;
                        }
                    }
                }
                scheduleHashMap.put(CalenderUtil.getDateStringFromDate(currentDate.getTime()), schedule);
                currentDate.add(Calendar.DAY_OF_MONTH, 1);
            }
            allDays.add(scheduleHashMap);

        }
        return allDays;
    }

    public static Date getDateFromString(String date) {
        DateFormat df = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        try {
            return df.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getDateStringFromDate(Date date) {
        DateFormat df = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return df.format(date);
    }

    public static HashMap<Integer, Integer> getAllDatesFromList(List<Schedule> scheduleList) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        int i = 0;
        for (Schedule schedule : scheduleList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(schedule.getDate());
            hashMap.put(calendar.get(Calendar.DATE), i);
            i++;
        }
        return hashMap;
    }

    public static boolean getDifferenceBetweenDates(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long elapsedHours = Math.abs(different / hoursInMilli);


        if (elapsedHours < 4) {
            String currTime = getAMPMValueFromDate(startDate);
            String targetTime = getAMPMValueFromDate(endDate);
            return currTime.equalsIgnoreCase(targetTime);
        }

        return false;
    }


    public static List<String> convertShiftTimeTo12HrsFormat(List<String> shiftTimes) {
        List<String> convertedShiftTimes = new ArrayList<>();
        for (String shiftTime : shiftTimes) {
            try {
                DateFormat f1 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH); //HH for hour of the day (0 - 23)
                Date d = f1.parse(shiftTime);
                convertedShiftTimes.add(getTime(d));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return convertedShiftTimes;
    }
}