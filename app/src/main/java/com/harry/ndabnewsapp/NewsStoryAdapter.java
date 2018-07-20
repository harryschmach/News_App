package com.harry.ndabnewsapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * An {@link NewsStoryAdapter} knows how to create a list item layout for each News Story
 * in the data source (a list of {@link NewsStory} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class NewsStoryAdapter extends ArrayAdapter<NewsStory> {


    /**
     * Constructs a new {@link NewsStoryAdapter}.
     *
     * @param context of the app
     * @param newsStories is the list of newsStories, which is the data source of the adapter
     */
    public NewsStoryAdapter(Context context, List<NewsStory> newsStories) {
        super(context, 0, newsStories);
    }

    /**
     * Returns a list item view that displays information about the news story at the given position
     * in the list of newsStories.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_story_list_item, parent, false);
        }

        // Find the NewsStory at the given position in the list of stories
        NewsStory currentNewsStory = getItem(position);

        // Get the title.
        String title = currentNewsStory.getTitle();

        // Get the author.
        String author = currentNewsStory.getAuthor();

        // Get the date published.
        String date = currentNewsStory.getDatePublished();

        // Get the section.
        String section = currentNewsStory.getSection();

        // Find the TextView with view holding the author
        TextView authorView = listItemView.findViewById(R.id.news_author);
        // Display the author in that TextView
        authorView.setText(author);

        // Find the TextView with the title
        TextView titleView = listItemView.findViewById(R.id.news_headline);
        // Display the title in that TextView
        titleView.setText(title);

        // Create a new Date object from the time in milliseconds of the earthquake
        // need to format the date string to clip that TZ nonsense
        // "webPublicationDate": "2018-07-16T02:15:24Z",
        String dateForDisplay = date.substring(0,10);

        // Find the TextView with view ID date
        TextView dateView = listItemView.findViewById(R.id.news_date);
        // Format the date string (i.e. "Mar 3, 1984")
        dateView.setText(dateForDisplay);

        // Find the TextView with view ID section
        TextView sectionView = listItemView.findViewById(R.id.news_section);
        // Display the section category of the current earthquake in that TextView
        sectionView.setText(section);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}