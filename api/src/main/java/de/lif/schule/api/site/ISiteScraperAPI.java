package de.lif.schule.api.site;

import java.util.List;

public interface ISiteScraperAPI {

    void scrapeData();

    Object getProductData();

    List<String> getNewCrawlLinks();

}
