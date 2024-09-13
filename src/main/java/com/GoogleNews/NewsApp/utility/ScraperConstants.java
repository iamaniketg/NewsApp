package com.GoogleNews.NewsApp.utility;
public class ScraperConstants {

    // Playwright and browser constants
    public static final String PLAYWRIGHT_BROWSER_STARTED = "Starting Playwright and launching browser in non-headless mode.";

    // Google News related constants
    public static final String GOOGLE_NEWS_URL = "https://news.google.com/";
    public static final String NAVIGATING_TO_GOOGLE_NEWS = "Navigating to Google News...";
    public static final String SEARCHING_FOR_TERM = "Searching for term: {}";
    public static final String SEARCH_RESULTS_LOADED = "Search results loaded. Waiting for content to appear...";
    public static final String CAPTURING_SCREENSHOT = "Capturing screenshot of search results...";
    public static final String SCREENSHOT_SAVED = "Screenshot saved as '{}'.";
    public static final String EXTRACTING_URLS = "Extracting article URLs...";
    public static final String URL_FOUND = "Found URL: {}";
    public static final String EXTRACTED_URLS_COUNT = "Finished extracting {} URLs.";
    public static final String ERROR_FETCHING_NEWS_URLS = "Error occurred while fetching news URLs: ";

    // Selectors and attributes
    public static final String INPUT_TEXT_SELECTOR = "input[type='text']";
    public static final String ARTICLE_ANCHOR_SELECTOR = "article a";
    public static final String HREF_ATTRIBUTE = "href";

    // Special keys
    public static final String ENTER_KEY = "Enter";

    // URL fragments and prefix
    public static final String GOOGLE_URL_FRAGMENT = "google";
    public static final String GOOGLE_NEWS_PREFIX = "https://news.google.com";

    // Screenshot file name
    public static final String SEARCH_RESULTS_SCREENSHOT = "search_results.png";

    // JSON keys
    public static final String URL_KEY = "url";

    // Timeouts
    public static final int DEFAULT_WAIT_TIMEOUT = 10000;
    public static final int SELECTOR_TIMEOUT = 60000;

    // Controller constants
    public static final String FETCH_NEWS_REQUEST_RECEIVED = "Received request to fetch news for search term: {}";
    public static final String TITLE_KEY = "title";
    public static final String EXTRACTING_URLS_AND_TITLES = "Extracting article URLs and titles.";
    public static final String URL_AND_TITLE_FOUND = "Found URL: {}, Title: {}";
    public static final String EXTRACTED_URLS_AND_TITLES_COUNT = "Extracted {} article URLs and titles.";
    public static final String ERROR_FETCHING_NEWS_URLS_AND_TITLES = "Error while fetching news URLs and titles.";
}
