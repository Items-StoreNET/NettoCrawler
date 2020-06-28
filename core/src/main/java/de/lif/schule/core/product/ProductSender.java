package de.lif.schule.core.product;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

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
        return "{\"marketName\":\"Netto" +
                "\";\"categoryName\":\"" + productData.getCategory() +
                "\";\"productName\":\"" + productData.getName() +
                "\";\"productInfo\":\"" + productData.getDescription() +
                "\";\"currentPrice\":\"" + productData.getPrice() +
                "\";\"rabbatPrice\":\"" + "" +
                "\";\"productGrammage\":\"" + productData.getGrammage() + "\"}";
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
