package com.startowerstudio.kly;

import java.util.Calendar;

/**
 * Created by Riley on 1/28/2018.
 */

public class EtaCountdown {
    // TODO: most of this class in unnecessary at this point, I could probably get rid of it and add the useful bits to DateUtils
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
        departureCalendar.set(2004, 4, 17, 11, 45, 0);

    }

    boolean timerUp(Calendar currentCalendar) {
        return currentCalendar.after(etaCalendar);
    }

    boolean timerUp() {
        return Calendar.getInstance().after(etaCalendar);
    }

    // TODO: I don't think either of these are actually necessary
    Calendar getCopy() {
        Calendar copy = (Calendar) etaCalendar.clone();
        return copy;
    }

    Calendar getDeparture() {
        Calendar copy = (Calendar) departureCalendar.clone();
        return copy;
    }

}
