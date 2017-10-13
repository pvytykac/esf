package net.pvytykac.scraper.impl;

import net.pvytykac.scraper.ScrapeResultHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class ScrapeTest {

	private static final String ID = "awduowdoawhdawd";
	private static final HttpUriRequest REQUEST = mock(HttpUriRequest.class);
	private static final List<ScrapeResultHandler<Object>> HANDLERS = new ArrayList<>();

	private Scrape<Object> scrape = new Scrape<>(ID, REQUEST, HANDLERS);

	@Test
	public void getId() throws Exception {
		assertEquals(ID, scrape.getId());
	}

	@Test
	public void getRequest() throws Exception {
		assertSame(REQUEST, scrape.getRequest());
	}

	@Test
	public void getScrapeHandlers() throws Exception {
		assertEquals(HANDLERS, scrape.getScrapeHandlers());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void getScrapeHandlersIsImmutable() throws Exception {
		scrape.getScrapeHandlers().add(null);
	}
}
