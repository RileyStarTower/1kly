package com.startowerstudio.kly;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

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
    void scheduleNotification(final Context context, final KlyTask task, final Handler handler) {
        if (isNotificationsOn(context)) {
            // If notifications are on, then we can schedule one
            long delay = task.getStart().getTimeInMillis() - System.currentTimeMillis();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    displayNotification(context);
                }
            };
            handler.postDelayed(runnable, delay);
        }
    }

    // Returns whether notifications for new tasks are turned on
    Boolean isNotificationsOn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SettingsActivity.PREF_NOTIFICATIONS, true);
    }

    // Cancels any existing notifications
    void cancelNotifications(Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    // Creates a notification for the task
    // Mostly copied from:
    // https://developer.android.com/guide/topics/ui/notifiers/notifications.html#SimpleNotification
    private void displayNotification(Context context) {
        // Check if notifications are on, since they may have been turned off after this notification was scheduled
        if (!isNotificationsOn(context)) {
            return;
        }

        // String CHANNEL_ID = "kly_channel"; // commented because I'm not sure if I need it now
        // TODO: clean this up; I added some scheduling stuff at some point, and it got bloated
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_kly_ship_icon_status_bar)
                        .setContentTitle("1 kly")
                        .setContentText("New tasks waiting in 1 kly");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, Tasks.class);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Tasks.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the notification.
        // For example, to cancel the notification, you can pass its ID number to NotificationManager.cancel().
        // we use the same notification for everything, because we only want one notification at a time
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
