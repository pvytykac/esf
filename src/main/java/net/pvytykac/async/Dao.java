package net.pvytykac.async;

import net.pvytykac.scraper.impl.justice.Organization;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * @author Paly
 * @since 2017-11-26
 */
public interface Dao {

    Set<BatchItem> getNextBatch(int offsetId, int limit) throws SQLException;
    void upsertJusticeOrganization(Organization organization) throws SQLException;
    void upsertJusticeDocuments(Integer subjectId, List<Organization.Document> document) throws SQLException;

    class BatchItem {
        private final Integer id;
        private final String ico;

        BatchItem(Integer id, String ico) {
            this.id = id;
            this.ico = ico;
        }

        public Integer getId() {
            return id;
        }

        public String getIco() {
            return ico;
        }
    }
}
