package com.ustin.earthquake;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.Date;

public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter adapter;

    private static final String TAG = "<==EARTHQUAKE_LIST_FRAGMENT==> ";
    private Handler handler = new Handler();

    // метод загружает данные из бд и создает фрагменты для отображения пользователю
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{EarthquakeProvider.KEY_SUMMARY},
                new int[]{android.R.id.text1}, 0);
        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        Log.w(TAG, "onActivityCreated");
        refreshEarthquakes();
    }

    // метод обновляет экран отображения данных каждый раз при появлении новой записи
    public void refreshEarthquakes() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().restartLoader(0, null, EarthquakeListFragment.this);
                getActivity().startService(new Intent(getActivity(), EarthquakeService.class));
                Log.w(TAG, "refreshEarthquakes");
            }
        });
    }

    // метод отображает данные из бд основанные на настройке отображения(по магнитуде)
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{EarthquakeProvider.KEY_ID, EarthquakeProvider.KEY_SUMMARY};
        MainActivity earthquakeActivity = (MainActivity) getActivity();
        String minMag = EarthquakeProvider.KEY_MAGNITUDE + " > " + earthquakeActivity.minimumMagnitude;
        CursorLoader loader = new CursorLoader(getActivity(),
                EarthquakeProvider.CONTENT_URI,
                projection,
                minMag,
                null,
                null);
        Log.w(TAG, "onCreateLoader");
        return loader;
    }

    // часть LoaderManager
    @Override
    public void onLoadFinished(@NonNull androidx.loader.content.Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        Log.w(TAG, "onLoadFinished");
    }

    // часть LoaderManager
    @Override
    public void onLoaderReset(@NonNull androidx.loader.content.Loader<Cursor> loader) {
        adapter.swapCursor(null);
        Log.w(TAG, "onLoaderReset");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ContentResolver cr = getActivity().getContentResolver();
        Cursor result = cr.query(ContentUris.withAppendedId(EarthquakeProvider.CONTENT_URI, id),
                null, null, null, null, null);

        if (result.moveToFirst()) {
            Date date = new Date(result.getLong(result.getColumnIndex(EarthquakeProvider.KEY_DATE)));
            String details = result.getString(result.getColumnIndex(EarthquakeProvider.KEY_DETAILS));
            double magnitude = result.getDouble(result.getColumnIndex(EarthquakeProvider.KEY_MAGNITUDE));
            String location = result.getString(result.getColumnIndex(EarthquakeProvider.KEY_LOCATION));

            Quake quake = new Quake(date, details, magnitude, location);

            DialogFragment newFragment = EarthquakeDialog.newInstance(getActivity(), quake);
            newFragment.show(getFragmentManager(), "dialog");
        }

        Log.w(TAG, "onListItemClick " + id);
    }
}