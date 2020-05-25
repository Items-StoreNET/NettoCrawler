package de.lif.schule.core.site;

import de.lif.schule.api.Crawler;
import de.lif.schule.api.site.ISiteCrawlerAPI;
import de.lif.schule.api.site.ISiteScraperAPI;
import de.lif.schule.core.NettoCrawler;
import de.lif.schule.core.product.ProductData;
import de.lif.schule.core.product.ProductSender;
import org.jsoup.Jsoup;

import java.io.*;
import java.util.*;

public class SiteCrawler implements ISiteCrawlerAPI {

    private String filePath = "C:\\Users\\Annett\\Desktop\\projekt\\sites\\";
    private String archivPath = "C:\\Users\\Annett\\Desktop\\projekt\\archiv\\";
    private ProductSender productSender = new ProductSender();

    private List<String> siteList = new ArrayList<>();
    private List<String> scrapeList = new ArrayList<>();
    private List<String> alreadyDoneList = new ArrayList<>();
    private Map<String, String> categoryMap = new HashMap<>();
    private List<ProductData> productDataList = new ArrayList<>();

    public SiteCrawler(){
        siteList.add("https://www.netto-online.de/lebensmittel/c-N01");
        alreadyDoneList.add("https://www.netto-online.de/lebensmittel/c-N01");
    }

    public void crawlSite() {
        if(siteList.size() > 0){
            NettoCrawler.getExecutorService().submit(() ->{
                String fileName = siteList.remove(0);
                String randomName = getRandomName();

                System.out.println("Crawling site " + fileName + " " + randomName);

                try (PrintWriter printWriter = new PrintWriter(filePath + randomName)) {
                    printWriter.println(Jsoup.connect(fileName).get().html());

                    if(categoryMap.containsKey(fileName)){
                        String category = categoryMap.get(fileName);
                        categoryMap.put(randomName, category);
                        categoryMap.remove(fileName);
                    } else {
                        categoryMap.put(randomName, fileName.split("/")[3]);
                    }
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
                ISiteScraperAPI scraperAPI = new SiteScraper(filePath, fileName, categoryMap.get(fileName));
                ProductData productData = null;

                categoryMap.remove(fileName);
                scraperAPI.scrapeData();
                productData = (ProductData) scraperAPI.getProductData();

                if(productData.isValid()){
                    productDataList.add(productData);

                    System.out.println("Name: " + productData.getName());
                    System.out.println("Price: " + productData.getPrice());
                    System.out.println("Description: " + productData.getDescription());
                    System.out.println("Gram: " + productData.getGrammage());
                    System.out.println("Category: " + productData.getCategory());
                }

                String category = "";
                if(categoryMap.containsKey(fileName)){
                    category = categoryMap.get(fileName);
                }
                for(String string : scraperAPI.getNewCrawlLinks()){
                    if(!alreadyDoneList.contains(string)){
                        alreadyDoneList.add(string);
                        siteList.add(string);

                        if(category.equalsIgnoreCase("")){
                            category = string.split("/")[3];
                        }
                        categoryMap.put(string, category);
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