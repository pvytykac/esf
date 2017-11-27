package net.pvytykac.scraper.db.res;

import net.pvytykac.scraper.impl.res.Organization;

import java.sql.SQLException;

/**
 * @author Paly
 * @since 2017-10-13
 */
public interface ResDao {

    void upsertOrganization(Organization organization) throws SQLException;

    void upsertJusticeOrganization(net.pvytykac.scraper.impl.justice.Organization organization) throws SQLException;

    void updateGeolocation(Integer id, Double longitude, Double latitude) throws SQLException;

}
