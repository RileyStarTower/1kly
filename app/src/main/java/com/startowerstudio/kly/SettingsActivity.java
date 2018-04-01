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
    // TODO: there's some extra lifecycle stuff I'm supposed to manage here, I guess?
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(PREF_NOTIFICATIONS)) {
//            if (!KlyTaskUtils.getInstance().isNotificationsOn(this)) {
//                KlyTaskUtils.getInstance().cancelNotifications(this);
//            }
//        }
//    }
}
