package net.pvytykac.async;

import net.pvytykac.scraper.http.ResponseUtil;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paly
 * @since 2017-11-26
 */
public class SearchCallback implements FutureCallback<HttpResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(SearchCallback.class);

    private final String ico;
    private final HttpAsyncClient http;
    private final HttpRequest request;

    public SearchCallback(String ico, HttpAsyncClient http, HttpRequest request) {
        this.ico = ico;
        this.http = http;
        this.request = request;
    }

    @Override
    public void completed(HttpResponse result) {

        if (result.getStatusLine().getStatusCode() != 200) {
            try {
                Thread.sleep(10000L);
                http.execute(Main.HOST, request, this);
                return;
            } catch (InterruptedException ignored) {}
        }

        try {
            Document doc = ResponseUtil.buildDocument(result);
            FileUtil.writeToFile(ico, "search", doc);
            String detailUrl = doc.select("ul.result-links > li:first-child > a").attr("href");

            int start = detailUrl.indexOf("=");
            int end = detailUrl.indexOf("&", start);
            String subjectId = (start >= 0 && end >= 0) ? detailUrl.substring(start + 1, end) : null;

            if (subjectId != null) {

                HttpRequest detailRequest = RequestBuilder.get("/ias/ui/rejstrik-firma.vysledky")
                        .addParameter("subjektId", subjectId)
                        .addParameter("typ", "PLATNY")
                        .build();

                HttpRequest docsRequest = RequestBuilder.get("/ias/ui/vypis-sl-firma")
                        .addParameter("subjektId", subjectId)
                        .build();

                LOG.debug("Requesting details and docs for subject with ICO: {} and ID: {}", ico, subjectId);
                http.execute(Main.HOST, detailRequest, new DetailCallback(detailRequest, http, subjectId));
                http.execute(Main.HOST, docsRequest, new DocsCallback(docsRequest, http, subjectId));
            } else {
                LOG.warn("No entry found for ICO: {}", ico);
            }
        } catch (Exception ex) {
            LOG.error("Exception processing search for ico: {}", ico, ex);
        } finally {
            Main.SEMAPHORE.release();
        }
    }

    @Override
    public void failed(Exception ex) {
    }

    @Override
    public void cancelled() {
    }

}
