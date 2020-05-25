package de.lif.schule.api;

import de.lif.schule.api.site.ISiteCrawlerAPI;
import de.lif.schule.api.site.ISiteScraperAPI;

public class Crawler {

    private static ISiteCrawlerAPI siteCrawler;
    private static ISiteScraperAPI siteScraper;

    public static void setSiteCrawler(ISiteCrawlerAPI siteCrawler) {
        Crawler.siteCrawler = siteCrawler;
    }

    public static void setSiteScraper(ISiteScraperAPI siteScraper) {
        Crawler.siteScraper = siteScraper;
    }

    public static ISiteCrawlerAPI getSiteCrawler() {
        return siteCrawler;
    }

    public static ISiteScraperAPI getSiteScraper() {
        return siteScraper;
    }
}
