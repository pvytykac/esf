package net.pvytykac.scraper.db.res.impl;

import com.mysql.jdbc.Driver;
import net.pvytykac.scraper.db.res.ResDao;
import net.pvytykac.scraper.impl.res.Organization;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Paly
 * @since 2017-10-13
 */
public class ResDaoImpl implements ResDao {

    private final DataSource db;

    public ResDaoImpl() {
        try {
            BasicDataSource db = new BasicDataSource();
            db.setDriver(new Driver());
            db.setUrl("jdbc:mysql://pvytykac.net:3306/res_data");
            db.setUsername("res");
            db.setPassword("hesloheslo");
            this.db = db;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void upsertOrganization(Organization organization) throws SQLException {
        if (organization == null)
            return;

        try (Connection connection = db.getConnection()) {
            PreparedStatement ps;

            if (organization.getForm() != null) {
                ps = connection.prepareStatement("INSERT INTO form(form_id, name) VALUES(?,?) ON DUPLICATE KEY UPDATE name = VALUES(name)");

                ps.setString(1, organization.getForm().getId());
                ps.setString(2, organization.getForm().getValue());

                ps.execute();
                ps.close();
            }

            if (organization.getDistrict() != null) {
                ps = connection.prepareStatement("INSERT INTO district(dist_id, name) VALUES(?,?) ON DUPLICATE KEY UPDATE name = VALUES(name)");

                ps.setString(1, organization.getDistrict().getId());
                ps.setString(2, organization.getDistrict().getValue());

                ps.execute();
                ps.close();
            }

            if (organization.getTerritory() != null) {
                ps = connection.prepareStatement("INSERT INTO territory(terr_id, name) VALUES(?,?) ON DUPLICATE KEY UPDATE name = VALUES(name)");

                ps.setString(1, organization.getTerritory().getId());
                ps.setString(2, organization.getTerritory().getValue());

                ps.execute();
                ps.close();
            }

            ps = connection.prepareStatement("INSERT INTO new_organization(org_id, ico, name, created, ceased, address, form_id, dist_id, terr_id) " +
                    "VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE ico = VALUES(ico), name = VALUES(name), " +
                    "created = VALUES(created), ceased = VALUES(ceased), address = VALUES(address), form_id = VALUES(form_id), " +
                    "dist_id = VALUES(dist_id), terr_id = VALUES(terr_id)");

            ps.setInt(1, organization.getId());
            ps.setString(2, organization.getIco());
            ps.setString(3, organization.getName());
            ps.setDate(4, toSqlDate(organization.getCreated()));
            ps.setDate(5, toSqlDate(organization.getCeased()));
            ps.setString(6, organization.getAddress());
            ps.setString(7, organization.getForm() != null ? organization.getForm().getId() : null);
            ps.setString(8, organization.getDistrict() != null ? organization.getDistrict().getId() : null);
            ps.setString(9, organization.getTerritory() != null ? organization.getTerritory().getId() : null);

            ps.execute();
            ps.close();

            ps = connection.prepareCall("DELETE FROM attribute_value WHERE org_id = ?");
            ps.setInt(1, organization.getId());

            ps.execute();
            ps.close();

            ps = connection.prepareStatement("INSERT INTO attribute_value(org_id, attr, code, value) VALUES (?,?,?,?)");
            for (Map.Entry<String, List<Organization.Enumerated>> entry : organization.getAttributes().entrySet()) {
                String id = entry.getKey();
                for (Organization.Enumerated attribute : entry.getValue()) {
                    ps.setInt(1, organization.getId());
                    ps.setString(2, id);
                    ps.setString(3, attribute.getId());
                    ps.setString(4, attribute.getValue());

                    ps.addBatch();
                }
            }

            ps.executeBatch();
            ps.close();
        }
    }

    @Override
    public void upsertJusticeOrganization(net.pvytykac.scraper.impl.justice.Organization organization) throws SQLException {
        try (
            Connection connection = db.getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO justice_organization(id, ico, name, form, address, created, entered, purpose, associations, capital) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE ico = VALUES(ico), name = VALUES(name), form = VALUES(form), address = VALUES(address), " +
                    "created = VALUES(created), entered = VALUES(entered), purpose = VALUES(purpose), associations = VALUES(associations), " +
                    "capital = VALUES(capital) ");
        ) {
            ps.setString(1, organization.getId());
            ps.setString(2, organization.getIco());
            ps.setString(3, organization.getName());
            ps.setString(4, organization.getForm());
            ps.setString(5, organization.getAddress());
            ps.setDate(6, toSqlDate(organization.getCreated()));
            ps.setDate(7, toSqlDate(organization.getEntered()));
            ps.setString(8, organization.getPurpose());
            ps.setObject(9, organization.getAssociations());
            ps.setObject(10, organization.getCapital());

            ps.execute();
        }
    }

    @Override
    public void updateGeolocation(Integer id, Double longitude, Double latitude) {
        // tba
    }

    private static java.sql.Date toSqlDate(Date date) {
        return date == null ? null : new java.sql.Date(date.getTime());
    }
}
