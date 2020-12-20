package com.ustin.earthquake;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

// класс должен искать запись в бд(не работает надо пофиксить)
public class EarthquakeSearchResults extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    private static final String TAG = "<==EARTHQUAKE_SEARCH_RESULT==>";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate");
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[]{EarthquakeProvider.KEY_SUMMARY},
                new int[]{android.R.id.text1}, 0);

        setListAdapter(adapter);
        Log.w(TAG, "onCreate setListAdapter");

        getLoaderManager().initLoader(0, null, this);
        Log.w(TAG, "onCreate getLoaderManager");

        parseIntent(getIntent());
        Log.w(TAG, "onCreate parseIntent(getIntent())");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(getIntent());
        Log.w(TAG, "onNewIntent");
    }

    private String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";

    private void parseIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            Bundle args = new Bundle();
            args.putString(QUERY_EXTRA_KEY, searchQuery);
            getLoaderManager().restartLoader(0, null, this);
        }
        Log.w(TAG, "parseIntent");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = "0";
        if (args != null) {
            query = args.getString(QUERY_EXTRA_KEY);
        }
        String[] projection = {EarthquakeProvider.KEY_ID, EarthquakeProvider.KEY_SUMMARY};
        String where = EarthquakeProvider.KEY_SUMMARY + " LIKE \"%" + query + "%\"";
        String[] whereArgs = null;
        String sortOrder = EarthquakeProvider.KEY_SUMMARY + " COLLATE LOCALIZED ASC";
        Log.w(TAG, "onCreateLoader");
        return new CursorLoader(this, EarthquakeProvider.CONTENT_URI, projection, where, whereArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        Log.w(TAG, "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        Log.w(TAG, "onLoaderReset");
    }
}
