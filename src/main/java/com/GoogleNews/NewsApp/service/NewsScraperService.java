package com.GoogleNews.NewsApp.service;
import com.GoogleNews.NewsApp.utility.ResourceNotFoundException;
import com.GoogleNews.NewsApp.utility.ScraperConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;


@Slf4j
@Service
public class NewsScraperService {

    /**
     * Fetches news article URLs based on the provided search term from Google News.
     * Navigates to Google News, searches for the given term, captures a screenshot of the search results,
     * and extracts URLs from the articles.
     *
     * @param searchTerm The search term to query news articles.
     * @return A list of maps containing article URLs.
     * @throws Exception if something goes wrong during the scraping process.
     */
    public List<Map<String, String>> fetchNewsUrls(String searchTerm) throws Exception {
        List<Map<String, String>> newsUrls = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();  // To track duplicate URLs

        try (Playwright playwright = Playwright.create()) {
            log.info(ScraperConstants.PLAYWRIGHT_BROWSER_STARTED);
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Navigate to Google News
            log.info(ScraperConstants.NAVIGATING_TO_GOOGLE_NEWS);
            page.navigate(ScraperConstants.GOOGLE_NEWS_URL);
//            page.waitForTimeout(ScraperConstants.DEFAULT_WAIT_TIMEOUT);

            // Search for the term
            log.info(ScraperConstants.SEARCHING_FOR_TERM, searchTerm);
            page.waitForSelector(ScraperConstants.INPUT_TEXT_SELECTOR, new Page.WaitForSelectorOptions().setTimeout(ScraperConstants.SELECTOR_TIMEOUT));
            page.fill(ScraperConstants.INPUT_TEXT_SELECTOR, searchTerm);
            page.keyboard().press(ScraperConstants.ENTER_KEY);

            // Wait for search results to load
            log.info(ScraperConstants.SEARCH_RESULTS_LOADED);
            page.waitForTimeout(ScraperConstants.DEFAULT_WAIT_TIMEOUT);

            // Capture screenshot after loading search results
            log.info(ScraperConstants.CAPTURING_SCREENSHOT);
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(ScraperConstants.SEARCH_RESULTS_SCREENSHOT)).setFullPage(true));
            log.info(ScraperConstants.SCREENSHOT_SAVED, ScraperConstants.SEARCH_RESULTS_SCREENSHOT);

            // Get article URLs and Titles
            log.info(ScraperConstants.EXTRACTING_URLS_AND_TITLES);
            List<ElementHandle> articles = page.querySelectorAll(ScraperConstants.ARTICLE_ANCHOR_SELECTOR);

            // Reverse loop through the articles
            for (int i = articles.size() - 1; i >= 0; i--) {
                ElementHandle article = articles.get(i);
                String url = article.getAttribute(ScraperConstants.HREF_ATTRIBUTE);
                String title = article.innerText();// Assume the title is the text inside the anchor tag
                // Only add articles with non-empty titles and valid URLs
                if (url != null && !url.contains(ScraperConstants.GOOGLE_URL_FRAGMENT) && title != null && !title.trim().isEmpty()) {
                    String fullUrl = ScraperConstants.GOOGLE_NEWS_PREFIX + url;

                    // Check if the URL is already added (to avoid duplicates)
                    if (!seenUrls.contains(fullUrl)) {
                        Map<String, String> newsItem = new HashMap<>();
                        newsItem.put(ScraperConstants.TITLE_KEY, title);
                        newsItem.put(ScraperConstants.URL_KEY, fullUrl);
                        newsUrls.add(newsItem);
                        seenUrls.add(fullUrl);  // Mark this URL as seen
                        log.info(ScraperConstants.URL_AND_TITLE_FOUND, fullUrl, title);
                    }
                }
            }

            log.info(ScraperConstants.EXTRACTED_URLS_AND_TITLES_COUNT, newsUrls.size());
        } catch (Exception e) {
            log.error(ScraperConstants.ERROR_FETCHING_NEWS_URLS_AND_TITLES, e);
            throw e;  // Throwing exception to be handled by global exception handler
        }

        return newsUrls;
    }
}
