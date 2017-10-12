package net.pvytykac.scraper;

import net.pvytykac.scraper.impl.ScrapeJob;
import net.pvytykac.scraper.impl.res.Organization;
import net.pvytykac.scraper.impl.res.ResScrapingStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Paly
 * @since 2017-10-11
 */
public final class Main {

    private static final ThreadGroup TG = new ThreadGroup("scrape-job-pool");
    private static final ExecutorService POOL = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(TG, runnable);
        thread.setDaemon(false);

        return thread;
    });

    public static void main(String[] args) {
        ScrapeJob<Organization> resJob = new ScrapeJob<>(new ResScrapingStrategy(), 10);
        POOL.submit(resJob::loop);
    }
}
