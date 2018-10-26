package com.example.omar.cs193a;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        listener = this;



    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String bootKey = getResources().getString(R.string.switch_preference_boot);
        if(key.equals(bootKey)) {
            if(sharedPreferences.getBoolean(bootKey, false)) {
                getActivity().registerReceiver(new BootReceiver(), new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
            }
            else {
                try {
                    getActivity().unregisterReceiver(new BootReceiver());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
