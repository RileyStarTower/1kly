package com.startowerstudio.kly.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Riley on 1/27/2018.
 */

public class TaskQueries extends KlyDB {
    private static final TaskQueries ourInstance = new TaskQueries();

    public static TaskQueries getInstance() {
        return ourInstance;
    }

    private TaskQueries() {
    }

    public Cursor getOneTask(Context context) {
        return getNTasks(context, 1);
    }

    // TODO: fix the day offset
    public Cursor getNTasks(Context context, int n) {
        // Selects the task description, the expiration text, and the resolution text, for a random task
        String query = "SELECT kt." + TASK_COL_ID + ", kt." + TASK_COL_DESC + ", kc." + CAT_COL_EXP_TEXT + ", kc." + CAT_COL_RES_TEXT +
                " FROM " + TASK_TABLE_NAME + " kt LEFT OUTER JOIN " +
                CAT_TABLE_NAME + " kc ON kt." + TASK_COL_CAT + " = kc." + CAT_COL_ID +
                " WHERE kt." + TASK_COL_DATE + " > strftime(\'%s\', \'now\', \'30 day\')" +  // don't pull in recent tasks
                " ORDER BY RANDOM() LIMIT " + n;

        TaskDatabaseHelper taskDbHelper = new TaskDatabaseHelper(context);
        SQLiteDatabase taskDbReadableDatabase = taskDbHelper.getReadableDatabase();
        Cursor cursor = runQuery(taskDbReadableDatabase, query);
        taskDbHelper.close();

        return cursor;
    }

    public void updateTaskDate(Context context, int id) {
        // build the new date
        ContentValues dateColAndVal = new ContentValues(1);
        dateColAndVal.put(TASK_COL_DATE, System.currentTimeMillis() / 1000);

        // build the where clause
        String where = TASK_COL_ID + " = ?";
        String[] arg = {Integer.toString(id)};

        TaskDatabaseHelper taskDbHelper = new TaskDatabaseHelper(context);
        SQLiteDatabase taskDbWritableDatabase = taskDbHelper.getWritableDatabase();
        taskDbWritableDatabase.update(TASK_TABLE_NAME, dateColAndVal, where, arg);
        taskDbHelper.close();
    }
}
