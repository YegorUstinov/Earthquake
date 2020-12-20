package com.ustin.earthquake;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

// класс создает активити настроек программы
public class PreferencesActivity extends PreferenceActivity {

    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
    public static final String PREF_MIN_MAG = "PREF_MIN_MAG";
    public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";

    private static final String TAG = "MY_LOG: ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
        Log.d(TAG, " created");
    }
}
