package com.harry.ndabnewsapp;

/**
 * A {@link NewsStory} object contains information related to a single news article.
 */
public class NewsStory {

    /** Website URL of the news story */
    private String mUrl;
    /** Author of the news story, if available*/
    private String mAuthor;
    /** Section news story */
    private String mSection;
    /** Title of the news story */
    private String mTitle;
    /** Date of publication of the news story */
    private String mDatePublished;



    /**
     * Constructs a new {@link NewsStory} object.
     *
     * @param url is the website URL to find more details about the earthquake
     * @param author is contributor of story
     * @param section is section that story belongs to
     * @param dateofPub is date of publication
     * @param title is title of piece
     */
    public NewsStory(String url, String author, String section, String title, String dateofPub) {
        mUrl = url;
        mAuthor = author;
        mSection = section;
        mTitle = title;
        mDatePublished = dateofPub;
    }

    /**
     * Returns the website URL to find more information about the earthquake.
     */
    public String getUrl() {
        return mUrl;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getSection() {
        return mSection;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDatePublished() {
        return mDatePublished;
    }
}
