package net.pvytykac.scraper.impl.justice;

import net.pvytykac.scraper.impl.ScrapeResult;
import org.junit.Test;

import java.util.Calendar;

import static net.pvytykac.scraper.TestUtil.mockResponse;
import static org.junit.Assert.*;

/**
 * @author Paly
 * @since 2017-11-11
 */
public class JusticeResponseProcessorTest {

    private JusticeResponseProcessor jrp = new JusticeResponseProcessor();

    @Test
    public void processResponseStatusOkHasData() throws Exception {
        ScrapeResult<Organization> result = jrp.processResponse(mockResponse(200, "justice/organization_response.html", "utf-8"));

        assertNotNull(result);
        assertFalse(result.isError());

        Organization organization = result.getResult();
        assertNotNull(organization);

        assertEquals("737337", organization.getId());
        assertEquals("00409430", organization.getIco());
        assertEquals("Junák - český skaut, z. s.", organization.getName());
        assertEquals("Senovážné náměstí č.ev. 977/24, Nové Město, 110 00 Praha", organization.getAddress());
        assertEquals("Spolek", organization.getForm());
        assertEquals("Posláním a účelem je - v souladu s principy a metodami stanovenými zakladatelem skautského hnutí Robertem Baden-Powellem a zakladatelem českého skautingu A. B. Svojsíkem - podporovat rozvoj osobnosti mladých lidí; jejich duchovních, mravních, intelektuálních, sociálních a tělesných schopností tak, aby byli po celý život připraveni plnit povinnosti k nejvyšší Pravdě a Lásce; sobě samým; bližním, vlasti, celému lidskému společenství a přírodě. (čl. 1 stanov spolku)", organization.getPurpose());
        assertEquals(Integer.valueOf(571), organization.getAssociations());
        assertNull(organization.getCapital());

        Calendar entered = Calendar.getInstance();
        Calendar created = Calendar.getInstance();
        entered.set(2014, Calendar.JANUARY, 1, 0, 0, 0);
        entered.set(Calendar.MILLISECOND, 0);
        created.set(1990, Calendar.JUNE, 19, 0, 0, 0);
        created.set(Calendar.MILLISECOND, 0);
        assertEquals(entered.getTime(), organization.getEntered());
        assertEquals(created.getTime(), organization.getCreated());
    }

    @Test
    public void processResponseStatusOkNoData() throws Exception {
        ScrapeResult<Organization> result = jrp.processResponse(mockResponse(200, "justice/empty_organization.html", "utf-8"));

        assertNotNull(result);
        assertTrue(result.isError());

        ScrapeResult.ScrapeError error = result.getError();
        assertNotNull(error);
        assertEquals(0, error.getRetries());
        assertEquals("Entity does not exist.", error.getMessage());
    }

    @Test
    public void processResponseStatusOkHasCapitalData() throws Exception {
        ScrapeResult<Organization> result = jrp.processResponse(mockResponse(200, "justice/organization_capital.html", "utf-8"));

        assertNotNull(result);
        assertFalse(result.isError());

        Organization organization = result.getResult();
        assertNotNull(organization);

        assertEquals(Double.valueOf(171409283.76), organization.getCapital());
    }
}
