package net.pvytykac.scraper.impl.res;

import net.pvytykac.scraper.ResponseProcessor;
import net.pvytykac.scraper.http.ResponseUtil;
import net.pvytykac.scraper.impl.ScrapeResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.pvytykac.scraper.impl.res.Organization.OrganizationBuilder;

/**
 * @author Paly
 * @since 2017-10-12
 */
public final class ResResponseProcessor implements ResponseProcessor<Organization> {

    private static final String IdXp;
    private static final String IcoXp;
    private static final String NameXp;
    private static final String SpfXp;
    private static final String FromXp;
    private static final String ToXp;
    private static final String AddrXp;
    private static final String DcXp;
    private static final String DXp;
    private static final String ZujcXp;
    private static final String ZujXp;
    private static final String AttrXp;

    static {
        try {
            IdXp = "form[name=\"form_detail\"] input[name=\"vypis\"]";
            IcoXp = "table[summary=\"identifikace\"] tr:nth-child(1) td:last-child strong";
            NameXp = "table[summary=\"identifikace\"] tr:nth-child(2) td:last-child strong";
            SpfXp = "table[summary=\"identifikace\"] tr:nth-child(3) td:last-child";
            FromXp = "table[summary=\"vznik a zanik\"] tr:nth-child(1) td:last-child";
            ToXp = "table[summary=\"vznik a zanik\"] tr:nth-child(2) td:last-child";
            AddrXp = "table[summary=\"adresa\"] tr:nth-child(1) td:last-child";
            DcXp = "table[summary=\"adresa-kody\"] tr:nth-child(1) td:nth-last-child(3)";
            DXp = "table[summary=\"adresa-kody\"] tr:nth-child(1) td:last-child";
            ZujcXp = "table[summary=\"adresa-kody\"] tr:nth-child(2) td:nth-last-child(3)";
            ZujXp = "table[summary=\"adresa-kody\"] tr:nth-child(2) td:last-child";
            AttrXp = "table[summary=\"atributy\"] tr:not(:first-child)";
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ScrapeResult<Organization> processResponse(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        ScrapeResult<Organization> result;

        if (statusLine.getStatusCode() == 200) {
            try {
                Document document = ResponseUtil.buildDocument(response);
                String ico = document.select(IcoXp).text();

                if (StringUtils.isBlank(ico)) {
                    result = new ScrapeResult<>(new ScrapeResult.ScrapeError(0, "entity does not exist"));
                } else {
                    String id = document.select(IdXp).attr("onclick");
                    String[] form = split(document.select(SpfXp).text(), " - ", 2);
                    Date created = parseDate(document.select(FromXp).text());
                    Date ceased = parseDate(document.select(ToXp).text());

                    OrganizationBuilder organization = new OrganizationBuilder()
                            .setId(Integer.valueOf(id.substring(id.indexOf("prajed_id=") + 10).split("&")[0]))
                            .setIco(ico)
                            .setName(document.select(NameXp).text())
                            .setForm(form[0], form[1])
                            .setAddress(document.select(AddrXp).text())
                            .setCreated(created)
                            .setCeased(ceased)
                            .setDistrict(document.select(DcXp).text(), document.select(DXp).text())
                            .setTerritory(document.select(ZujcXp).text(), document.select(ZujXp).text());

                    Elements attrNodeSet = document.select(AttrXp);
                    String prevAttr = null;
                    for (int i = 0; i < attrNodeSet.size(); i++) {
                        Element element = attrNodeSet.get(i);

                        String attr = element.select("td:first-child a").text();
                        String code = element.select("td:nth-child(2)").text();
                        String value = element.select("td:last-child").text();
                        organization.addAttributes(StringUtils.isBlank(attr) ? prevAttr : attr, code, value);

                        if ("Činnosti - dle CZ-NACE".equalsIgnoreCase(attr) && form[0] == null) {
                            organization.setForm(code, value);
                        }

                        if (!StringUtils.isBlank(attr)) {
                            prevAttr = attr.trim();
                        }
                    }

                    result = new ScrapeResult<>(organization.build());
                }
            } catch (ParseException ex) {
                result = new ScrapeResult<>(new ScrapeResult.ScrapeError(5, "error when parsing date"));
            }
        } else {
            result = new ScrapeResult<>(new ScrapeResult.ScrapeError(-1, "unexpected status code: " + statusLine.getStatusCode()));
        }

        return result;
    }

    private Date parseDate(String date) throws ParseException {
        String processed = date.replace("\u00a0","");
        return StringUtils.isBlank(processed) ? null : new SimpleDateFormat("dd.MM.yyyy").parse(processed);
    }

    private static String[] split(String original, String separator, int size) {
        return StringUtils.isBlank(original) ? new String[size] : original.split(separator);
    }
}
