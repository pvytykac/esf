package net.pvytykac.scraper;

import net.pvytykac.scraper.impl.res.ResResponseProcessorTest;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Paly
 * @since 2017-11-11
 */
public class TestUtil {

    public static HttpResponse mockResponse(int status, String document, String encoding) throws Exception {
        HttpResponse response = mock(HttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);
        StatusLine statusLine = mock(StatusLine.class);
        Header contentType = mock(Header.class);

        when(statusLine.getStatusCode()).thenReturn(status);

        when(contentType.getValue()).thenReturn("text/html; charset=" + encoding);

        when(entity.getContentType()).thenReturn(contentType);
        when(entity.getContent()).thenReturn(ResResponseProcessorTest.class.getClassLoader().getResourceAsStream(document));

        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(entity);

        return response;
    }

}
