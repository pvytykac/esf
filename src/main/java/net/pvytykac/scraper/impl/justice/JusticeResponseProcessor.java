package net.pvytykac.scraper.impl.justice;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.http.HttpResponse;

import net.pvytykac.scraper.ResponseProcessor;
import net.pvytykac.scraper.impl.ScrapeResult;

public class JusticeResponseProcessor implements ResponseProcessor<Organization> {

	private static final XPathExpression IdXp;
	private static final XPathExpression IcoXp;
	private static final XPathExpression NameXp;
	private static final XPathExpression SpfXp;
	private static final XPathExpression FromXp;
	private static final XPathExpression ToXp;
	private static final XPathExpression AddrXp;
	private static final XPathExpression DcXp;
	private static final XPathExpression DXp;
	private static final XPathExpression ZujcXp;
	private static final XPathExpression ZujXp;
	private static final XPathExpression AttrXp;

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
		return null;
	}
}
