package com.ustin.earthquake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

// !!!!добавить карты, графики по разным критериям, возможно добавить прогнозирование!!!!
public class MainActivity extends AppCompatActivity {

    static final private int SHOW_PREFERENCES = 1;
    public int minimumMagnitude = 0;
    public boolean autoUpdateChecked = false;
    public int updateFreq = 0;
    private static final String TAG = "<==MAIN_ACTIVITY==> ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w(TAG, "onCreate");
        updateFromPreferences();
        Log.w(TAG, "onCreate updateFromPreferences");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        Log.w(TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        Log.w(TAG, "onCreate");
        switch (menuItem.getItemId()) {
            case (R.id.menu_refresh): {
                startService(new Intent(this, EarthquakeService.class));
                return true;
            }
            case (R.id.menu_preferences): {
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivityForResult(i, SHOW_PREFERENCES);
                Log.w(TAG, "onOptionsItemSelected true");
                return true;
            }
            default:
                return false;
        }
    }

    // обновляет экран в соответствии с настройками
    private void updateFromPreferences() {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        minimumMagnitude = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
        updateFreq = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
        Log.w(TAG, "updateFromPreferences");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_PREFERENCES) {
            updateFromPreferences();
            startService(new Intent(this, EarthquakeService.class));
        }
    }
}
