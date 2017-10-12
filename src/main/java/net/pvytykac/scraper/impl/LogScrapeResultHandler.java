package net.pvytykac.scraper.impl;

import net.pvytykac.scraper.ScrapeResultHandler;

/**
 * @author Paly
 * @since 2017-10-12
 */
public final class LogScrapeResultHandler<T> implements ScrapeResultHandler<T> {

    @Override
    public void handleResult(T result) {
        System.out.println(result);
    }
}
