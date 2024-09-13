package com.GoogleNews.NewsApp.controller;
import com.GoogleNews.NewsApp.service.NewsScraperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
public class NewsController {

    private final NewsScraperService newsScraperService;

    public NewsController(NewsScraperService newsScraperService) {
        this.newsScraperService = newsScraperService;
    }


    @GetMapping("/fetch-news")
    public List<Map<String, String>> fetchNews(@RequestParam String searchTerm) throws Exception {
        return newsScraperService.fetchNewsUrls(searchTerm);
    }

}