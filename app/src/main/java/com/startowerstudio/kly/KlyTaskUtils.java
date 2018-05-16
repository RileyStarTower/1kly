package com.startowerstudio.kly;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by Riley on 3/5/2018.
 */

public class KlyTaskUtils {
    private static final int NOTIFICATION_ID = 1;
    private static final KlyTaskUtils ourInstance = new KlyTaskUtils();

    public static KlyTaskUtils getInstance() {
        return ourInstance;
    }

    private KlyTaskUtils() {
    }

    // Returns the next available count value for a task filename
    int getNextCount(ArrayList<KlyTask> taskList) {
        int count = 0;
        int temp;
        for (KlyTask task: taskList) {
            temp = task.getFileCount();
            if (temp == count) {
                // if this count is being used, try the next one
                count++;
            } else {
                // if this count isn't being used, we can use it now
                return count;
            }
        }

        return count;
    }

    // Returns true if the list contains at least one task with a start date in the past, false otherwise
    boolean hasCurrentTasks(ArrayList<KlyTask> taskList) {
        // check each task in the list to see if it has expired
        for (KlyTask task : taskList) {
            // if the start date is before the current date, then the list has at least one started task
            if (task.hasStarted()) {
                return true;
            }
        }

        return false;
    }

    // Schedules the notification for the task
    // ... and the difficult thing about doing that is I need to create an Intent that isn't an activity?
    void scheduleNotification(final Context context, final KlyTask task) {
        if (isNotificationsOn(context)) {
            // If notifications are on, then we can schedule one
            Intent intent = new Intent(context, NotificationService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, task.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, task.getStart().getTimeInMillis(), pendingIntent);
        }
    }

    // Returns whether notifications for new tasks are turned on
    protected Boolean isNotificationsOn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true);
    }

    // Cancels any existing notifications
    void cancelNotifications(Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            mNotificationManager.cancel(NOTIFICATION_ID);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // Cancels any existing alarms for a task
    void cancelAlarm(Context context, KlyTask task) {
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, task.getId(), intent, PendingIntent.FLAG_NO_CREATE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
