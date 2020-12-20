package com.ustin.earthquake;

import android.os.Bundle;
import android.preference.PreferenceFragment;

// класс СОЗДАВАЛ фрагмент настроек программы (в данный момент не используется)
public class UserPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
    }
}
