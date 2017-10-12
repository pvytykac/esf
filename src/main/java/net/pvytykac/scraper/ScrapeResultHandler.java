package net.pvytykac.scraper;

/**
 * @author Paly
 * @since 2017-10-11
 */
public interface ScrapeResultHandler<T> {

    /**
     * @param result
     */
    void handleResult(T result);

}
