package com.startowerstudio.kly;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.startowerstudio.kly.db.ManifestQueries;

public class DestinationActivity extends KlyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startCountdown();

        if (DateUtils.getInstance().timerActive()) {
            // If the timer isn't up, display the pre-arrival resident breakdown
            ResidentAdapter breakdownAdapter = new ResidentAdapter(this, ManifestQueries.getInstance().getResidents(this));
            final NonScrollListView listView = findViewById(R.id.residentList);
            listView.setAdapter(breakdownAdapter);
            setListViewHeight(listView);
        } else {
            // If the timer is up, display the resident breakdown with the passengers accounted for
            ResidentAdapter breakdownAdapter = new ResidentAdapter(this, ManifestQueries.getInstance().getEndResidents(this));
            final NonScrollListView listView = findViewById(R.id.residentList);
            listView.setAdapter(breakdownAdapter);
            setListViewHeight(listView);
        }
    }

    // This just calls the base function with the specific string
    public void loginDialog(View v) {
        super.loginDialog(R.string.login_resident_admin);
    }

    private class ResidentAdapter extends CursorAdapter {
        ResidentAdapter(Context context, Cursor cursor) {
            super (context, cursor, 0);
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            // Add the resident type to the breakdown view
            TextView textViewType = view.findViewById(R.id.breakdownName);
            final String type = cursor.getString(cursor.getColumnIndex("_id"));
            textViewType.setText(type);
            textViewType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginDialog(v);
                }
            });

            // Add the count for the given resident type to the breakdown view
            TextView textViewCount = view.findViewById(R.id.breakdownCount);
            final String count = cursor.getString(cursor.getColumnIndex("count"));
            textViewCount.setText(count);
            textViewCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginDialog(v);
                }
            });
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.breakdown_adapter, parent, false);
        }
    }

}
