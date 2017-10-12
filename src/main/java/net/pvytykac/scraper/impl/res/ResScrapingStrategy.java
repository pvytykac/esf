package net.pvytykac.scraper.impl.res;

import net.pvytykac.scraper.ResponseProcessor;
import net.pvytykac.scraper.ScrapeResultHandler;
import net.pvytykac.scraper.ScrapingStrategy;
import net.pvytykac.scraper.impl.LogScrapeResultHandler;
import net.pvytykac.scraper.impl.Scrape;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Paly
 * @since 2017-10-12
 */
public final class ResScrapingStrategy implements ScrapingStrategy<Organization> {

    private static final List<ScrapeResultHandler<Organization>> HANDLERS = Arrays.asList(new ResScrapeResultHandler(), new LogScrapeResultHandler<>());
    private static final HttpUriRequest TEMPLATE = RequestBuilder.get("http://apl.czso.cz/irsw/detail.jsp").build();

    private final Object lock = new Object();
    private final ResponseProcessor<Organization> processor = new ResResponseProcessor();
    private AtomicInteger ids = new AtomicInteger(0);

    @Override
    public boolean isDone() {
        synchronized (lock) {
            return ids.get() >= 10000000;
        }
    }

    @Override
    public ResponseProcessor<Organization> getResponseProcessor() {
        return processor;
    }

    @Override
    public Scrape<Organization> getNext() {
        synchronized (lock) {
            String id = String.valueOf(ids.incrementAndGet());
            HttpUriRequest request = RequestBuilder.copy(TEMPLATE)
                    .addParameter("prajed_id", id)
                    .build();

            return new Scrape<>(id, request, HANDLERS);
        }
    }
}
