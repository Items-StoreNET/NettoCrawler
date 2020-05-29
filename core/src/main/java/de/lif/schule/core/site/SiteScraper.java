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
    private boolean crapeDescription = false;
    private boolean crapeGram = false;

    public SiteScraper(String filePath, String fileName){
        this.file = new File(filePath + fileName);
        this.newCrawlerLinks = new ArrayList<>();
    }

    @Override
    public void scrapeData() {
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            productData = new ProductData();

            String line;
            while((line = bufferedReader.readLine()) != null){
                crapeLinks(line);
                crapeName(line);
                crapePrice(line);
                crapeDescription(line);
                crapeGram(line);
                crapeCategory(line);
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
            if(line.contains("https://www.netto-online.de/") && (line.contains("/p-"))){ //line.contains("/c-N") || line.contains("/p-")
                String[] split = line.split("href=");
                String link = split[1].replace('"', ' ');

                newCrawlerLinks.add(link.split(" ")[1]);
            }
        }
    }

    private void crapeCategory(String line){
        if(line.contains("itemprop=") && line.contains("name") && line.contains("itemListElement")
            && !line.contains("breadcrumbs-list-active") && !line.contains("breadcrumbs-list-home")){
            String category = line.split(">")[3].split("<")[0];
            category = category.replace("amp;", "");
            category = category.replace("nbsp;", "");

            productData.setCategory(category);
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

    private void crapeGram(String line){
       checkHeaderForGram(line);
       checkFoodLabelForGram(line);
    }

    private void checkHeaderForGram(String line){
        if(line.contains("tc-pdp-productname headline__minor")){
            if(line.contains(" ")){
                String[] split = line.split(">")[1].split("<")[0].split(" ");

                for(int i = 0; i < split.length; i++){
                    if((i + 1) < split.length){
                        String gramString = split[i + 1];
                        gramString = gramString.replace(",", "").replace(".", "");
                        if(gramString.equalsIgnoreCase("g") || split[i].contains("g")
                            || gramString.equalsIgnoreCase("ml") || split[i].contains("ml")){
                            if(tryUpdateGram(split[i])){
                                break;
                            }
                        }
                    } else {
                        if(split[i].contains("g") || split[i].contains("ml")){
                            if(tryUpdateGram(split[i])){
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean tryUpdateGram(String string){
        string = string.replace("g", "").replace("ml", "");
        int currentInteger = tryParseInt(string);

        if(currentInteger != 0){
            productData.setGrammage(currentInteger);
            return true;
        }
        return false;
    }

    private void checkFoodLabelForGram(String line){
        if(line.contains("food-labeling__text")){
            crapeGram = true;
        }
        if(crapeGram){
            if(line.contains("Verkaufsinhalt:")){
                int gram = tryParseInt(line.split(" ")[1]);

                if(gram != 0){
                    productData.setGrammage(gram);
                }
                crapeGram = false;
            }
        }
    }

    private void crapeName(String line){
        if(line.contains("tc-pdp-productname headline__minor")){
            String[] split = line.split(">")[1].split("<")[0].split(" ");
            String name = "";

            for(int i = 0; i < split.length; i++){
                if((i + 1) < split.length){
                    if(split[i + 1].equalsIgnoreCase("g") || tryParseInt(split[i]) == 0){
                        name = name + " " + split[i];
                    } else {
                        break;
                    }
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
