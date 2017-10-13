package net.pvytykac.scraper.impl.res;

import net.pvytykac.scraper.impl.ScrapeResult;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Paly
 * @since 2017-10-13
 */
public class ResResponseProcessorTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("d.M.yyyy");

    private ResResponseProcessor rrp = new ResResponseProcessor();

    @Test
    public void processResponseStatusOkHasData() throws Exception {
        ScrapeResult<Organization> result = rrp.processResponse(mockResponse(200, "organization_response.html"));

        assertNotNull(result);
        assertFalse(result.isError());

        Organization organization = result.getResult();

        assertNotNull(organization);
        assertEquals("00075710", organization.getIco());
        assertEquals("Muzeum jižního Plzeňska v Blovicích, příspěvková organizace", organization.getName());
        assertEquals("Blovice, 33601, Hradiště, Hradiště 1", organization.getAddress());

        assertNotNull(organization.getForm());
        assertEquals("331", organization.getForm().getId());
        assertEquals("Příspěvková organizace", organization.getForm().getValue());

        assertNotNull(organization.getCreated());
        assertEquals("1.7.1973", SDF.format(organization.getCreated()));
        assertNull(organization.getCeased());

        assertNotNull(organization.getDistrict());
        assertEquals("CZ0324", organization.getDistrict().getId());
        assertEquals("Plzeň-jih", organization.getDistrict().getValue());

        assertNotNull(organization.getTerritory());
        assertEquals("557587", organization.getTerritory().getId());
        assertEquals("Blovice", organization.getTerritory().getValue());

        assertNotNull(organization.getAttributes());
        assertEquals(4, organization.getAttributes().size());

        List<Organization.Enumerated> values;
        values = assertAttributes(organization.getAttributes(), "Statistická právní forma");
        assertEquals(1, values.size());
        assertTrue(values.stream().anyMatch(e -> e.getId().equals("331") && e.getValue().equals("Příspěvková organizace")));

        values = assertAttributes(organization.getAttributes(), "Institucionální sektor: dle ESA2010");
        assertEquals(1, values.size());
        assertTrue(values.stream().anyMatch(e -> e.getId().equals("13130") && e.getValue().equals("Místní vládní instituce (kromě fondů sociálního zabezpečení)")));

        values = assertAttributes(organization.getAttributes(), "Činnosti - dle CZ-NACE");
        assertEquals(4, values.size());
        assertTrue(values.stream().anyMatch(e -> e.getId().equals("91020") && e.getValue().equals("Činnosti muzeí")));
        assertTrue(values.stream().anyMatch(e -> e.getId().equals("G") && e.getValue().equals("Velkoobchod a maloobchod; opravy a údržba motorových vozidel")));
        assertTrue(values.stream().anyMatch(e -> e.getId().equals("6820") && e.getValue().equals("Pronájem a správa vlastních nebo pronajatých nemovitostí")));
        assertTrue(values.stream().anyMatch(e -> e.getId().equals("72200") && e.getValue().equals("Výzkum a vývoj v oblasti společenských a humanitních věd")));

        values = assertAttributes(organization.getAttributes(), "Velikostní kat. dle počtu zam.");
        assertEquals(1, values.size());
        assertTrue(values.stream().anyMatch(e -> e.getId().equals("220") && e.getValue().equals("20 - 24 zaměstnanci")));
    }

    @Test
    public void processResponseStatusOkNoData() throws Exception {
        ScrapeResult<Organization> result = rrp.processResponse(mockResponse(200, "empty_response.html"));

        assertNotNull(result);
        assertTrue(result.isError());
        assertNotNull(result.getError());
        assertEquals(0, result.getError().getRetries());
        assertNotNull(result.getError().getMessage());
    }

    // if form is empty, first CZ-NACE should be stored instead
    @Test
    public void processResponseStatusOkEmptyForm() throws Exception {
        ScrapeResult<Organization> result = rrp.processResponse(mockResponse(200, "empty_form_response.html"));

        assertNotNull(result);
        assertFalse(result.isError());

        Organization organization = result.getResult();
        assertNotNull(organization);
        assertNotNull(organization.getForm());
        assertEquals("94910", organization.getForm().getId());
        assertEquals("Činnosti náboženských organizací", organization.getForm().getValue());
    }

    @Test
    public void processResponseStatusTemporarilyUnavailable() throws Exception {
        ScrapeResult<Organization> result = rrp.processResponse(mockResponse(503, "empty_form_response.html"));

        assertNotNull(result);
        assertTrue(result.isError());
        assertEquals(-1, result.getError().getRetries());
        assertNotNull(result.getError().getMessage());
    }

    private static List<Organization.Enumerated> assertAttributes(Map<String, List<Organization.Enumerated>> attributes,
                                                                  String key) throws Exception {
        assertTrue(attributes.containsKey(key));
        return attributes.get(key);
    }

    private static HttpResponse mockResponse(int status, String document) throws Exception {
        HttpResponse response = mock(HttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);
        StatusLine statusLine = mock(StatusLine.class);
        Header contentType = mock(Header.class);

        when(statusLine.getStatusCode()).thenReturn(status);

        when(contentType.getValue()).thenReturn("text/html; charset=windows-1250");

        when(entity.getContentType()).thenReturn(contentType);
        when(entity.getContent()).thenReturn(ResResponseProcessorTest.class.getClassLoader().getResourceAsStream(document));

        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(entity);

        return response;
    }
}
