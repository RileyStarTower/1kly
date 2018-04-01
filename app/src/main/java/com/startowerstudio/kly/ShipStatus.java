package com.startowerstudio.kly;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.startowerstudio.kly.db.ManifestQueries;

import java.util.Calendar;
import java.util.Locale;

public class ShipStatus extends KlyActivity {
    private TextView yearsElapsed, monthsElapsed, daysElapsed, hoursElapsed, minutesElapsed, secondsElapsed;
    private Handler handlerElapsed;
    private Runnable runnableElapsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startCountdown();
        startElapsed();

        if (!EtaCountdown.getInstance().timerUp()) {
            // If the timer isn't up, show everything
            BreakdownAdapter breakdownAdapter = new BreakdownAdapter(this,
                    ManifestQueries.getInstance().getOccupationBreakdown(this));
            final NonScrollListView listView = (NonScrollListView) findViewById(R.id.breakdownList);
            listView.setAdapter(breakdownAdapter);
            // Set the height of the ListView so it doesn't have to scroll
            setListViewHeight(listView);

            // Setting the height of the ListView gives it focus, so reset it afterward
            ScrollView scrollView = (ScrollView) findViewById(R.id.statusScroll);
            scrollView.setFocusableInTouchMode(true);
            scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        } else {
            // If the timer is up, don't show the breakdown, and reset a bunch of the text
            TextView textView = (TextView) findViewById(R.id.currentSpeedVal);
            textView.setText(R.string.null_string);
            textView.setTextColor(getResources().getColor(R.color.nullText));
            textView = (TextView) findViewById(R.id.averageSpeedVal);
            textView.setText(R.string.null_string);
            textView.setTextColor(getResources().getColor(R.color.nullText));
            textView = (TextView) findViewById(R.id.healthVal);
            textView.setText(R.string.null_string);
            textView.setTextColor(getResources().getColor(R.color.nullText));
            textView = (TextView) findViewById(R.id.riskVal);
            textView.setText(R.string.null_string);
            textView.setTextColor(getResources().getColor(R.color.nullText));

            ListView listView = (NonScrollListView) findViewById(R.id.breakdownList);
            listView.setVisibility(View.GONE);
        }
    }

    // Initialize the view objects for the elapsed time layout
    private void initElapsed() {
        yearsElapsed = (TextView) findViewById(R.id.yearElapsed);
        monthsElapsed = (TextView) findViewById(R.id.monthElapsed);
        daysElapsed = (TextView) findViewById(R.id.dayElapsed);
        hoursElapsed = (TextView) findViewById(R.id.hourElapsed);
        minutesElapsed = (TextView) findViewById(R.id.minuteElapsed);
        secondsElapsed = (TextView) findViewById(R.id.secondElapsed);
    }

    // Fills out the remaining years, and elapsed distance views, based on the elapsed time values
    private void fillOutUI() {
        int remainingYears = (int) (Integer.parseInt(years.getText().toString().trim()) * 0.99);
        String yearStr = remainingYears + " ly";
        TextView remainingDist = (TextView) findViewById(R.id.remainingDistVal);
        remainingDist.setText(yearStr);

        int elapsedYears = (int) (Integer.parseInt(yearsElapsed.getText().toString().trim()) * 0.99);
        yearStr = elapsedYears + " ly";
        TextView elapsedDist = (TextView) findViewById(R.id.elapsedDistVal);
        elapsedDist.setText(yearStr);
    }

    // Start the elapsed timer
    private void startElapsed() {
        initElapsed();
        handlerElapsed = new Handler();
        runnableElapsed = new Runnable() {
            @Override
            public void run() {
                handlerElapsed.postDelayed(this, 1000);
                try {
                    Calendar currentCalendar = Calendar.getInstance();
                    if (!EtaCountdown.getInstance().timerUp(currentCalendar)) {
                        Calendar departure = EtaCountdown.getInstance().getDeparture();
                        DateUtils.getInstance().updateCalendar(currentCalendar, departure, Calendar.SECOND);
                        DateUtils.getInstance().updateCalendar(currentCalendar, departure, Calendar.MINUTE);
                        DateUtils.getInstance().updateCalendar(currentCalendar, departure, Calendar.HOUR_OF_DAY);
                        DateUtils.getInstance().updateCalendar(currentCalendar, departure, Calendar.DAY_OF_MONTH);
                        DateUtils.getInstance().updateCalendar(currentCalendar, departure, Calendar.MONTH);
                        DateUtils.getInstance().updateCalendar(currentCalendar, departure, Calendar.YEAR);
                        updateText(secondsElapsed, DateUtils.getInstance().otherFormat(currentCalendar.get(Calendar.SECOND)));
                        updateText(minutesElapsed, DateUtils.getInstance().otherFormat(currentCalendar.get(Calendar.MINUTE)));
                        updateText(hoursElapsed, DateUtils.getInstance().otherFormat(currentCalendar.get(Calendar.HOUR_OF_DAY)));
                        updateText(daysElapsed, DateUtils.getInstance().otherFormat(currentCalendar.get(Calendar.DAY_OF_MONTH)));
                        updateText(monthsElapsed, DateUtils.getInstance().otherFormat(currentCalendar.get(Calendar.MONTH)));
                        updateText(yearsElapsed, DateUtils.getInstance().yearFormat(currentCalendar.get(Calendar.YEAR)));

                        // now that the time text fields are filled out, we can use them to update the other text fields
                        fillOutUI();
                    } else {
                        yearsElapsed.setText("000");
                        monthsElapsed.setText("00");
                        daysElapsed.setText("00");
                        hoursElapsed.setText("00");
                        minutesElapsed.setText("00");
                        secondsElapsed.setText("00");
                        fillOutUI();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handlerElapsed.postDelayed(runnableElapsed, 0);
    }

    // This just calls the base function with the specific string
    public void loginDialog(View view) {
        super.loginDialog(R.string.login_voyage_admin);
    }

    private class BreakdownAdapter extends CursorAdapter {
        BreakdownAdapter(Context context, Cursor cursor) {
            super (context, cursor, 0);
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            TextView textViewOccupation = (TextView) view.findViewById(R.id.breakdownName);
            final String occupation = cursor.getString(cursor.getColumnIndex(ManifestQueries.getInstance().MANIFEST_COL_OCC));
            textViewOccupation.setText(occupation);
            textViewOccupation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginDialog(v);
                }
            });

            TextView textViewCount = (TextView) view.findViewById(R.id.breakdownCount);
            // I probably shouldn't call this column "_id" but, it makes things easier, I guess
            final double count = cursor.getInt(cursor.getColumnIndex("_id"));
            final double total = cursor.getInt(cursor.getColumnIndex("total_count"));
            double percent = (count / total) * 100;
            String countStr = String.format(Locale.US, "%.2f", percent) + "%";
            textViewCount.setText(countStr);
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
