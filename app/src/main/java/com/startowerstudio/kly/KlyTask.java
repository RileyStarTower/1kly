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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Riley on 1/27/2018.
 */

public class KlyTask {
    private String description, filename, expiredText, resolutionText;
    private Calendar start, expiration;
    private int id;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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

    // TODO: return the rest of the thing as well
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
    // objects out of the second and third
    // TODO: mostly just used for testing...
    KlyTask(String description, String filename, String expiredText, String resolutionText) {
        this.description = description;
//        this.start = DateUtils.getInstance().mkCalendar(start);
//        this.expiration = DateUtils.getInstance().mkCalendar(expiration);
        this.filename = filename;
        this.expiredText = expiredText;
        this.resolutionText = resolutionText;
        this.start = DateUtils.getInstance().mkStartDate();
        this.expiration = DateUtils.getInstance().mkEndDate(this.start);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Constructor that reads from a database result
    KlyTask(Cursor taskCursor, int fileCount, Context context) {
        // TODO: this crashes if the cursor doesn't have data for those columns
        // We could catch a CursorIndexOutOfBounds error, or just check the size
        // But we're already doing that before we call this
        // Maybe it would be better to do that check here, and make a fake task
        // with a description like, "please contact your system administrator"?
        if (taskCursor.getCount() > 0) {
            id = taskCursor.getInt(0);
            description = taskCursor.getString(1);
            expiredText = taskCursor.getString(2);
            resolutionText = taskCursor.getString(3);
        } else {
            id = -1;
            description = context.getResources().getString(R.string.invalid_task);
            expiredText = description;
            resolutionText = context.getResources().getString(R.string.invalid_task);
        }
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
            outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write((description + "\n").getBytes());
            outputStream.write((expiredText + "\n").getBytes());
            outputStream.write((resolutionText + "\n").getBytes());
            outputStream.write(DateUtils.getInstance().unMkCalendar(start).getBytes());
            outputStream.write(DateUtils.getInstance().unMkCalendar(expiration).getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean hasStarted() {
        return start.before(Calendar.getInstance());
    }

    boolean hasExpired() {
        return expiration.before(Calendar.getInstance());
    }

    // Returns the integer portion of the filename
    int getFileCount() {
        int length = CURRENT_TASKS_BASE.length();
        return Integer.valueOf(filename.substring(length, filename.length()));
    }
}
