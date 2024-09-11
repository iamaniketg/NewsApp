package com.GoogleNews.NewsApp.service;
import com.GoogleNews.NewsApp.utility.ResourceNotFoundException;
import com.GoogleNews.NewsApp.utility.ScraperConstants;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

        try (Playwright playwright = Playwright.create()) {
            log.info(ScraperConstants.PLAYWRIGHT_BROWSER_STARTED);
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Navigate to Google News
            log.info(ScraperConstants.NAVIGATING_TO_GOOGLE_NEWS);
            page.navigate(ScraperConstants.GOOGLE_NEWS_URL);
            page.waitForTimeout(ScraperConstants.DEFAULT_WAIT_TIMEOUT);

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

            // Get article URLs
            log.info(ScraperConstants.EXTRACTING_URLS);
            List<ElementHandle> articles = page.querySelectorAll(ScraperConstants.ARTICLE_ANCHOR_SELECTOR);

            // Reverse loop through the articles
            for (int i = articles.size() - 1; i >= 0; i--) {
                ElementHandle article = articles.get(i);
                String url = article.getAttribute(ScraperConstants.HREF_ATTRIBUTE);
                if (url != null && !url.contains(ScraperConstants.GOOGLE_URL_FRAGMENT)) { // Exclude Google-related links
                    Map<String, String> newsItem = new HashMap<>();
                    String fullUrl = ScraperConstants.GOOGLE_NEWS_PREFIX + url;
                    newsItem.put(ScraperConstants.URL_KEY, fullUrl);
                    newsUrls.add(newsItem);
                    log.info(ScraperConstants.URL_FOUND, fullUrl);
                }
            }

            log.info(ScraperConstants.EXTRACTED_URLS_COUNT, newsUrls.size());
        } catch (Exception e) {
            log.error(ScraperConstants.ERROR_FETCHING_NEWS_URLS, e);
            throw e;  // Throwing exception to be handled by global exception handler
        }

        return newsUrls;
    }
}
