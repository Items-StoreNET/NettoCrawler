package de.lif.schule.core.product

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder

/**
 * @author: Kai Danz
 */

class ProductSender {

    private val httpClient = HttpClientBuilder.create().build()
    private val httpRequest = HttpPost("http://localhost:9000/createProductMarketForm")

    fun send(product: ProductData) {
        var productJson: String = createProductJson(product)

        //We have Problems with the Encoding of äöü so we need to replace them till we find a Solution
        productJson = replaceBadChars(productJson)

        val requestEntity = StringEntity(productJson)
        requestEntity.setContentEncoding("UTF-8")
        requestEntity.setContentType("application/json; UTF-8")
        httpRequest.addHeader("content-Type", "application/json; charset=UTF-8;")
        httpRequest.entity = requestEntity

        httpClient.execute(httpRequest)
    }

    private fun createProductJson(product: ProductData) =
            """{"marketName":"Netto","categoryName":"${product.category}","productName":"${product.name}","productInfo":"${product.description}","currentPrice":"${product.price}","rabbatPrice":"","productGrammage":"${product.grammage}"}"""

    private fun replaceBadChars(s: String): String{
        var text = s
        if(text.contains("ö"))
            text = replace(text,'ö',"oe")
        if(text.contains('ä'))
            text = replace(text,'ä',"ae")
        if(text.contains('ü'))
            text = replace(text,'ü',"ue")
        return text
    }

    private fun replace(string: String, withChar: Char, newValue: String) = string.replace(""+withChar,newValue)
}
