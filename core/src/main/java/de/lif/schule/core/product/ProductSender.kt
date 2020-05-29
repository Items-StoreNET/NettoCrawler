package de.lif.schule.core.product

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder

/**
 * @author: Kai Danz
 */

class ProductSender {

    fun send(product: ProductData){
        val httpClient = HttpClientBuilder.create().build()
        val httpRequestv2 = HttpPost("http://localhost:9000/collatio/product")
        httpRequestv2.addHeader("content-Type", "application/json; charset=UTF-8;")
        var productJson = createProductJson(product)
        productJson = replaceBadChars(productJson)
        val entity = StringEntity(productJson)
        httpRequestv2.entity = entity
        httpClient.execute(httpRequestv2)
    }

    private fun replaceBadChars(s: String): String{
        var text = s
        if(text.contains(";&nbsp;")){
            text = replace(text,";&nbsp;", " & ")
        }
        if(text.contains("ö"))
            text = replace(text,"ö","oe")
        if(text.contains('ä'))
            text = replace(text,"ä","ae")
        if(text.contains('ü'))
            text = replace(text,"ü","ue")
        return text
    }

    private fun createProductJson(product: ProductData) =
            """{"marketName":"Netto","categoryName":"${product.category}","productName":"${product.name}","productInfo":"${product.description}","currentPrice":"${product.price}","rabbatPrice":"","productGrammage":"${product.grammage}"}"""

    private fun replace(string: String, withChar: String, newValue: String) = string.replace(withChar,newValue)
}
