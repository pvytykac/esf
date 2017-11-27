package net.pvytykac.async;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.concurrent.Semaphore;

/**
 * @author Paly
 * @since 2017-11-26
 */
public class Main {

    public static String PATH;
    public static final Semaphore SEMAPHORE = new Semaphore(10);
    public static final HttpHost HOST = new HttpHost("or.justice.cz", 443, "https");
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOReactorException {
        if (args.length < 1) {
            throw new IllegalArgumentException("usage: java -jar <.jar> $path-to-save-html-docs");
        }

        try {
            PATH = args[0];
            Files.createDirectories(FileSystems.getDefault().getPath(PATH));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        HttpAsyncClientBuilder builder = HttpAsyncClients.custom()
                .setConnectionManager(new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor()));

        IcoQueue queue = new IcoQueue();
        queue.start();

        try(CloseableHttpAsyncClient http = builder.build()) {
            http.start();
            while(queue.hasMore()) {
                try {
                    String ico = queue.next().getIco();
                    HttpRequest request = RequestBuilder.get("/ias/ui/rejstrik")
                            .addParameter("p::submit", "x")
                            .addParameter("-1.IFormSubmitListener-htmlContainer-top-form", "")
                            .addParameter("search", ico)
                            .build();

                    FutureCallback<HttpResponse> callback = new SearchCallback(ico, http, request);

                    LOG.info("queuing request for ICO: {}", ico);

                    SEMAPHORE.acquire();
                    http.execute(HOST, request, callback);
                } catch (Exception ex) {
                    LOG.error("Exception caught while processing ico from queue");
                }
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

}
