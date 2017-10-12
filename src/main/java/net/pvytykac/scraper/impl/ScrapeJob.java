package net.pvytykac.scraper.impl;

import net.pvytykac.scraper.ScrapingStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author Paly
 * @since 2017-10-11
 */
public final class ScrapeJob<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ScrapeJob.class);
    private static final ThreadGroup TG = new ThreadGroup("scrape-task-pool");

    private static final PoolingHttpClientConnectionManager CM = new PoolingHttpClientConnectionManager();
    static {
        CM.setMaxTotal(10);
        CM.setDefaultMaxPerRoute(10);
    }

    private static final HttpClient HTTP = HttpClients
            .custom()
            .setConnectionManager(CM)
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setConnectTimeout(5000)
                    .build()
            )
            .disableAuthCaching()
            .disableCookieManagement()
            .disableRedirectHandling()
            .setUserAgent("pv-scraper")
            .build();

    private final ScrapingStrategy<T> strategy;
    private final ExecutorService pool;
    private final Semaphore semaphore;

    public ScrapeJob(ScrapingStrategy<T> strategy, int poolSize) {
        this.strategy = strategy;
        this.pool = Executors.newFixedThreadPool(poolSize, r -> new Thread(TG, r));
        this.semaphore = new Semaphore(poolSize);
    }

    public final void loop() {
        try {
            while (!strategy.isDone()) {
                semaphore.acquire();
                pool.submit(() -> {
                    try {
                        Scrape<T> scrape = strategy.getNext();
                        HttpUriRequest request = scrape.getRequest();
                        ScrapeResult<T> processed = doRequest(request);

                        if (processed.isError()) {
                            int retries = processed.getError().getRetries();
                            int retried = 0;
                            while (processed.isError() && (processed.getError().getRetries() < 0 || retried < retries)) {
                                LOG.debug("retry #{} for request {}", ++retried, request);
                                processed = doRequest(request);
                            }
                        }

                        Optional<T> result = processed.isError()
                                ? Optional.empty()
                                : Optional.of(processed.getResult());

                        processed.ifError(error -> LOG.info("error for request {} -> {}", request, error));
                        result.ifPresent(t -> scrape.getScrapeHandlers().forEach(handler -> handler.handleResult(t)));
                    } catch (Exception ex) {
                        LOG.error("exception caught", ex);
                    } finally {
                        semaphore.release();
                    }
                });
            }
        } catch (Exception ex) {
            LOG.error("scrape loop caught an exception", ex);
        }
    }

    private ScrapeResult<T> doRequest(HttpUriRequest request) {
        try {
            HttpResponse response = HTTP.execute(request);
            return strategy.getResponseProcessor().processResponse(response);
        } catch (IOException ex) {
            return new ScrapeResult<>(new ScrapeResult.ScrapeError(-1, ex.getMessage()));
        }
    }

}
