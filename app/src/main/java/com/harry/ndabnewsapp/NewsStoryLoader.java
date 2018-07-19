package com.harry.ndabnewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class NewsStoryLoader extends AsyncTaskLoader<List<NewsStory>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsStoryLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link NewsStoryLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsStoryLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<NewsStory> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of newsStories.
        List<NewsStory> newsStories = QueryUtils.fetchNewsStories(mUrl);
        return newsStories;
    }
}
