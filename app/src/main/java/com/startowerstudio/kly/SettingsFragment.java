package com.startowerstudio.kly;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Riley on 3/16/2018.
 */

// The official example has this as a static class, but android studio doesn't want me to do that
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from the XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
