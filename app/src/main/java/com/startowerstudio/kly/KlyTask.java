package com.startowerstudio.kly;

import android.content.Context;
import android.database.Cursor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * Created by Riley on 1/27/2018.
 */

public class KlyTask {
    private String description, filename, expiredText, resolutionText;
    private Calendar start, expiration;
    private int id;

    static final String CURRENT_TASKS_BASE = "currentTask";

    /*
     * Getters and setters
     */
    Calendar getStart() {
        return start;
    }

    public int getId() {
        return id;
    }

    String getDescription() {
        return description;
    }

    String getFilename() {
        return filename;
    }

    String getExpiredText() {
        return expiredText;
    }

    String getResolutionText() {
        return resolutionText;
    }

    Calendar getExpiration() {
        return expiration;
    }

    @Override
    public String toString() {
        return "KlyTask{" +
                "description='" + description + '\'' +
                '}';
    }


    /*
     * Constructors
     */

    // Direct constructor takes three strings, and we build Calendar
    // objects out of the second and third.
    // Only used for testing
    KlyTask(String description, String filename, String expiredText, String resolutionText) {
        this.description = description;
        this.filename = filename;
        this.expiredText = expiredText;
        this.resolutionText = resolutionText;
        this.start = DateUtils.getInstance().mkStartDate();
        this.expiration = DateUtils.getInstance().mkEndDate(this.start);
        id = -2; // error ID for these test tasks
    }

    // Constructor that reads from file
    KlyTask(String filename, Context context) {
        BufferedReader fileIn;
        FileInputStream fs;
        DataInputStream ds;
        this.filename = filename;
        try {
            // this seems like a mess, why does this require 3 lines???
            fs = context.openFileInput(filename);
            ds = new DataInputStream(fs);
            fileIn = new BufferedReader(new InputStreamReader(ds));
            description = fileIn.readLine();
            expiredText = fileIn.readLine();
            resolutionText = fileIn.readLine();
            String startString = fileIn.readLine();
            start = DateUtils.getInstance().mkCalendar(startString);
            String expirationString = fileIn.readLine();
            expiration = DateUtils.getInstance().mkCalendar(expirationString);
            id = Integer.valueOf(fileIn.readLine());
            // probably don't need all 3 of these, but I don't want to check, and it doesn't seem to be causing issues
            fileIn.close();
            ds.close();
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Constructor that reads from a database result
    KlyTask(Cursor taskCursor, int fileCount, Context context) {
        if (taskCursor.getCount() > 0) {
            // If there is a task here, pull in the information
            id = taskCursor.getInt(0);
            description = taskCursor.getString(1);
            expiredText = taskCursor.getString(2);
            resolutionText = taskCursor.getString(3);
        } else {
            // If we don't actually have a task, we can build a default error task
            id = -1;
            description = context.getResources().getString(R.string.invalid_task);
            expiredText = description;
            resolutionText = context.getResources().getString(R.string.resolve_invalid);
        }
        // however we build the task, we can create a start and expiration date, and set the filename
        start = DateUtils.getInstance().mkStartDate();
        expiration = DateUtils.getInstance().mkEndDate(start);
        filename = CURRENT_TASKS_BASE + fileCount;
    }

    /*
     * Other methods
     */

    // Writes the task to file
    void writeTask(Context context) {
        FileOutputStream outputStream;
        try {
            // The order here is extremely important, since we have to load the data in the same order
            outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write((description + "\n").getBytes());
            outputStream.write((expiredText + "\n").getBytes());
            outputStream.write((resolutionText + "\n").getBytes());
            outputStream.write(DateUtils.getInstance().unMkCalendar(start).getBytes());
            outputStream.write(DateUtils.getInstance().unMkCalendar(expiration).getBytes());
            outputStream.write((id + "\n").getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Return true if the start date of the task is in the past, false otherwise
    boolean hasStarted() {
        return start.before(Calendar.getInstance());
    }

    // Return true if the expiration date of the task is in the past, false otherwise
    boolean hasExpired() {
        return expiration.before(Calendar.getInstance());
    }

    // Returns the integer portion of the filename
    int getFileCount() {
        int length = CURRENT_TASKS_BASE.length();
        return Integer.valueOf(filename.substring(length, filename.length()));
    }
}
