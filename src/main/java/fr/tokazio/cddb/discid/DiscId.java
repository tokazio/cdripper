package fr.tokazio.cddb.discid;

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

    public DiscIdData getDiscId() throws DiscIdException {
        final ProcessBuilder pb = new ProcessBuilder(CD_DISCID);
        pb.inheritIO();
        try {
            final Process proc = pb.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            final StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
            reader.lines().iterator().forEachRemaining(sj::add);
            String result = sj.toString();
            proc.waitFor();
            if (proc.exitValue() != 0) {
                throw new DiscIdException(result);
            }
            return new DiscIdData(result);
        } catch (IOException | InterruptedException ex) {
            throw new DiscIdException(ex);
        }
    }

}
