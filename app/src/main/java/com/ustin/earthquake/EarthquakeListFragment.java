package com.ustin.earthquake;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter adapter;

    private static final String TAG = "<==EARTHQUAKE_LIST_FRAGMENT==> ";
    private Handler handler = new Handler();

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

    @Override
    public void onLoadFinished(@NonNull androidx.loader.content.Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        Log.w(TAG, "onLoadFinished");
    }

    @Override
    public void onLoaderReset(@NonNull androidx.loader.content.Loader<Cursor> loader) {
        adapter.swapCursor(null);
        Log.w(TAG, "onLoaderReset");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //String where = EarthquakeProvider.KEY_ID + "=" + id;
        //String whereArgs[] = null;
        //ContentResolver cr = getActivity().getContentResolver();
        //cr.delete(EarthquakeProvider.CONTENT_URI, where, whereArgs);
        Log.w(TAG, "onListItemClick " + id);
    }
}