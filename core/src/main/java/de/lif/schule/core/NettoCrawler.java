package de.lif.schule.core;

import de.lif.schule.api.Crawler;
import de.lif.schule.core.countdown.CountdownManager;
import de.lif.schule.core.site.SiteCrawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettoCrawler {

    private static ExecutorService executorService;
    private static CountdownManager countdownManager;

    public static void main(String[] args) {
        Crawler.setSiteCrawler(new SiteCrawler());

        executorService = Executors.newCachedThreadPool();

        countdownManager = new CountdownManager();
        countdownManager.startScraperCountdown();
        countdownManager.startCrawlerCountdown();
        countdownManager.startSendingCountdown();
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static CountdownManager getCountdownManager() {
        return countdownManager;
    }
}
