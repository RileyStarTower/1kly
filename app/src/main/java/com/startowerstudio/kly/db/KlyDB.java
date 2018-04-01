package com.startowerstudio.kly.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Riley on 1/27/2018.
 * Base class for any database query classes
 * Constants are for the SQL schema
 */

class KlyDB {
    final String MANIFEST_TABLE_NAME = "kly_manifest";
    final String MANIFEST_TABLE_CONN = "kly_connections";
    final String MANIFEST_COL_ID = "manifest_id";
    public final String MANIFEST_COL_NAME = "username";
    public final String MANIFEST_COL_OCC = "occupation";
    public final String MANIFEST_COL_LOC = "location";
    public final String MANIFEST_COL_CONN = "connection";

    final String TASK_TABLE_NAME = "kly_tasks";
    final String TASK_COL_ID = "task_id";
    final String TASK_COL_DESC = "description";
    final String TASK_COL_CAT = "category_id";
    final String TASK_COL_DATE = "most_recent_use";
    final String CAT_TABLE_NAME = "kly_categories";
    final String CAT_COL_ID = "category_id";
    final String CAT_COL_EXP_TEXT = "expiration_text";
    final String CAT_COL_RES_TEXT = "resolution_text";

    // Helper function to run a given query
    Cursor runQuery(SQLiteDatabase db, String query) {
        Cursor cursor = db.rawQuery(query, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}
