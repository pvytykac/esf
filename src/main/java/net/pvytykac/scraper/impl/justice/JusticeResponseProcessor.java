package net.pvytykac.scraper.impl.justice;

import net.pvytykac.scraper.http.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;

import net.pvytykac.scraper.ResponseProcessor;
import net.pvytykac.scraper.impl.ScrapeResult;
import org.apache.http.StatusLine;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class JusticeResponseProcessor implements ResponseProcessor<Organization> {

    private static final String IdXp = "div[id=\"page\"] div a[id=\"logo\"]";
    private static final String AttributeXp = "div[class=\"aunp-content\"] div[class=\"aunp-udajPanel\"] div[class=\"div-table\"] div[class=\"div-row\"]";
    private static final String KeyXp = "div[class*=\"div-cell\"]:first-child div[class=\"vr-hlavicka\"]";
    private static final String ValueXp = "div[class=\"div-cell\"]:last-child div";

	@Override
	public ScrapeResult<Organization> processResponse(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        ScrapeResult<Organization> result;

        if (statusLine.getStatusCode() == 200) {
            Document document = ResponseUtil.buildDocument(response);

            String idHref = document.select(IdXp).attr("href");
            String id = idHref.substring(idHref.indexOf("subjektId=") + "subjektId=".length(), idHref.indexOf('&', idHref.indexOf("subjektId=")));
            Elements attributeNodes = document.select(AttributeXp);
            Map<String, String> attributes = new HashMap<>(attributeNodes.size());
            String key = "";
            String value;
            for (int i = 0; i < attributeNodes.size(); i++) {
                Element node = attributeNodes.get(i);
                String curKey = StringUtils.trim(node.select(KeyXp).text());
                if (StringUtils.isBlank(curKey)) {
                    curKey = key;
                }

                Elements valueElements = node.select(ValueXp);
                value = valueElements.size() > 0
                        ? StringUtils.trim(valueElements.get(0).text())
                        : StringUtils.trim(valueElements.text());

                key = curKey;
                if (StringUtils.equals(curKey, value) || StringUtils.isBlank(value)) {
                    continue;
                }
                attributes.put(curKey, value);
            }

            if (!attributes.containsKey("Identifikační číslo:")) {
                result = new ScrapeResult<>(new ScrapeResult.ScrapeError(0, "Entity does not exist."));
            } else {
                String name = null;

                if (attributes.containsKey("Název:")) {
                    name = attributes.get("Název:");
                } else if (attributes.containsKey("Obchodní firma:")) {
                    name = attributes.get("Obchodní firma:");
                } else if (attributes.containsKey("Označení odštěpného závodu:")) {
                    name = attributes.get("Označení odštěpného závodu:");
                }

                Date created = parseDate(attributes.get("Datum vzniku:"));
                Date entered = parseDate(attributes.get("Datum zápisu:"));
                if (attributes.containsKey("Datum vzniku a zápisu:")) {
                    created = entered = parseDate(attributes.get("Datum vzniku a zápisu:"));
                }

                result = new ScrapeResult<>(new Organization(
                        id,
                        attributes.get("Identifikační číslo:"),
                        name,
                        attributes.get("Právní forma:"),
                        attributes.get("Sídlo:"),
                        created,
                        entered,
                        attributes.get("Účel:"),
                        Collections.emptyList(),
                        parseAssocCount(attributes.get("Pobočný spolek:")),
                        parseCapital(attributes.get("Nadační kapitál:"))
                ));
            }
        } else {
            result = new ScrapeResult<>(new ScrapeResult.ScrapeError(-1, "unexpected status code: " + statusLine.getStatusCode()));
        }

        return result;
	}

	private static Date parseDate(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        int daySep = text.indexOf('.');
        int monthSep = text.indexOf(' ', daySep + 2);
        String day = StringUtils.trim(text.substring(0, daySep));
        String monthStr = StringUtils.trim(text.substring(daySep + 2, monthSep));
        String year = StringUtils.trim(text.substring(monthSep));

        Integer month;
        switch(monthStr) {
            case "ledna": month = Calendar.JANUARY; break;
            case "února": month = Calendar.FEBRUARY; break;
            case "března": month = Calendar.MARCH; break;
            case "dubna": month = Calendar.APRIL; break;
            case "května": month = Calendar.MAY; break;
            case "června": month = Calendar.JUNE; break;
            case "července": month = Calendar.JULY; break;
            case "srpna": month = Calendar.AUGUST; break;
            case "září": month = Calendar.SEPTEMBER; break;
            case "října": month = Calendar.OCTOBER; break;
            case "listopadu": month = Calendar.NOVEMBER; break;
            case "prosince": month = Calendar.DECEMBER; break;
            default: throw new IllegalArgumentException("Unknown month: " + monthSep);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year), month, Integer.parseInt(day), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private static Integer parseAssocCount(String text) {
	    return StringUtils.isBlank(text)
                ? null
                : Integer.parseInt(text.substring(text.indexOf(':') + 2, text.indexOf('Z', text.indexOf(':')) - 1));
    }

    private static Double parseCapital(String text) {
	    if (StringUtils.isBlank(text)) {
	        return null;
        }

        String clean = text.replaceAll("\\s", "").replaceAll(",", ".").replaceAll("\\.-\\s*", "");
        return Double.parseDouble(clean.substring(0, clean.indexOf("Kč")));
    }

}
