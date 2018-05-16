package com.startowerstudio.kly;

import android.os.Bundle;

public class SettingsActivity extends KlyActivity {

    public static final String PREF_NOTIFICATIONS = "pref_notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
