package net.pvytykac.scraper;

import net.pvytykac.scraper.impl.ScrapeResult;
import org.apache.http.HttpResponse;

/**
 * @author Paly
 * @since 2017-10-11
 */
public interface ResponseProcessor<T> {

    /**
     * @param response HTTP response containing the data to be scraped
     * @return scrape result
     */
    ScrapeResult<T> processResponse(HttpResponse response);

}
