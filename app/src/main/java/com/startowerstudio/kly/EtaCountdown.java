package com.startowerstudio.kly;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Riley on 1/28/2018.
 */

public class EtaCountdown {
    private Calendar etaCalendar, departureCalendar;

    private static final EtaCountdown ourInstance = new EtaCountdown();

    public static EtaCountdown getInstance() {
        return ourInstance;
    }

    private EtaCountdown() {
        etaCalendar = Calendar.getInstance();
        etaCalendar.clear();
        etaCalendar.set(3002, 6, 30, 23, 58, 58);

        departureCalendar = Calendar.getInstance();
        departureCalendar.clear();
        departureCalendar.set(2004, 4, 17, 11, 45, 00);

    }

    boolean timerUp(Calendar currentCalendar) {
        return currentCalendar.after(etaCalendar);
    }

    boolean timerUp() {
        return Calendar.getInstance().after(etaCalendar);
    }

    /*
     * The get functions are the main part of this class. They calculate what should be displayed
     * in the countdown timer at the top of every activity.
     */
    public String getYear(Calendar currentCalendar) {
        int yearDiff = etaCalendar.get(Calendar.YEAR) - currentCalendar.get(Calendar.YEAR);
        yearDiff -= yearOffset(currentCalendar);
        return String.format(Locale.US, "%3d", yearDiff);
    }

    public String getMonth(Calendar currentCalendar) {
        int monthDiff = etaCalendar.get(Calendar.MONTH) - currentCalendar.get(Calendar.MONTH);
        monthDiff -= monthOffset(currentCalendar);
        return String.format(Locale.US, "%02d", monthDiff);
    }

    public String getDay(Calendar currentCalendar) {
        int dayDiff = etaCalendar.get(Calendar.DAY_OF_MONTH) - currentCalendar.get(Calendar.DAY_OF_MONTH);
        dayDiff -= dayOffset(currentCalendar);
        return String.format(Locale.US, "%02d", dayDiff);
    }

    public String getHour(Calendar currentCalendar) {
        int hourDiff = etaCalendar.get(Calendar.HOUR_OF_DAY) - currentCalendar.get(Calendar.HOUR_OF_DAY);
        hourDiff -= hourOffset(currentCalendar);
        return String.format(Locale.US, "%02d", hourDiff);
    }

    public String getMinute(Calendar currentCalendar) {
        int minuteDiff = etaCalendar.get(Calendar.MINUTE) - currentCalendar.get(Calendar.MINUTE);
        minuteDiff -= minuteOffset(currentCalendar);
        return String.format(Locale.US, "%02d", minuteDiff);
    }

    public String getSecond(Calendar currentCalendar) {
        int secondDiff = etaCalendar.get(Calendar.SECOND) - currentCalendar.get(Calendar.SECOND);
        secondDiff = properMod(secondDiff, 60);
        return String.format(Locale.US, "%02d", secondDiff);
    }

    /*
     * Offset methods determine whether we should adjust a value based on the next more granular value
     */
    private int yearOffset(Calendar currentCalendar) {
        if (calendarOffset(etaCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.MONTH)) > 0) return 1;
        return 0;
    }

    private int monthOffset(Calendar currentCalendar) {
        if (calendarOffset(etaCalendar.get(Calendar.DAY_OF_MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH)) > 0) return 1;
        return 0;
    }

    private int dayOffset(Calendar currentCalendar) {
        if (calendarOffset(etaCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.HOUR_OF_DAY)) > 0) return 1;
        return 0;
    }

    private int hourOffset(Calendar currentCalendar) {
        if (calendarOffset(etaCalendar.get(Calendar.MINUTE), currentCalendar.get(Calendar.MINUTE)) > 0) return 1;
        return 0;
    }

    private int minuteOffset(Calendar currentCalendar) {
        if (calendarOffset(etaCalendar.get(Calendar.SECOND), currentCalendar.get(Calendar.SECOND)) > 0) return 1;
        return 0;
    }

    // If, say, the ETA month is earlier in the year than the current month, then we need to offset the year we display
    private int calendarOffset(int etaValue, int currentValue) {
        return (etaValue < currentValue) ? 1 : 0;
    }

    // Java apparently doesn't calculate mod correctly for negative values, so we need a special function to take care of this
    // explanation of math here: https://stackoverflow.com/questions/4412179/best-way-to-make-javas-modulus-behave-like-it-should-with-negative-numbers/4412200#4412200
    // there's another option in Java 8, but it looks like that needs a higher API level in android than I want to use
    private int properMod(int a, int b) {return ((a % b) + b) % b; }

    Calendar getCopy() {
        Calendar copy = (Calendar) etaCalendar.clone();
        return copy;
    }

    Calendar getDeparture() {
        Calendar copy = (Calendar) departureCalendar.clone();
        return copy;
    }

}
