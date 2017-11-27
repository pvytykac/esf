package net.pvytykac.async;

import com.mysql.jdbc.Driver;
import net.pvytykac.scraper.impl.justice.Organization;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Paly
 * @since 2017-11-26
 */
public class DaoImpl implements Dao {

    public static final DaoImpl INSTANCE = new DaoImpl();

    private static final Logger LOG = LoggerFactory.getLogger(DaoImpl.class);

    private final DataSource db;

    private DaoImpl() {
        try {
            BasicDataSource db = new BasicDataSource();
            db.setDriver(new Driver());
            db.setUrl("jdbc:mysql://localhost:3306/res_data");
            db.setUsername("res");
            db.setPassword("hesloheslo");
            this.db = db;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Set<BatchItem> getNextBatch(int offsetId, int limit) throws SQLException {
        try (
            Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT id, ico FROM organization WHERE id > ? ORDER BY id ASC LIMIT ?");
        ) {
            ps.setInt(1, offsetId);
            ps.setInt(2, limit);

            Set<BatchItem> batch = new HashSet<>(limit);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                batch.add(new BatchItem(rs.getInt(1), rs.getString(2)));
            }

            return batch;
        }
    }

    @Override
    public void upsertJusticeOrganization(Organization organization) throws SQLException {
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
    public void upsertJusticeDocuments(Integer subjectId, List<Organization.Document> documents) throws SQLException {
        try (
            Connection connection = db.getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO justice_document(id, folder_id, name, type, created, delivered, registered, pages, url, org_id)" +
                    " VALUES(?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE folder_id = VALUES(folder_id), name = VALUES(name), type = VALUES(type), created = VALUES(created), " +
                    "delivered = VALUES(delivered), registered = VALUES(delivered), pages = VALUES(pages), url = VALUES(url), org_id = VALUES(org_id)");
        ) {

            for (Organization.Document document: documents) {
                ps.setInt(1, document.getId());
                ps.setInt(2, document.getFolderId());
                ps.setObject(3, document.getName());
                ps.setObject(4, document.getType());
                ps.setObject(5, toSqlDate(document.getCreated()));
                ps.setObject(6, toSqlDate(document.getDelivered()));
                ps.setObject(7, toSqlDate(document.getRegistered()));
                ps.setObject(8, document.getPages());
                ps.setObject(9, document.getUrl());
                ps.setInt(10, subjectId);

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private static java.sql.Date toSqlDate(Date date) {
        return date == null ? null : new java.sql.Date(date.getTime());
    }
}
