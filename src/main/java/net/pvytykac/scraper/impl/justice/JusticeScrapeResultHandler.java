package net.pvytykac.scraper.impl.justice;

import net.pvytykac.scraper.ScrapeResultHandler;
import net.pvytykac.scraper.db.res.ResDao;
import net.pvytykac.scraper.db.res.impl.ResDaoImpl;
import net.pvytykac.scraper.impl.res.ResScrapeResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class JusticeScrapeResultHandler implements ScrapeResultHandler<Organization> {

    private static final Logger log = LoggerFactory.getLogger(ResScrapeResultHandler.class);

    private final ResDao resDao = new ResDaoImpl();

    @Override
    public void handleResult(Organization result) {
        try {
            resDao.upsertJusticeOrganization(result);
        } catch (SQLException ex) {
            log.error("failed to save organization", ex);
        }
    }
}
