package com.harry.ndabnewsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    // Example News Story organized
    // {
    //        "id": "tv-and-radio\/2018\/jul\/16\/sharp-objects-recap-season-one-episode-two-dirt",
    //        "type": "article",
    //        "sectionId": "tv-and-radio",
    //        "sectionName": "Television & radio",
    //        "webPublicationDate": "2018-07-16T02:15:24Z",
    //        "webTitle": "Sharp Objects recap: season one, episode two \u2013 Dirt",
    //        "webUrl": "https:\/\/www.theguardian.com\/tv-and-radio\/2018\/jul\/16\/sharp-objects-recap-season-one-episode-two-dirt",
    //        "apiUrl": "https:\/\/content.guardianapis.com\/tv-and-radio\/2018\/jul\/16\/sharp-objects-recap-season-one-episode-two-dirt",
    //        "tags": [
    //          {
    //            "id": "profile\/rebeccanicholson",
    //            "type": "contributor",
    //            "webTitle": "Rebecca Nicholson",
    //            "webUrl": "https:\/\/www.theguardian.com\/profile\/rebeccanicholson",
    //            "apiUrl": "https:\/\/content.guardianapis.com\/profile\/rebeccanicholson",
    //            "references": [
    //
    //            ],
    //            "bio": "<p>Rebecca Nicholson is a freelance writer&nbsp;<\/p>",
    //            "bylineImageUrl": "https:\/\/static.guim.co.uk\/sys-images\/Guardian\/Pix\/contributor\/2015\/6\/3\/1433328429342\/Rebecca-Nicholson.jpg",
    //            "bylineLargeImageUrl": "https:\/\/uploads.guim.co.uk\/2017\/10\/09\/Rebecca-Nicholson,-L.png",
    //            "firstName": "nicholson",
    //            "lastName": "rebecca"
    //          }
    //        ],
    //        "isHosted": false,
    //        "pillarId": "pillar\/arts",
    //        "pillarName": "Arts"
    //      },

    // Sample border wall query
    // https://content.guardianapis.com/search?q=BORDER%20AND%20WALL&api-key=9450dc58-f15c-42cc-a0a5-3b9b19b4f61d
    // Has contributors
    // https://content.guardianapis.com/search?show-tags=contributor&api-key=9450dc58-f15c-42cc-a0a5-3b9b19b4f61d
    // Full Cali query
    // https://content.guardianapis.com/search?show-tags=contributor&order-by=relevance&q=california&api-key=9450dc58-f15c-42cc-a0a5-3b9b19b4f61d

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link NewsStory} objects.
     */
    public static List<NewsStory> fetchNewsStories(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link NewsStory}s
        List<NewsStory> newsStories = extractResultFromJson(jsonResponse);

        // Return the list of {@link NewsStory}s
        return newsStories;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsStory} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<NewsStory> extractResultFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding newsStories to
        List<NewsStory> newsStories = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or newsStories).
            JSONArray newsStoriesArray = baseJsonResponse.getJSONArray("results");

            // For each news article in the newsStoriesArray, create an {@link NewsStory} object
            for (int i = 0; i < newsStoriesArray.length(); i++) {

                // Get a single newsStory at position i within the list of newsStories
                JSONObject currentNewsStory = newsStoriesArray.getJSONObject(i);

                // Extract the value for the key called "webUrl"
                // this is the url to follow
                String url = currentNewsStory.getString("webUrl");

                // For a given newsStory, extract the JSONObject associated with the
                // key called "tags", which represents returns an object with the author.
                JSONArray tags = currentNewsStory.getJSONArray("tags");

                // Extract the Author name from the tags block
                String authorName;
                try {
                    JSONObject tagsBlock = tags.getJSONObject(0);
                    authorName = tagsBlock.getString("webTitle");
                }catch (Exception e){
                    Log.v(LOG_TAG, e.toString());
                    authorName = "No contributor found";
                }

                // Extract the Section it belongs to
                String sectionName = currentNewsStory.getString("sectionName");


                // Extract the value for the key called "title"
                // Extract the title
                String titleOfPublication = currentNewsStory.getString("webTitle");

                // Extract the Date
                String dateOfPublicationExtracted = currentNewsStory.getString("webPublicationDate");

                // Create a new {@link NewsStory} object with the magnitude, location, time,
                // and url from the JSON response.
                NewsStory newsStory = new NewsStory(url, authorName, sectionName, titleOfPublication, dateOfPublicationExtracted);

                // Add the new {@link NewsStory} to the list of newsStories.
                newsStories.add(newsStory);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of newsStories
        return newsStories;
    }

}
