package net.pvytykac.scraper.impl;

import net.pvytykac.scraper.ScrapeResultHandler;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Paly
 * @since 2017-10-12
 */
public final class Scrape<T> {

    private final String id;
    private final HttpUriRequest request;
    private final List<ScrapeResultHandler<T>> scrapeHandlers;

    public Scrape(String id, HttpUriRequest request, List<ScrapeResultHandler<T>> scrapeHandlers) {
        this.id = id;
        this.request = request;
        this.scrapeHandlers = Collections.unmodifiableList(scrapeHandlers);
    }

    public String getId() {
        return id;
    }

    public HttpUriRequest getRequest() {
        return request;
    }

    public List<ScrapeResultHandler<T>> getScrapeHandlers() {
        return scrapeHandlers;
    }
}
