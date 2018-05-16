package com.startowerstudio.kly;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class NotificationService extends Service {
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Check if notifications are on, since they may have been turned off after this service was scheduled
        if (KlyTaskUtils.getInstance().isNotificationsOn(this)) {
            displayNotification();
        }
        stopSelf();
        return START_STICKY;
    }

    // Display the "new tasks" notification
    // Largely taken from the Android guide:
    // https://developer.android.com/reference/android/app/Service.html
    private void displayNotification() {
        // The pending intent to launch the Tasks activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Tasks.class), 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_kly_ship_icon_status_bar)
                .setContentTitle("New tasks")
//                .setContentText("New tasks waiting")
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the notification.
        // For example, to cancel the notification, you can pass its ID number to NotificationManager.cancel().
        // we use the same notification for everything, because we only want one notification at a time
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}
