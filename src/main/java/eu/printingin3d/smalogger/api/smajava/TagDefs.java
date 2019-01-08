package eu.printingin3d.smalogger.api.smajava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TagDefs {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagDefs.class);

    private static TagDefs instance;

    private class TD {
        private String tag; // label
        private int lri; // Logical Record Index
        private String desc; // Description

        TD(String tag, int lri, String desc) {
            this.tag = tag;
            this.lri = lri;
            this.desc = desc;
        }

        public String getTag() {
            return tag;
        }

        public int getLRI() {
            return lri;
        }

        public String getDesc() {
            return desc;
        }
    }

    private Map<Integer, TD> tagdefmap = new HashMap<>();

    private void printError(String msg, int line, String fpath) {
        LOGGER.error(msg + " on line " + line + " [" + fpath + "]\n");
    }

    private void add(int tagID, String tag, int lri, String desc) {
        tagdefmap.put(tagID, new TD(tag, lri, desc));
    }

    private TagDefs() {

    }

    public static TagDefs getInstance() {
        if (instance == null) {
            instance = new TagDefs();
        }
        return instance;
    }

    public void readall(String locale) throws IOException {
        locale = locale.toUpperCase();

        // Build fullpath to taglist<locale>.txt
        // Default to EN-US if localized file not found
        String taglist = "/TagList" + locale + ".txt";

        InputStream in = getClass().getResourceAsStream(taglist);

        try (Reader fr = new InputStreamReader(in); BufferedReader br = new BufferedReader(fr)) {
            String line;
            int lineCnt = 0;
            while ((line = br.readLine()) != null) {
                lineCnt++;

                // Get rid of comments and empty lines
                int hashpos = -1;
                if (line.startsWith("#") || line.startsWith("\r")) {
                    hashpos = line.indexOf('#');
                }
                if (hashpos == -1) {
                    hashpos = line.indexOf('\r');
                }

                if (hashpos != -1) {
                    line = line.substring(0, hashpos);
                }

                if (line.length() > 0) {
                    // Split line TagID=Tag\Lri\Descr
                    String[] lineparts;
                    lineparts = line.split("[=\\\\]");
                    if (lineparts.length != 4) {
                        printError("Wrong number of items", lineCnt, taglist);
                    } else {
                        int entryOK = 1;
                        int tagID = 0;
                        try {
                            tagID = Integer.parseInt(lineparts[0]);
                        } catch (NumberFormatException e) {
                            printError("Invalid tagID", lineCnt, taglist);
                            entryOK = 0;
                        }

                        int lri = 0;
                        try {
                            lri = Integer.parseInt(lineparts[2]);
                        } catch (NumberFormatException e) {
                            printError("Invalid LRI", lineCnt, taglist);
                            entryOK = 0;
                        }

                        if (entryOK == 1) {
                            String tag = lineparts[1];
                            tag = tag.trim();

                            String descr = lineparts[3];
                            descr = descr.trim();

                            add(tagID, tag, lri, descr);
                        }
                    }
                }
            }
        }
    }

    public String getTag(int tagID) {
        return tagdefmap.get(tagID).getTag();
    }

    public int getLRI(int tagID) {
        return tagdefmap.get(tagID).getLRI();
    }

    public String getDesc(int tagID) {
        return tagdefmap.get(tagID).getDesc();
    }

    public String getDesc(int tagID, String def) {
        return (tagdefmap.get(tagID) == null || tagdefmap.get(tagID).getDesc() == null) ? def
                : tagdefmap.get(tagID).getDesc();
    }
}
