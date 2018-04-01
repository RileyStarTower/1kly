package com.startowerstudio.kly;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.startowerstudio.kly.db.ManifestDatabaseHelper;
import com.startowerstudio.kly.db.ManifestQueries;

public class Manifest extends KlyActivity {
    public static final String EXTRA_PASSENGER = "com.sts.riley.a1kly.PASSENGER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startCountdown();

        String query = "";


        // Get the query from the intent
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        } else if (PassengerProfileActivity.EXTRA_LOCATION.equals(intent.getAction())) {
            query = intent.getStringExtra(PassengerProfileActivity.EXTRA_LOCATION);
        }

        if (!EtaCountdown.getInstance().timerUp()) {
            if (query.split(":").length == 4) {
                ManifestAdapter manifestAdapter = new ManifestAdapter(this, getLocationCursor(query));
                final ListView listView = (ListView) findViewById(R.id.passengerList);
                listView.setAdapter(manifestAdapter);
                setListViewHeight(listView);
                showUI(query);
            } else if (query.length() > 0) {
                ManifestAdapter manifestAdapter = new ManifestAdapter(this, getNamesCursor(query));
                final ListView listView = (ListView) findViewById(R.id.passengerList);
                listView.setAdapter(manifestAdapter);
                setListViewHeight(listView);
                showUI(query);
            }
        } else {
            if (query.length() > 0) {
                showUI(query);
            }
        }
    }

    // Show information about the results listView
    private void showUI(String query) {
        // Show and update the query display
        TextView resultsTextView = (TextView) findViewById(R.id.queryDisplay);
        String resultsString = getString(R.string.query_display_label) + " \"" + query + "\"";
        resultsTextView.setText(resultsString);
        resultsTextView.setVisibility(View.VISIBLE);

        // Show the column labels
        TextView usernameColumn = (TextView) findViewById(R.id.usernameColumnLabel);
        usernameColumn.setVisibility(View.VISIBLE);
        TextView locationColumn = (TextView) findViewById(R.id.locationColumnLabel);
        locationColumn.setVisibility(View.VISIBLE);
    }

    // Get results for a name-based query
    private Cursor getNamesCursor(String query) {
        ManifestDatabaseHelper manifestDbHelper = new ManifestDatabaseHelper(this);
        SQLiteDatabase manifestDbReadableDatabase = manifestDbHelper.getReadableDatabase();
        return ManifestQueries.getInstance().getByNames(manifestDbReadableDatabase, query);
    }

    // Get results for a location-based query
    private Cursor getLocationCursor(String query) {
        ManifestDatabaseHelper manifestDbHelper = new ManifestDatabaseHelper(this);
        SQLiteDatabase manifestDbReadableDatabase = manifestDbHelper.getReadableDatabase();
        return ManifestQueries.getInstance().getByLocation(manifestDbReadableDatabase, query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manifest_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            onSearchRequested();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private class ManifestAdapter extends CursorAdapter {
        ManifestAdapter(Context context, Cursor cursor) {
            super (context, cursor, 0);
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            TextView textViewUsername = (TextView) view.findViewById(R.id.passengerUsername);
            final String username = cursor.getString(cursor.getColumnIndex(ManifestQueries.getInstance().MANIFEST_COL_NAME));
            textViewUsername.setText(username);
            textViewUsername.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, PassengerProfileActivity.class);
                    intent.putExtra(EXTRA_PASSENGER, username);
                    startActivity(intent);
                }
            });

            TextView textViewLocation = (TextView) view.findViewById(R.id.passengerLocation);
            final String location = cursor.getString(cursor.getColumnIndex(ManifestQueries.getInstance().MANIFEST_COL_LOC));
            textViewLocation.setText(location);
            textViewLocation.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, PassengerProfileActivity.class);
                    intent.putExtra(EXTRA_PASSENGER, username);
                    startActivity(intent);
                }
            });
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.passenger_adapter, parent, false);
        }
    }

}
