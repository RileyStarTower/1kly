package com.startowerstudio.kly;

import android.os.Bundle;

public class SettingsActivity extends KlyActivity {
//        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREF_NOTIFICATIONS = "pref_notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    // If notifications are turned off, we should clear out any existing notifications
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(PREF_NOTIFICATIONS)) {
//            if (!KlyTaskUtils.getInstance().isNotificationsOn(this)) {
//                KlyTaskUtils.getInstance().cancelNotifications(this);
//            } else {
//                KlyTaskUtils.getInstance().restartNotifications(this, loadTasks());
//            }
//        }
//    }
//
//    // https://developer.android.com/guide/topics/ui/settings.html#Listening
//    @Override
//    protected void onResume() {
//        super.onResume();
//        PreferenceManager.getDefaultSharedPreferences(this)
//                .registerOnSharedPreferenceChangeListener(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        PreferenceManager.getDefaultSharedPreferences(this)
//                .unregisterOnSharedPreferenceChangeListener(this);
//    }
}
