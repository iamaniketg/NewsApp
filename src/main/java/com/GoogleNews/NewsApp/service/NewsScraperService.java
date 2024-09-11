package com.GoogleNews.NewsApp.service;
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
     * The method navigates to Google News, searches for the given term,
     * captures a screenshot of the search results, and extracts URLs from the articles.
     *
     * @param searchTerm The search term to query news articles.
     * @return A list of maps containing article URLs.
     */
    public List<Map<String, String>> fetchNewsUrls(String searchTerm) {
        List<Map<String, String>> newsUrls = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            log.info("Starting Playwright and launching browser in non-headless mode.");
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Navigate to Google News
            log.info("Navigating to Google News...");
            page.navigate("https://news.google.com/");
            page.waitForTimeout(10000);

            // Search for the term
            log.info("Searching for term: {}", searchTerm);
            page.waitForSelector("input[type='text']", new Page.WaitForSelectorOptions().setTimeout(60000));
            page.fill("input[type='text']", searchTerm);
            page.keyboard().press("Enter");

            // Wait for search results to load
            log.info("Waiting for search results to load...");
            page.waitForTimeout(10000);

            // Capture screenshot after loading search results
            log.info("Capturing screenshot of search results...");
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("search_results.png")).setFullPage(true));
            log.info("Screenshot saved as 'search_results.png'.");

            // Get article URLs
            log.info("Extracting article URLs...");
            List<ElementHandle> articles = page.querySelectorAll("article a");

            // Reverse loop through the articles
            for (int i = articles.size() - 1; i >= 0; i--) {
                ElementHandle article = articles.get(i);
                String url = article.getAttribute("href");
                if (url != null && !url.contains("google")) { // Exclude Google-related links
                    Map<String, String> newsItem = new HashMap<>();
                    String fullUrl = "https://news.google.com" + url;
                    newsItem.put("url", fullUrl);
                    newsUrls.add(newsItem);
                    log.info("Found URL: {}", fullUrl);
                }
            }

            log.info("Finished extracting {} URLs.", newsUrls.size());
        } catch (Exception e) {
            log.error("Error occurred while fetching news URLs: ", e);
        }

        return newsUrls;
    }
}



