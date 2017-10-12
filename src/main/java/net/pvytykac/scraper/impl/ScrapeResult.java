package net.pvytykac.scraper.impl;

import com.google.common.base.Preconditions;

import java.util.function.Consumer;

/**
 * @author Paly
 * @since 2017-10-11
 */
public final class ScrapeResult<T> {

    private final ScrapeError error;
    private final T result;

    public ScrapeResult(ScrapeError error) {
        this.error = error;
        this.result = null;
    }

    public ScrapeResult(T result) {
        this.result = result;
        this.error = null;
    }

    public void ifError(Consumer<String> errorHandler) {
        if (isError() && getError().getRetries() > 0) {
            errorHandler.accept(getError().getMessage());
        }
    }

    /**
     * @return true if scraping failed, false otherwise
     */
    public boolean isError() {
        return error != null;
    }

    /**
     * @return error report if scraping failed
     * @throws IllegalStateException if scraping was successful
     */
    public ScrapeError getError() {
        Preconditions.checkState(isError(), "the scrape result was successful");
        return error;
    }

    /**
     * @return scraped object if scraping was successful
     * @throws IllegalStateException if scraping failed
     */
    T getResult() {
        Preconditions.checkState(!isError(), "the scrape result failed");
        return result;
    }

    public static class ScrapeError {

        private final int retries;
        private final String message;

        /**
         * @param retries -1: infinite, 0 - don't retry, 1+: specific amount
         * @param message
         */
        public ScrapeError(int retries, String message) {
            this.retries = retries;
            this.message = message;
        }

        public int getRetries() {
            return retries;
        }

        public String getMessage() {
            return message;
        }
    }

}
