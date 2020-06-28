package de.lif.schule.core.site;

import de.lif.schule.api.site.ISiteCrawlerAPI;
import de.lif.schule.api.site.ISiteScraperAPI;
import de.lif.schule.core.NettoCrawler;
import de.lif.schule.core.product.ProductData;
import de.lif.schule.core.product.ProductSender;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.util.*;

public class SiteCrawler implements ISiteCrawlerAPI {

    private String filePath = "C:\\Users\\owner\\Desktop\\nettocrawler\\sites\\";
    private String archivPath = "C:\\Users\\Annett\\Desktop\\projekt\\archiv\\";
    private ProductSender productSender = new ProductSender();

    private List<String> siteList = new ArrayList<>();
    private List<String> scrapeList = new ArrayList<>();
    private List<String> alreadyDoneList = new ArrayList<>();
    private List<ProductData> productDataList = new ArrayList<>();

    public SiteCrawler(){
        addSiteToList("https://www.netto-online.de/vorratspacks/c-N10");
        addSiteToList("https://www.netto-online.de/lebensmittel/c-N01");
        addSiteToList("https://www.netto-online.de/getraenke/c-N010102");
        addSiteToList("https://www.netto-online.de/kaffee-tee-kakao/c-N0111");
        addSiteToList("https://www.netto-online.de/konserven-fertiggerichte/c-N0122");
        addSiteToList("https://www.netto-online.de/nudeln-reis-kartoffelprodukte/c-N0119");
        addSiteToList("https://www.netto-online.de/suesswaren-knabbereien/c-N0118");
        addSiteToList("https://www.netto-online.de/themenwelten/c-N0114");
        addSiteToList("https://www.netto-online.de/wuerzmittel-sossen/c-N0121");
        addSiteToList("https://www.netto-online.de/zucker-backen-desserts/c-N0117");
        addSiteToList("https://www.netto-online.de/brot-backwaren-muesli/c-N0115");
        addSiteToList("https://www.netto-online.de/suesswaren-knabbereien/c-N0118/1");
        addSiteToList("https://www.netto-online.de/konserven-fertiggerichte/c-N0122/1");
        addSiteToList("https://www.netto-online.de/kaffee-tee-kakao/c-N0111/1");
        addSiteToList("https://www.netto-online.de/getraenke/c-N010102/1");
    }

    public void crawlSite() {
        if(siteList.size() > 0){
            NettoCrawler.getExecutorService().submit(() ->{
                String fileName = siteList.remove(0);
                String randomName = getRandomName();

                System.out.println("Crawling site " + fileName);

                try (PrintWriter printWriter = new PrintWriter(filePath + randomName)) {
                    printWriter.println(Jsoup.connect(fileName).get().html());

                    scrapeList.add(randomName);
                } catch (IOException e) {
                    new File(filePath + randomName).delete();
                }
            });
        }
    }

    public void scrapeSite() {
        if(scrapeList.size() > 0){
            NettoCrawler.getExecutorService().submit(() -> {
                String fileName = scrapeList.remove(0);
                ISiteScraperAPI scraperAPI = new SiteScraper(filePath, fileName);
                ProductData productData = null;

                scraperAPI.scrapeData();
                productData = (ProductData) scraperAPI.getProductData();

                if(productData.isValid()){
                    productDataList.add(productData);

                    System.out.println("Product is valid!");
                    System.out.println("Name: " + productData.getName());
                    System.out.println("Price: " + productData.getPrice());
                    System.out.println("Gram: " + productData.getGrammage());
                    System.out.println("Category: " + productData.getCategory());
                }
                for(String string : scraperAPI.getNewCrawlLinks()){
                    if(!alreadyDoneList.contains(string)){
                        addSiteToList(string);
                    }
                }
                new File(filePath + fileName).delete();
                //try {
                    //Files.move(new File(filePath + fileName).toPath(), new File(archivPath + fileName).toPath());
                //} catch (IOException e) {
                  //  e.printStackTrace();
                //}
            });
        }
    }

    @Override
    public void sendProduct() {
        if(productDataList.size() > 0){
            NettoCrawler.getExecutorService().submit(() -> {
               ProductData productData = productDataList.remove(0);
               productSender.send(productData);
            });
        }
    }

    private void addSiteToList(String site){
        alreadyDoneList.add(site);
        siteList.add(site);
    }

    private String getRandomName(){
        String string = "abcdefghijklmnopqrstuvwxyz0123456789";
        String randomName = "";
        Random random = new Random();

        for(int i = 0; i < 30; i++){
            randomName = randomName + string.toCharArray()[random.nextInt(string.length())];
        }
        return randomName;
    }

}