package com.GoogleNews.NewsApp.utility;
import com.microsoft.playwright.Playwright;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class PlaywrightSetup {
    public static void setup() {
        Playwright.create();  // This will ensure the driver is installed
    }
}

