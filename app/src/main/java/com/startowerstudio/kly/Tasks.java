package com.startowerstudio.kly;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Tasks extends KlyActivity {
    private ArrayList<KlyTask> taskList;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startCountdown();

        taskList = loadStartedTasks();
        taskAdapter = new TaskAdapter();

        // if the list has no tasks that have started, show the "no tasks" message
        if (!KlyTaskUtils.getInstance().hasCurrentTasks(taskList)) {
            TextView noTasks = findViewById(R.id.noTasks);
            noTasks.setVisibility(View.VISIBLE);
        } else {
            // otherwise, hide the "no tasks" message and show the list
            TextView noTasks = findViewById(R.id.noTasks);
            noTasks.setVisibility(View.GONE);
            final ListView listView = findViewById(R.id.taskList);
            listView.setAdapter(taskAdapter);
            setListViewHeight(listView);
        }
    }

    // Adapter button to resolve the task
    public void resolveTask(int position, KlyTask task) {
        // delete the corresponding file
        File dir = getFilesDir();
        File file = new File(dir, task.getFilename());
        boolean deleted = file.delete();

        // Remove the notification when we clear the task
        KlyTaskUtils.getInstance().cancelNotifications(this);

        // remove the task from the list
        taskList.remove(position);
        taskAdapter.notifyDataSetChanged();

        if (taskList.size() < 1) {
            TextView noTasks = findViewById(R.id.noTasks);
            noTasks.setVisibility(View.VISIBLE);
        }

        Toast.makeText(this, "Task resolved", Toast.LENGTH_SHORT).show();
    }

    // Load all tasks from file, and only keep ones that have started
    private ArrayList<KlyTask> loadStartedTasks() {
        ArrayList<KlyTask> taskList = new ArrayList<>();
        KlyTask task;

        // pull in all files
        String[] fileList = getFilesDir().list();
        for (String filename : fileList) {
            // only go through the "currentTask" files
            if (invalidCurrentTask(filename)) continue;
            // make a KlyTask for each valid task file
            task = new KlyTask(filename, this);
            // only include tasks that have started
            if (!task.hasStarted()) continue;
            taskList.add(task);
        }

        return taskList;
    }

    private class TaskAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return taskList.size();
        }

        @Override
        public KlyTask getItem(int position) {
            return taskList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        // Switches between "day" and "days"
        // I think there's a way to do this with android string resources
        // but I also vaguely remember looking into that and changing my mind, so, here's this function
        private String getExpirationString(int expirationLength) {
            if (expirationLength > 1) {
                return expirationLength + " days";
            } else if (expirationLength == 1) {
                return expirationLength + " day";
            } else {
                // TODO: I don't think this ever gets hit
                return "today";
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.task_adapter, container, false);
            }

            final KlyTask task = taskList.get(position);
            if (!task.hasExpired()) {
                // If the task hasn't expired, show the expiration date, and the resolution text

                // Set the task description
                TextView taskDesc = convertView.findViewById(R.id.taskDesc);
                taskDesc.setText(task.getDescription());

                // Set the expiration date text
                int expirationLength = DateUtils.getInstance().getExpirationLength(task.getExpiration());
                String expirationStr = getExpirationString(expirationLength);
                TextView expiration = convertView.findViewById(R.id.expirationDate);
                expiration.setText(expirationStr);
                expiration.setTextColor(getResources().getColor(R.color.buttonText)); // we have to set the color in case we're using a recycled view
                // Reuse the expiration object to make sure the label is also the right color
                expiration = convertView.findViewById(R.id.expirationLabel);
                expiration.setTextColor(getResources().getColor(R.color.buttonText));

                // Set the resolution text
                final TextView resolveTask = convertView.findViewById(R.id.resolveTask);
                resolveTask.setText("Tap to " + task.getResolutionText());
            } else {
                // If the task has expired, show the expiration text, and change the formatting

                // Set the task description to the expired text
                TextView taskDesc = convertView.findViewById(R.id.taskDesc);
                taskDesc.setText(task.getExpiredText());

                // Set the expiration date to say "past due"
                TextView expiration = convertView.findViewById(R.id.expirationDate);
                expiration.setText(R.string.past_due);
                expiration.setTextColor(getResources().getColor(R.color.expiredTask));
                // Reuse the expiration object to set the color of the expiration date label
                expiration = convertView.findViewById(R.id.expirationLabel);
                expiration.setTextColor(getResources().getColor(R.color.expiredTask));

                // Set the resolution text to the generic version
                final TextView resolveTask = convertView.findViewById(R.id.resolveTask);
                resolveTask.setText(R.string.resolve_generic);
            }

            final LinearLayout taskItem = convertView.findViewById((R.id.taskItemL));
            taskItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resolveTask(position, task);
                }
            });

            return convertView;
        }
    }
}
