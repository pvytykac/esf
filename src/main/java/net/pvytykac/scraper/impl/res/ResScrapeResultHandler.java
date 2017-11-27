package net.pvytykac.scraper.impl.res;

import net.pvytykac.scraper.ScrapeResultHandler;
import net.pvytykac.scraper.db.res.ResDao;
import net.pvytykac.scraper.db.res.impl.ResDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author Paly
 * @since 2017-10-12
 */
public final class ResScrapeResultHandler implements ScrapeResultHandler<Organization> {

    private static final Logger log = LoggerFactory.getLogger(ResScrapeResultHandler.class);

    private final ResDao resDao = new ResDaoImpl();

	@Override
	public void handleResult(Organization result) {
	    try {
            resDao.upsertOrganization(result);
        } catch (SQLException ex) {
            log.error("failed to save organization", ex);
        }
	}

}
