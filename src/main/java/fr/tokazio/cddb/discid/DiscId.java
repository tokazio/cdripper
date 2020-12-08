package fr.tokazio.cddb.discid;

import fr.tokazio.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;

/**
 * Using third party tool called cd-discid
 * OSX:
 * brew install cd-discid
 * <p>
 * Linux:
 * apt install cd-discid
 *
 * @link http://manpages.ubuntu.com/manpages/trusty/man1/cd-discid.1.html
 * <p>
 * This one uses a lib:
 * @link https://www.javatips.net/api/MetaMusic-master/retrieval-tools/src/main/java/slash/metamusic/discid/DiscId.java
 * <p>
 * No java native solution found
 */
public class DiscId {

    public static final String CD_DISCID = "cd-discid";
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscId.class);

    public DiscIdData getDiscId() throws DiscIdException {
        final String cmd = getCmd();
        final ProcessBuilder pb = new ProcessBuilder(cmd.split("\\s"));
        //pb.inheritIO();
        String result = "";
        try {
            LOGGER.debug("Discid command: " + cmd);
            final Process proc = pb.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            final StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
            reader.lines().iterator().forEachRemaining(sj::add);
            result = sj.toString();
            proc.waitFor();
            if (proc.exitValue() != 0) {
                LOGGER.error("Discid error: code=" + proc.exitValue() + " result=" + result);
                if (proc.exitValue() == 1) {
                    throw new DiscIdException(cmd + ": Operation not permitted");
                }
                if (proc.exitValue() == 2) {
                    throw new DiscIdException(cmd + ": No such file or directory");
                }
                throw new DiscIdException(result);
            }
            LOGGER.debug("Discid result: " + result);
            return new DiscIdData(result);
        } catch (IOException | InterruptedException ex) {
            LOGGER.error(cmd + ": " + result, ex);
            throw new DiscIdException(ex);
        }
    }

    private String getCmd() {
        if (OS.isMac()) {
            return CD_DISCID + " /dev/rdisk2";
        }
        return CD_DISCID;
    }

}
