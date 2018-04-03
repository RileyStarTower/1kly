package com.startowerstudio.kly;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Riley on 1/28/2018.
 */

public class KlyActivity extends AppCompatActivity {
    protected TextView years, months, days, hours, minutes, seconds;
    protected Handler handler;
    protected Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    // Get objects for all the TextViews in the countdown timer
    protected void initUI() {
        years = (TextView) findViewById(R.id.yearNumber);
        months = (TextView) findViewById(R.id.monthNumber);
        days = (TextView) findViewById(R.id.dayNumber);
        hours = (TextView) findViewById(R.id.hourNumber);
        minutes = (TextView) findViewById(R.id.minuteNumber);
        seconds = (TextView) findViewById(R.id.secondNumber);
    }

    // Start the countdown
    protected void startCountdown() {
        initUI();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    Calendar currentCalendar = Calendar.getInstance();
                    if (!EtaCountdown.getInstance().timerUp(currentCalendar)) {
                        Calendar diffCalendar = EtaCountdown.getInstance().getCopy();
                        // Calculate the difference
                        diffCalendar = DateUtils.getInstance().calcDiff(diffCalendar, currentCalendar);
                        // Display the result
                        updateText(seconds, DateUtils.getInstance().otherFormat(diffCalendar.get(Calendar.SECOND)));
                        updateText(minutes, DateUtils.getInstance().otherFormat(diffCalendar.get(Calendar.MINUTE)));
                        updateText(hours, DateUtils.getInstance().otherFormat(diffCalendar.get(Calendar.HOUR_OF_DAY)));
                        updateText(days, DateUtils.getInstance().otherFormat(diffCalendar.get(Calendar.DAY_OF_MONTH)));
                        updateText(months, DateUtils.getInstance().otherFormat(diffCalendar.get(Calendar.MONTH)));
                        updateText(years, DateUtils.getInstance().yearFormat(diffCalendar.get(Calendar.YEAR)));
                    } else {
                        years.setText("000");
                        months.setText("00");
                        days.setText("00");
                        hours.setText("00");
                        minutes.setText("00");
                        seconds.setText("00");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    protected void updateText(TextView view, String val) {
        view.setText(val);
    }

    // TODO: consider moving this to KlyTaskUtils
    // The filename must contain the correct prefix to be considered
    protected boolean validCurrentTask(String filename) {
        int length = KlyTask.CURRENT_TASKS_BASE.length();

        // if the filename is too short, it won't match
        if (filename.length() < length) return false;

        // the filename prefix needs to match
        String prefix = filename.substring(0, length);
        return prefix.equals(KlyTask.CURRENT_TASKS_BASE);
    }

    // TODO: consider moving this to KlyTaskUtils
    // Load and process the tasks file
    protected ArrayList<KlyTask> loadTasks() {
        ArrayList<KlyTask> taskList = new ArrayList<>();
        KlyTask task;

        // pull in all files
        String[] fileList = getFilesDir().list();
        for (String filename : fileList) {
            // only go through the "currentTask" files
            if (!validCurrentTask(filename)) continue;
            // make a KlyTask for each valid task file
            task = new KlyTask(filename, this);
            taskList.add(task);
        }

        return taskList;
    }

    // Sets a listView height based on the height of the children
    protected void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        // Loop over all elements in the list, and add up their height
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            listItem.getMeasuredHeight();
            totalHeight += listItem.getMeasuredHeight();
        }

        // Reset the height of the ListView
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // Displays the login dialog
    // Copied from the AlertDialog stuff here:
    // https://developer.android.com/guide/topics/ui/dialogs.html
    public void loginDialog(int stringID) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle(R.string.title_login)
                .setMessage(stringID)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // This navigation is possible from multiple activities, so it lives in the base class
    public void actStatus(View view) {
        Intent intent = new Intent(this, ShipStatus.class);
        startActivity(intent);
    }
}
