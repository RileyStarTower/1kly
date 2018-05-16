package com.startowerstudio.kly;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Riley on 1/27/2018.
 *
 * Utility class for handling dates with the following assumed format:
 *  YYYY:MM:DD:HH:mm:SS
 *
 * So the substring indexes are:
 *  year: 0, 4
 *  month: 5, 6
 *  day: 7, 9
 *  hour: 10, 12
 *  minute: 13, 15
 *  second: 16, 18
 */

public class DateUtils {
    private static final DateUtils ourInstance = new DateUtils();
    private static final int HOURS_IN_MONTH = 720;
    private Calendar etaCalendar, departureCalendar;

    static DateUtils getInstance() {
        return ourInstance;
    }

    private DateUtils() {
        etaCalendar = Calendar.getInstance();
        etaCalendar.clear();
        etaCalendar.set(3002, 6, 30, 23, 58, 58);

        departureCalendar = Calendar.getInstance();
        departureCalendar.clear();
        departureCalendar.set(2004, 4, 17, 11, 45, 0);
    }

    // Helper method to get a substring and parse out the integer
    private int subAndParse(String date, int begin, int end) {
        if (date == null) return 0;
        String temp = date.substring(begin, end);
        return Integer.parseInt(temp);
    }

    // These get methods parse a date string to retrieve a particular date component
    private int getYear(String date) {
        return subAndParse(date, 0, 4);
    }

    private int getMonth(String date) {
        return subAndParse(date, 5, 7);
    }

    private int getDay(String date) {
        return subAndParse(date, 8, 10);
    }

    private int getHour(String date) {
        return subAndParse(date, 11, 13);
    }

    private int getMinute(String date) {
        return subAndParse(date, 14, 16);
    }

    private int getSecond(String date) {
        return subAndParse(date, 17, 19);
    }

    // Formats a year value to have 3 digits, used in the countdown
    String yearFormat(int year) {
        return String.format(Locale.US, "%3d", year);
    }

    // Formats non-year values to have 2 digits, used in the countdown
    String otherFormat(int val) {
        return String.format(Locale.US, "%02d", val);
    }

    // Returns difference between current day of month and a future day of month
    int getExpirationLength(Calendar expiration) {
        Calendar diffCalendar = calcDiff(expiration, Calendar.getInstance());
        return diffCalendar.get(Calendar.DAY_OF_MONTH);
    }

    // Builds a Calendar object from a formatted date string
    Calendar mkCalendar(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYear(date), getMonth(date), getDay(date), getHour(date), getMinute(date), getSecond(date));
        return calendar;
    }

    // Converts a Calendar object back into a formatted String
    String unMkCalendar(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + ":" +
                formatTwo(calendar.get(Calendar.MONTH)) + ":" +
                formatTwo(calendar.get(Calendar.DAY_OF_MONTH)) + ":" +
                formatTwo(calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                formatTwo(calendar.get(Calendar.MINUTE)) + ":" +
                formatTwo(calendar.get(Calendar.SECOND)) + "\n";
    }

    // Formats a value to have 2 digits
    private String formatTwo(int value) {
        return String.format(Locale.US, "%02d", value);
    }

    // Generate a date offset for tasks
    private int mkDateOffset() {
        // get a random number between 0 and 720
        return (int) (Math.random() * HOURS_IN_MONTH);
        //return randomNum;
    }

    // Generate a start date for tasks
    Calendar mkStartDate() {
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.HOUR_OF_DAY, (int) (mkDateOffset() * 0.25));
        return startDate;
    }

    // Generate an end date for tasks
    Calendar mkEndDate(Calendar startDate) {
        Calendar endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.HOUR_OF_DAY, mkDateOffset());
        return endDate;
    }

    // Calculates the difference between two Calendar objects, returns a Calendar object with that difference
    Calendar calcDiff(Calendar later, Calendar earlier) {
        Calendar diffCalendar = (Calendar) later.clone();
        // Calculate the difference
        DateUtils.getInstance().updateCalendar(diffCalendar, earlier, Calendar.SECOND);
        DateUtils.getInstance().updateCalendar(diffCalendar, earlier, Calendar.MINUTE);
        DateUtils.getInstance().updateCalendar(diffCalendar, earlier, Calendar.HOUR_OF_DAY);
        DateUtils.getInstance().updateCalendar(diffCalendar, earlier, Calendar.DAY_OF_MONTH);
        DateUtils.getInstance().updateCalendar(diffCalendar, earlier, Calendar.MONTH);
        DateUtils.getInstance().updateCalendar(diffCalendar, earlier, Calendar.YEAR);

        return diffCalendar;
    }

    // Subtracts the given field of the current calendar from the diff calendar
    void updateCalendar(Calendar diffCalendar, Calendar currentCalendar, int field) {
        int val = -1 * currentCalendar.get(field);
        diffCalendar.add(field, val);
    }

    boolean timerActive(Calendar currentCalendar) {
        return currentCalendar.before(etaCalendar);
    }

    boolean timerActive() {
        return Calendar.getInstance().before(etaCalendar);
    }

    Calendar getCopy() {
        return (Calendar) etaCalendar.clone();
    }

    Calendar getDeparture() {
        return (Calendar) departureCalendar.clone();
    }

}
