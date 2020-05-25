package de.lif.schule.core.countdown;

import de.lif.schule.api.Crawler;
import de.lif.schule.core.NettoCrawler;

public class CountdownManager {

    private boolean crawler = true;
    private boolean scraper = true;
    private boolean productSender = true;

    public void startCrawlerCountdown() {
        NettoCrawler.getExecutorService().submit(() ->{
            Thread thread = Thread.currentThread();

            while(crawler){
                Crawler.getSiteCrawler().crawlSite();

                sleepThread(thread);
            }
        });
    }

    public void startScraperCountdown() {
        NettoCrawler.getExecutorService().submit(() ->{
            Thread thread = Thread.currentThread();

            while(scraper){
                Crawler.getSiteCrawler().scrapeSite();

                sleepThread(thread);
            }
        });
    }

    public void startSendingCountdown() {
        NettoCrawler.getExecutorService().submit(() ->{
            Thread thread = Thread.currentThread();

            while(productSender){
                Crawler.getSiteCrawler().sendProduct();

                sleepThread(thread);
            }
        });
    }

    public void setScraper(boolean scraper) {
        this.scraper = scraper;
    }

    public void setCrawler(boolean crawler) {
        this.crawler = crawler;
    }

    public void setProductSender(boolean productSender) {
        this.productSender = productSender;
    }

    public boolean isScraper() {
        return scraper;
    }

    public boolean isCrawler() {
        return crawler;
    }

    public boolean isProductSender() {
        return productSender;
    }

    private void sleepThread(Thread thread){
        try {
            thread.sleep(499);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
