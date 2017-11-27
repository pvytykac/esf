package net.pvytykac.async;

import net.pvytykac.scraper.http.ResponseUtil;
import net.pvytykac.scraper.impl.justice.Organization;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * @author Paly
 * @since 2017-11-26
 */
public class DocsCallback implements FutureCallback<HttpResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(DocsCallback.class);

    private final HttpRequest request;
    private final HttpAsyncClient http;
    private final String subjectId;

    public DocsCallback(HttpRequest request, HttpAsyncClient http, String subjectId) {
        this.request = request;
        this.http = http;
        this.subjectId = subjectId;
    }

    @Override
    public void completed(HttpResponse result) {
        try {
            if (result.getStatusLine().getStatusCode() == 200) {
                Document document = ResponseUtil.buildDocument(result);
                Elements table = document.select("table.list > tbody > tr");
                List<Organization.Document> documents = new ArrayList<>(table.size());

                for (int i = 0; i < table.size(); i++) {
                    Element row = table.get(i);
                    Element anchor = row.select("td:first-child > a").first();
                    String docUrl = anchor.attr("href");
                    String name = StringUtils.trim(anchor.select("span").text());
                    String type = StringUtils.trim(row.select("td:nth-child(2)").text());
                    Date created = parseDate(row.select("td:nth-child(3)").text());
                    Date delivered = parseDate(row.select("td:nth-child(4)").text());
                    Date registered = parseDate(row.select("td:nth-child(5)").text());
                    Integer pages = parseInt(row.select("td:nth-child(6)").text());
                    Integer id = Integer.parseInt(parsePathParam(docUrl, "dokument"));
                    Integer folderId = Integer.parseInt(parsePathParam(docUrl, "spis"));

                    Organization.Document parsed = new Organization.Document(
                            id,
                            folderId,
                            name,
                            type,
                            created,
                            delivered,
                            registered,
                            pages,
                            true,
                            docUrl
                    );

                    documents.add(parsed);
                }

                if (!documents.isEmpty()) {
                    LOG.info("parsed {} documents for subjectId: {}", documents.size(), subjectId);
                    DaoImpl.INSTANCE.upsertJusticeDocuments(Integer.parseInt(subjectId), documents);
                }
                FileUtil.writeToFile(subjectId, "documents", document);
            } else {
                LOG.warn("Retrying: Unexpected status code: {}", result.getStatusLine().getStatusCode());
                retry();
                return;
            }
        } catch (Exception ex) {
            LOG.error("Exception when processing documents for subjectId: {}", subjectId, ex);
            retry();
        }
    }

    private String parsePathParam(String url, String param) {
        if (!StringUtils.isBlank(url) && !StringUtils.isBlank(param)) {
            int start = url.indexOf(param) + param.length() + 1;
            int end = url.indexOf("&", start);
            if (start >= 0)
                return (end >= 0) ? url.substring(start, end) : url.substring(start);
        }

        LOG.error("could not parse param: {} from url: {} for subjectId: {}", param, url, subjectId);
        throw new RuntimeException("could not parse param");
    }

    private static Integer parseInt(String text) {
        return (StringUtils.isBlank(text))
                ? null
                : Integer.parseInt(text);
    }

    private static Date parseDate(String date) throws ParseException {
        return (StringUtils.isBlank(date))
                ? null
                : new SimpleDateFormat("dd.MM.yyyy").parse(StringUtils.trim(date));
    }

    private void retry() {
//        try {
//            Thread.sleep(10000L);
//            http.execute(Main.HOST, request, this);
//        } catch (InterruptedException ignored) {}
    }

    @Override
    public void failed(Exception ex) {

    }

    @Override
    public void cancelled() {

    }
}
