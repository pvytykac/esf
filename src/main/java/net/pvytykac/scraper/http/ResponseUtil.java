package net.pvytykac.scraper.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStreamReader;

public final class ResponseUtil {

    private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();

    private ResponseUtil() {}

    static {
        try {
            DBF.setValidating(false);
            DBF.setNamespaceAware(true);
            DBF.setFeature("http://xml.org/sax/features/namespaces", false);
            DBF.setFeature("http://xml.org/sax/features/validation", false);
            DBF.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            DBF.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String readEntity(HttpResponse response) {
        try {
            HttpEntity entity = response.getEntity();
            byte[] buff = new byte[(int) entity.getContentLength()];
            IOUtils.readFully(entity.getContent(), buff, 0, buff.length);

            return new String(buff, getEncoding(response));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getEncoding(HttpResponse response) {
        String enc = response.getEntity().getContentType().getValue();
        int ix = enc == null
                ? -1
                : enc.indexOf("charset=");

        if (StringUtils.isEmpty(enc) || ix < 0)
            enc = "ISO-8859-1";
        else
            enc = enc.substring(ix + 8);

        return enc;
    }

    public static Document buildDocument(HttpResponse response) {
        try {
            HttpEntity entity = response.getEntity();
            InputStreamReader in = new InputStreamReader(entity.getContent(), getEncoding(response));

            return Jsoup.parse(entity.getContent(), getEncoding(response), "");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
