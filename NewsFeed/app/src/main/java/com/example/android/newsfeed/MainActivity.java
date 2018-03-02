package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Event>>{

    private final String URL_QUERY =
            "http://content.guardianapis.com/search?show-tags=contributor&page-size=20&api-key=9566393d-2cd8-4e40-bc23-babac7128bb6";
    private final int LOADER_ID = 1;
    private CustomAdapter customAdapter;
    private TextView emptystateTextView;
    private ProgressBar progressBar;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if there is internet connection
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        emptystateTextView = findViewById(R.id.txt_empty_state);
        progressBar = findViewById(R.id.progress);

        LoaderManager loaderManager = getLoaderManager();
        ListView listView = findViewById(R.id.news_list);
        customAdapter = new CustomAdapter(this, new ArrayList<Event>());
        listView.setAdapter(customAdapter);
        listView.setEmptyView(emptystateTextView);

        if(isConnected){
            loaderManager.initLoader(LOADER_ID, null, this);
        }else{
            emptystateTextView.setText(R.string.no_connection);
            progressBar.setVisibility(View.GONE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event currentEvent = customAdapter.getItem(i);
                openWebPage(currentEvent.getWebUrl());
            }
        });
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int i, Bundle bundle) {
        return new EventLoader(this, URL_QUERY);
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> events) {
        progressBar.setVisibility(View.GONE);
        emptystateTextView.setText(R.string.no_data);
        customAdapter.clear();
        if (events != null && !events.isEmpty())
            customAdapter.addAll(events);
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        customAdapter.clear();
    }

    /**
     * opens the event url in the users browser
     * @param url the url to open
     */
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
