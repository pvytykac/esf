package net.pvytykac.scraper.impl.res;

import net.pvytykac.scraper.ResponseProcessor;
import net.pvytykac.scraper.http.ResponseUtil;
import net.pvytykac.scraper.impl.ScrapeResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.pvytykac.scraper.impl.res.Organization.OrganizationBuilder;

/**
 * @author Paly
 * @since 2017-10-12
 */
public final class ResResponseProcessor implements ResponseProcessor<Organization> {

    private static XPathExpression IdXp;
    private static XPathExpression IcoXp;
    private static XPathExpression NameXp;
    private static XPathExpression SpfXp;
    private static XPathExpression FromXp;
    private static XPathExpression ToXp;
    private static XPathExpression AddrXp;
    private static XPathExpression DcXp;
    private static XPathExpression DXp;
    private static XPathExpression ZujcXp;
    private static XPathExpression ZujXp;
    private static XPathExpression AttrXp;

    static {
        try {
            XPath xpf = XPathFactory.newInstance().newXPath();
            IdXp = xpf.compile("//form[@name='form_detail']/input[@name='vypis']/@onclick");
            IcoXp = xpf.compile("//table[@summary='identifikace']/tr[1]/td[last()]/strong");
            NameXp = xpf.compile("//table[@summary='identifikace']/tr[2]/td[last()]/strong");
            SpfXp = xpf.compile("//table[@summary='identifikace']/tr[3]/td[last()]");
            FromXp = xpf.compile("//table[@summary='vznik a zanik']/tr[1]/td[last()]");
            ToXp = xpf.compile("//table[@summary='vznik a zanik']/tr[2]/td[last()]");
            AddrXp = xpf.compile("//table[@summary='adresa']/tr[1]/td[last()]");
            DcXp = xpf.compile("//table[@summary='adresa-kody']/tr[1]/td[last() - 2]");
            DXp = xpf.compile("//table[@summary='adresa-kody']/tr[1]/td[last()]");
            ZujcXp = xpf.compile("//table[@summary='adresa-kody']/tr[2]/td[last() - 2 ]");
            ZujXp = xpf.compile("//table[@summary='adresa-kody']/tr[2]/td[last()]");
            AttrXp = xpf.compile("//table[@summary='atributy']/tr[position() > 1]");
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
                String ico = IcoXp.evaluate(document);

                if (StringUtils.isBlank(ico)) {
                    result = new ScrapeResult<>(new ScrapeResult.ScrapeError(0, "entity does not exist"));
                } else {
                    String id = IdXp.evaluate(document);
                    String[] form = split(SpfXp.evaluate(document), " - ", 2);
                    Date created = parseDate(FromXp.evaluate(document));
                    Date ceased = parseDate(ToXp.evaluate(document));

                    OrganizationBuilder organization = new OrganizationBuilder()
                            .setId(Integer.valueOf(id.substring(id.indexOf("prajed_id=") + 10).split("&")[0]))
                            .setIco(ico)
                            .setName(NameXp.evaluate(document))
                            .setForm(form[0], form[1])
                            .setAddress(AddrXp.evaluate(document))
                            .setCreated(created)
                            .setCeased(ceased)
                            .setDistrict(DcXp.evaluate(document), DXp.evaluate(document))
                            .setTerritory(ZujcXp.evaluate(document), ZujXp.evaluate(document));

                    NodeList attrNodeSet = (NodeList) AttrXp.evaluate(document, XPathConstants.NODESET);
                    String prevAttr = null;
                    for (int i = 0; i < attrNodeSet.getLength(); i++) {
                        Node node = attrNodeSet.item(i);

                        String attr = node.getChildNodes().item(1).getTextContent().trim();
                        String code = node.getChildNodes().item(3).getTextContent().trim();
                        String value = node.getChildNodes().item(5).getTextContent().trim();
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
            } catch (XPathExpressionException ex) {
                result = new ScrapeResult<>(new ScrapeResult.ScrapeError(0, "xpath error"));
            } catch (ParseException ex) {
                result = new ScrapeResult<>(new ScrapeResult.ScrapeError(5, "error when parsing date"));
            }
        } else {
            result = new ScrapeResult<>(new ScrapeResult.ScrapeError(-1, "unexpected status code: " + statusLine.getStatusCode()));
        }

        return result;
    }

    private Date parseDate(String date) throws ParseException {
        return StringUtils.isBlank(date) ? null : new SimpleDateFormat("dd.MM.yyyy").parse(date);
    }

    private static String[] split(String original, String separator, int size) {
        return StringUtils.isBlank(original) ? new String[size] : original.split(separator);
    }
}
