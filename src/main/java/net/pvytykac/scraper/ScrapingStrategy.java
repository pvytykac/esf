package net.pvytykac.scraper;

import net.pvytykac.scraper.impl.Scrape;

/**
 * @author Paly
 * @since 2017-10-12
 */
public interface ScrapingStrategy<T> {

    /**
     * @return true if there are no more items to scrape, false otherwise
     */
    boolean isDone();

    /**
     * @return response processor responsible for parsing http responses for this strategy
     */
    ResponseProcessor<T> getResponseProcessor();

    /**
     * @return next item to scrape
     * @throws IllegalStateException if there are no more items to scrape
     */
    Scrape<T> getNext();

}
