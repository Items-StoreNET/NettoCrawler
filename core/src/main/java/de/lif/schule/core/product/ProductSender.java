package de.lif.schule.core.product;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.internal.StringUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ProductSender {

    public void send(ProductData productData) {
        try {
            HttpPost httpRequest = new HttpPost("http://localhost:9000/collatio/product");
            httpRequest.addHeader("content-Type", "application/json; charset=UTF-8;");

            String productJson = createProductJson(productData);
            productJson = replaceBadChars(productJson);

            StringEntity entity = new StringEntity(productJson);
            httpRequest.setEntity(entity);

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            httpClient.execute(httpRequest);
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private String createProductJson(ProductData productData) {
        try {
            String category = new String(productData.getCategory().getBytes(), StandardCharsets.UTF_8);
            String name = new String(productData.getName().getBytes(), StandardCharsets.UTF_8);
            String description = new String(productData.getDescription().getBytes(), StandardCharsets.UTF_8);
            double price = productData.getPrice();
            int grammage = productData.getGrammage();

            return String.format("{\"marketName\":\"Netto\"," +
                    "\"categoryName\":\"{0}\"," +
                    "\"productName\":\"{1}\"," +
                    "\"productInfo\":\"{2}\"," +
                    "\"currentPrice\":\"{3}\"," +
                    "\"rabbatPrice\":\"{4}\"," +
                    "\"productGrammage\":\"{5}\"}", category, name, description, price, "", grammage);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private String replaceBadChars(String s) {
        String text = s;
        if(text.contains(";&nbsp;")){
            text = replace(text,";&nbsp;", " & ");
        }
        if(text.contains("ö")) {
            text = replace(text,"ö", "oe");
        }
        if(text.contains("ä")) {
            text = replace(text,"ä", "ae");
        }
        if(text.contains("ü")) {
            text = replace(text,"ü", "ue");
        }
        return text;
    }

    private String replace(String string, String oldValue, String newValue) {
        return string.replace(oldValue,newValue);
    }
}
