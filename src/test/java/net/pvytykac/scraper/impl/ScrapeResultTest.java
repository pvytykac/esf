package net.pvytykac.scraper.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScrapeResultTest {

	private static final String MSG = "error msg";
	private static final Object SUCCESS_RESULT = mock(Object.class);
	private static final ScrapeResult.ScrapeError ERROR = mock(ScrapeResult.ScrapeError.class);

	private final ScrapeResult<Object> errorResult = new ScrapeResult<>(ERROR);
	private final ScrapeResult<Object> successResult = new ScrapeResult<>(SUCCESS_RESULT);

	@Before
	public void setUp() throws Exception {
		when(ERROR.getMessage()).thenReturn(MSG);
	}

	@Test
	public void ifErrorHasErrorNoRetries() throws Exception {
		when(ERROR.getRetries()).thenReturn(0);
		final StringBuilder buf = new StringBuilder();
		errorResult.ifError(buf::append);

		assertEquals(0, buf.length());
	}

	@Test
	public void ifErrorHasErrorHasRetries() throws Exception {
		when(ERROR.getRetries()).thenReturn(1);

		final StringBuilder buf = new StringBuilder();
		errorResult.ifError(buf::append);

		assertEquals(MSG, buf.toString());
	}

	@Test
	public void ifErrorNoError() throws Exception {
		final StringBuilder buf = new StringBuilder();
		successResult.ifError(buf::append);

		assertTrue(buf.length() == 0);
	}

	@Test
	public void isErrorTrue() throws Exception {
		assertTrue(errorResult.isError());
	}

	@Test
	public void isErrorFalse() throws Exception {
		assertFalse(successResult.isError());
	}

	@Test
	public void getErrorHasError() throws Exception {
		assertSame(ERROR, errorResult.getError());
	}

	@Test(expected = IllegalStateException.class)
	public void getErrorNoError() throws Exception {
		successResult.getError();
	}

	@Test
	public void getResultHasResult() throws Exception {
		assertSame(SUCCESS_RESULT, successResult.getResult());
	}

	@Test(expected = IllegalStateException.class)
	public void getResultNoResult() throws Exception {
		errorResult.getResult();
	}

	@Test
	public void scrapeErrorTest() throws Exception {
		final int retries = 1989823;
		final String msg = "wdawdawdwadawd";

		ScrapeResult.ScrapeError error = new ScrapeResult.ScrapeError(retries, msg);

		assertEquals(retries, error.getRetries());
		assertEquals(msg, error.getMessage());
	}
}
