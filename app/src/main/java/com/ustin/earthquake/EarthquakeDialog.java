package com.ustin.earthquake;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;

public class EarthquakeDialog extends DialogFragment {
    private static String DIALOG_STRING = "DIALOG_STRING";

    public static EarthquakeDialog newInstance(Context context, Quake quake) {
        EarthquakeDialog fragment = new EarthquakeDialog();
        Bundle args = new Bundle();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = simpleDateFormat.format(quake.getDate());
        String quakeText = dateString + "\n"
                + "Magnitude " + quake.getMagnitude() + "\n"
                + quake.getDetails() + "\n"
                + "Location " + quake.getLocation();
        args.putString(DIALOG_STRING, quakeText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quake_details, container, false);
        String title = getArguments().getString(DIALOG_STRING);
        TextView textView = (TextView) view.findViewById(R.id.quakeDetailsTextView);
        textView.setText(title);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstaceState) {
        Dialog dialog = super.onCreateDialog(savedInstaceState);
        dialog.setTitle("Earthquake Details");
        return dialog;
    }
}
