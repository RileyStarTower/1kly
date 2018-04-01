package com.startowerstudio.kly;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.startowerstudio.kly.db.ManifestDatabaseHelper;
import com.startowerstudio.kly.db.ManifestQueries;

public class PassengerProfileActivity extends KlyActivity {
    public static final String EXTRA_LOCATION = "com.sts.riley.a1kly.LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startCountdown();

        Intent intent = getIntent();
        String username = intent.getStringExtra(Manifest.EXTRA_PASSENGER);

        Cursor passengerData = loadPassenger(username);

        drawPassenger(passengerData);

        Cursor connectionData = loadConnections(username);
        final ListView listView = (ListView) findViewById(R.id.connectionsList);
        if (connectionData.getCount() > 0) {
            ProfileAdapter profileAdapter = new ProfileAdapter(this, connectionData);
            listView.setAdapter(profileAdapter);
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.GONE);
        }
    }

    private Cursor loadPassenger(String username) {
        ManifestDatabaseHelper manifestDbHelper = new ManifestDatabaseHelper(this);
        SQLiteDatabase manifestDbWritableDatabase = manifestDbHelper.getWritableDatabase();
        return ManifestQueries.getInstance().getByNameID(manifestDbWritableDatabase, username);
    }

    private Cursor loadConnections(String username) {
        ManifestDatabaseHelper manifestDbHelper = new ManifestDatabaseHelper(this);
        SQLiteDatabase manifestDbReadableDatabase = manifestDbHelper.getReadableDatabase();
        return ManifestQueries.getInstance().getConnections(manifestDbReadableDatabase, username);
    }

    private void drawPassenger(Cursor passengerData) {
        final String username = passengerData.getString(passengerData.getColumnIndex(ManifestQueries.getInstance().MANIFEST_COL_NAME));
        final String occupation = passengerData.getString(passengerData.getColumnIndex(ManifestQueries.getInstance().MANIFEST_COL_OCC));
        final String location = passengerData.getString(passengerData.getColumnIndex(ManifestQueries.getInstance().MANIFEST_COL_LOC));

        TextView usernameTextView = (TextView) findViewById(R.id.passengerUsername);
        usernameTextView.setText(username);

        TextView occupationTextView = (TextView) findViewById(R.id.passengerOccupation);
        occupationTextView.setText(occupation);

        TextView locationTextView = (TextView) findViewById(R.id.passengerLocation);
        locationTextView.setText(location);
    }

    public void nearbyPassengers(View view) {
        String location = ((TextView) view).getText().toString();   // get the location string from the view
        location = '*' + location.substring(1);
        Intent intent = new Intent(this, Manifest.class);
        intent.putExtra(EXTRA_LOCATION, location);
        intent.setAction(EXTRA_LOCATION);
        startActivity(intent);
    }

    // This just calls the base function with the specific string
    public void loginDialog(View view) {
        super.loginDialog(R.string.login_personal_info);
    }

    private class ProfileAdapter extends CursorAdapter {
        ProfileAdapter(Context context, Cursor cursor) {
            super (context, cursor, 0);
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            TextView textViewUsername = (TextView) view.findViewById(R.id.passengerUsername);
            textViewUsername.setTextColor(getResources().getColor(R.color.dataBlue));
            final String username = cursor.getString(cursor.getColumnIndex(ManifestQueries.getInstance().MANIFEST_COL_CONN));
            textViewUsername.setText(username);
            textViewUsername.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, PassengerProfileActivity.class);
                    intent.putExtra(Manifest.EXTRA_PASSENGER, username);
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
