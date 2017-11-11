package net.pvytykac.scraper.impl.justice;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import com.google.common.collect.ImmutableList;

import net.pvytykac.scraper.ResponseProcessor;
import net.pvytykac.scraper.ScrapeResultHandler;
import net.pvytykac.scraper.ScrapingStrategy;
import net.pvytykac.scraper.impl.Scrape;

public class JusticeScrapingStrategy implements ScrapingStrategy<Organization> {

	private final AtomicInteger idGenerator = new AtomicInteger(0);
	private final ResponseProcessor<Organization> rp = new JusticeResponseProcessor();
	private final List<ScrapeResultHandler<Organization>> handlers = ImmutableList.of(
			result -> System.out.println(result));

	@Override
	public boolean isDone() {
		return idGenerator.get() < 10000000;
	}

	@Override
	public ResponseProcessor<Organization> getResponseProcessor() {
		return rp;
	}

	@Override
	public Scrape<Organization> getNext() {
		String id = String.valueOf(idGenerator.incrementAndGet());
		HttpUriRequest request = RequestBuilder.get("https://or.justice.cz/ias/ui/rejstrik-firma.vysledky")
				.addParameter("subjektId", id)
				.addParameter("typ", "PLATNY")
				.build();

		return new Scrape<>(id, request, handlers);
	}
}
