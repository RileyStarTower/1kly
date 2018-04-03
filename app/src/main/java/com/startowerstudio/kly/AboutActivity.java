package com.startowerstudio.kly;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // This code forces the scrollview to start at the top
        ScrollView scrollView = findViewById(R.id.aboutScroll);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
    }
}
