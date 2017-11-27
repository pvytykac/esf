package net.pvytykac.async;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;

/**
 * @author Paly
 * @since 2017-11-26
 */
public class FileUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    public static void writeToFile(String subjectId, String type, Document document) {
        try {
            FileWriter fw = new FileWriter(Main.PATH + "/" + type + "-" + subjectId + ".html", false);
            fw.write(document.toString());
            fw.flush();
            fw.close();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

}
