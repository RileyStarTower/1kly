package com.startowerstudio.kly.db;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Riley on 1/27/2018.
 * TODO: include that I'm using this thing from ready state software
 */

public class ManifestDatabaseHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "manifest.db";
    private static final int DATABASE_VERSION = 1;

    public ManifestDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }
}
