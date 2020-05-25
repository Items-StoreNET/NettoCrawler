package de.lif.schule.core.site;

import de.lif.schule.api.site.ISiteScraperAPI;
import de.lif.schule.core.product.ProductData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SiteScraper implements ISiteScraperAPI {

    private ProductData productData;
    private BufferedReader bufferedReader;
    private List<String> newCrawlerLinks;
    private File file;
    private StringBuilder descriptionBuilder = new StringBuilder();
    private String category;
    private boolean crapeDescription = false;

    public SiteScraper(String filePath, String fileName, String category){
        this.file = new File(filePath + fileName);
        this.newCrawlerLinks = new ArrayList<>();
        this.category = category;
    }

    @Override
    public void scrapeData() {
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            productData = new ProductData();
            productData.setCategory(category);

            String line;
            while((line = bufferedReader.readLine()) != null){
                crapeLinks(line);
                crapeNameAndGram(line);
                crapePrice(line);
                crapeDescription(line);
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object getProductData() {
        return productData;
    }

    @Override
    public List<String> getNewCrawlLinks() {
        return newCrawlerLinks;
    }

    private void crapeLinks(String line){
        if(line.contains("href=")) {
            if(line.contains("https://www.netto-online.de/") && (line.contains("c-N") || line.contains("p-"))){
                String[] split = line.split("href=");
                String link = split[1].replace('"', ' ');

                newCrawlerLinks.add(link.split(" ")[1]);
            }
        }
    }

    private void crapeDescription(String line){
        if(line.contains("editContent tc-product-description")){
            if(line.contains("<br>")){
                String copyLine = line;
                copyLine = copyLine.split(">")[1].split("<")[0];
                insertDescription(copyLine);
            }
            crapeDescription = true;
            return;
        }
        if(crapeDescription){
            if(!line.contains("<p>") && !line.contains("<li>")){
                if(!line.contains("<ul>")){
                    if(!line.contains("<") && !line.contains(">")){
                        String copyLine = line;
                        insertDescription(copyLine);
                        return;
                    }
                    crapeDescription = false;
                    productData.setDescription(descriptionBuilder.toString());
                }
                return;
            }
            String copyLine = line;
            insertDescription(copyLine);
        }
    }

    private void insertDescription(String copyLine){
        String descriptionText = copyLine;
        if(descriptionText.contains("<p>")){
            descriptionText = descriptionText.replace("<p>", "").replace("</p>", "");
        }
        if(descriptionText.contains("<li>")){
            descriptionText = descriptionText.replace("<li>", "").replace("</li>", "");
        }
        if(descriptionText.contains("<")){
            descriptionText = descriptionText.split("<")[0];
        }
        if(descriptionText.contains("&nbsp;")){
            descriptionText = descriptionText.replace("&nbsp;", " ");
        }
        if(descriptionText.contains("&amp; ") || descriptionText.contains(" &amp;")){
            descriptionText = descriptionText.replace("&amp; ", "");
            descriptionText = descriptionText.replace(" &amp;", "");
        }
        descriptionText = descriptionText.trim();

        if(!descriptionText.equalsIgnoreCase("")){
            if(descriptionBuilder.length() <= 0){
                descriptionBuilder.append(descriptionText);
            } else {
                descriptionBuilder.append("\n" + descriptionText);
            }
        }
    }

    private void crapePrice(String line){
        if(line.contains("prices__ins--digits-before-comma")){
            String price = line.split("prices__ins--digits-before-comma")[1];
            if(price.contains(">")){
                price = price.split(">")[1];
            }
            if(price.contains("<")){
                price = price.split("<")[0];
            }
            if(price.contains("*")){
                price = price.replace("*", "");
            }
            double priceDouble = tryParseDouble(price);
            productData.setPrice(priceDouble);
        }
    }

    private void crapeNameAndGram(String line){
        if(line.contains("tc-pdp-productname headline__minor")){
            String[] split = line.split(">")[1].split(" ");
            String name = "";
            int gram = 0;

            for(int i = 0; i < split.length; i++){
                if((i + 1) != split.length) {
                    if(split[i].contains("g") || split[i + 1].contains("g")){
                        gram = tryParseInt(split[i]);
                    }
                } else {
                    if(split[i].contains("g")) {
                        gram = tryParseInt(split[i]);
                    }
                }

                if(gram == 0){
                    name = name + " " + split[i];
                } else {
                    break;
                }
            }
            if(name.contains("</h1")){
                name = name.replace("</h1", "");
            }
            if(name.contains("&amp; ") || name.contains(" &amp;")){
                name = name.replace("&amp; ", "");
                name = name.replace(" &amp;", "");
            }

            productData.setName(name.trim());
            productData.setGrammage(gram);
        }
    }

    private double tryParseDouble(String string){
        try {
            return Double.parseDouble(string);
        } catch (Exception exception){
            return 0.0;
        }
    }

    private int tryParseInt(String string){
        try {
            return Integer.parseInt(string);
        } catch (Exception exception){
            return 0;
        }
    }

}
