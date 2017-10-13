package net.pvytykac.scraper.impl.res;

import net.pvytykac.scraper.impl.Scrape;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Paly
 * @since 2017-10-13
 */
public class ResScrapingStrategyTest {

    private ResScrapingStrategy rss = new ResScrapingStrategy();

    @Test
    public void isDone() throws Exception {
        assertFalse(rss.isDone());
    }

    @Test
    public void getResponseProcessor() throws Exception {
        assertTrue(rss.getResponseProcessor() instanceof ResResponseProcessor);
    }

    @Test
    public void getNext() throws Exception {
        Scrape<Organization> scrape = rss.getNext();

        assertNotNull(scrape);
        assertEquals("1", scrape.getId());

        assertNotNull(scrape.getScrapeHandlers());
        assertEquals(1, scrape.getScrapeHandlers().size());
        assertTrue(scrape.getScrapeHandlers().get(0) instanceof ResScrapeResultHandler);

        assertNotNull(scrape.getRequest());
        assertEquals("GET", scrape.getRequest().getMethod());
        assertEquals("http://apl.czso.cz/irsw/detail.jsp?prajed_id=1", scrape.getRequest().getURI().toString());
    }
}
