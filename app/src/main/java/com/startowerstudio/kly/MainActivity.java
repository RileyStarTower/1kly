package com.startowerstudio.kly;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.startowerstudio.kly.db.TaskQueries;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends KlyActivity {
    private ArrayList<KlyTask> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startCountdown();

        if (DateUtils.getInstance().timerActive()) {
            taskList = loadTasks();

            // maximum of 10 tasks to keep things reasonable to look at
            if (taskList.size() < 20) {
                // execute the query to get a new task
                Cursor taskCursor = TaskQueries.getInstance().getNTasks(this, 20 - taskList.size());

                // create a task unless we didn't get anything back (can happen if all tasks have been used recently
                if (taskCursor.getCount() > 0) {
                    // create the tasks
                    while (taskCursor.moveToNext()) {
                        int count = KlyTaskUtils.getInstance().getNextCount(taskList);
                        KlyTask task = new KlyTask(taskCursor, count, this);
                        TaskQueries.getInstance().updateTaskDate(this, task.getId());
                        task.writeTask(this);
                        taskList.add(task);

                        // now schedule the task
                        KlyTaskUtils.getInstance().scheduleNotification(this, task);
                    }
                }
            }

            updateTasksButton();
        } else {
            deleteTaskFiles();
        }
    }

    // TODO: test only
    private String getFirstTaskWrapper() {
        return KlyTaskUtils.getInstance().getFirstTask(taskList);
    }

    // TODO: TEST ONLY
    private String getCurrentTime() {
        return DateUtils.getInstance().unMkCalendar(Calendar.getInstance());
    }

    // Deletes all files representing current tasks
    private void deleteTaskFiles() {
        String[] fileList = getFilesDir().list();
        File dir = getFilesDir();
        for (String f:fileList) {
            if (!f.substring(0,3).equals("cur")) continue;
            File file = new File(dir, f);
            boolean deleted = file.delete();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Adds the number of current tasks to the button text
    // It would be cool if this updated in real time,
    // but I'm not going to take the time to do that
    private void updateTasksButton() {
        int count = 0;
        String countString;
        Button taskButton = findViewById(R.id.tasks);

        // count up the number of current tasks
        for (KlyTask task : taskList) {
            if (task.hasStarted()) count++;
        }

        // build a string for the task count
        if (count < 1) {
            countString = "";
        } else if (count <= 9) {
            countString = " (" + count + ")";
        } else {
            countString = " (9+)";
        }

        // update the button text
        String taskLabel = getResources().getText(R.string.title_activity_tasks) + countString;
        taskButton.setText(taskLabel);
    }

    // Activity redirection functions from here down
    public void tasksButton(View view) {
        Intent intent = new Intent(this, Tasks.class);
        startActivity(intent);
    }

    public void btnManifest(View view) {
        Intent intent = new Intent(this, Manifest.class);
        startActivity(intent);
    }

    public void btnDestination(View view) {
        Intent intent = new Intent(this, DestinationActivity.class);
        startActivity(intent);
    }

    public void aboutActivity(MenuItem item) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void settingsActivity(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
