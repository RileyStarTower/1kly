package com.startowerstudio.kly.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Created by Riley on 1/27/2018.
 */

public class ManifestQueries extends KlyDB {
    private static final ManifestQueries ourInstance = new ManifestQueries();

    private final String PREAMBLE = "SELECT " + MANIFEST_COL_ID + " AS \'_id\', " + MANIFEST_COL_NAME + ", "
            + MANIFEST_COL_OCC + ", " + MANIFEST_COL_LOC + " FROM " + MANIFEST_TABLE_NAME + " WHERE ";

    public static ManifestQueries getInstance() {
        return ourInstance;
    }

    private ManifestQueries() {
    }

    // Returns passengers that match a given name string exactly
    public Cursor getByNameID(SQLiteDatabase db, String name) {
        // name is a username coming from the manifest activity,
        // which we can use as an ID because all usernames are unique
        String query = "SELECT " + MANIFEST_COL_ID + ", " + MANIFEST_COL_NAME + ", "
                + MANIFEST_COL_OCC + ", " + MANIFEST_COL_LOC +
                " FROM " + MANIFEST_TABLE_NAME + " WHERE " + MANIFEST_COL_NAME
                + " = '" + name + "'";

        return runQuery(db, query);
    }

    // Returns passengers that match a given name string approximately
    public Cursor getByNames(SQLiteDatabase db, String names) {
        Cursor cursor = runQuery(db, massageNames(names));
        return cursor;
    }

    // Builds a query out of a set of name strings
    String massageNames(String rawNames) {
        // break up the string by spaces, and match against each one individually;
        String[] names = rawNames.trim().split(" ");
        StringBuilder namesQuery = new StringBuilder();
        for (String name : names) {
            namesQuery.append("OR " + MANIFEST_COL_NAME + " LIKE " + wrapName(name) + "\n");
        }

        return PREAMBLE + namesQuery.substring(3);
    }

    // Retrieves passengers based on a given location string
    public Cursor getByLocation(SQLiteDatabase db, String location) {
        String query = PREAMBLE + MANIFEST_COL_LOC + " LIKE \'" + location.replace('*', '%') + "\'";
        return runQuery(db, query);
    }

    // Wrap a name in wildcard characters depending on the length of the name
    @NonNull
    private String wrapName(String name) {
        if (name.length() > 1) {
            // three or more characters, check for contains
            return "'%" + name + "%'";
        } else {
            // 1 or 2 characters, check for begins
            return "'" + name + "%'";
        }
    }

    // Returns a breakdown of passenger occupations, and the ratio of each occupation
    public Cursor getOccupationBreakdown(Context context) {
        String query = "SELECT " + MANIFEST_COL_OCC + ", count(" + MANIFEST_COL_NAME +
                ") as '_id', (SELECT count(*) FROM " + MANIFEST_TABLE_NAME + ") as 'total_count' FROM "
                + MANIFEST_TABLE_NAME + " GROUP BY " + MANIFEST_COL_OCC + " ORDER BY count(" +
                MANIFEST_COL_NAME + ") DESC";

        ManifestDatabaseHelper manifestDbHelper = new ManifestDatabaseHelper(context);
        SQLiteDatabase manifestDbReadableDatabase = manifestDbHelper.getReadableDatabase();
        return runQuery(manifestDbReadableDatabase, query);
    }

    // Returns the connections of a given passenger
    public Cursor getConnections(SQLiteDatabase db, String username) {
        String query = "SELECT " + MANIFEST_COL_NAME + " as '_id', " + MANIFEST_COL_CONN + " FROM "
                + MANIFEST_TABLE_CONN + " WHERE " + MANIFEST_COL_NAME + " = '" + username + "'";

        return runQuery(db, query);
    }

    // This query just generates a bunch of data to put in the Resident Demographics section of the Destination activity
    // I'm putting it in ManifestQueries because it's closest to that
    public Cursor getResidents(Context context) {
        ManifestDatabaseHelper manifestDbHelper = new ManifestDatabaseHelper(context);
        SQLiteDatabase manifestDbReadableDatabase = manifestDbHelper.getReadableDatabase();

        // the type column is called "_id" because android requires a column with that exact name
        String query = "SELECT \'Human\' AS \'_id\', \'0.00%\' AS \'count\' " +
                "UNION ALL SELECT \'Robot\' AS \'_id\', \'100.00%\' AS \'count\'" +
                "UNION ALL SELECT \'Local\' AS \'_id\', \'-\' AS \'count\'" +
                "UNION ALL SELECT \'Nonhuman\' AS \'_id\', \'0.00%\' AS \'count\'";

        return runQuery(manifestDbReadableDatabase, query);
    }

    // This is the same as getResidents, but for after you arrive
    public Cursor getEndResidents(Context context) {
        ManifestDatabaseHelper manifestDbHelper = new ManifestDatabaseHelper(context);
        SQLiteDatabase manifestDbReadableDatabase = manifestDbHelper.getReadableDatabase();

        // the type column is called "_id" because android requires a column with that exact name
        String query = "SELECT \'Human\' AS \'_id\', \'0.23%\' AS \'count\' " +
                "UNION ALL SELECT \'Robot\' AS \'_id\', \'99.76%\' AS \'count\'" +
                "UNION ALL SELECT \'Local\' AS \'_id\', \'-\' AS \'count\'" +
                "UNION ALL SELECT \'Nonhuman\' AS \'_id\', \'0.00%\' AS \'count\'";

        return runQuery(manifestDbReadableDatabase, query);
    }
}
