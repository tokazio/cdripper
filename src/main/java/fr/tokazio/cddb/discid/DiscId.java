package fr.tokazio.cddb.discid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;

/**
 * OSX:
 * brew install cd-discid
 * <p>
 * Linux:
 * apt install cd-discid
 *
 * @link http://manpages.ubuntu.com/manpages/trusty/man1/cd-discid.1.html
 */
public class DiscId {

    public static final String CD_DISCID = "cd-discid";

    private final StringBuilder sb = new StringBuilder();

    public DiscIdData getDiscId() throws DiscIdException {
        final ProcessBuilder pb = new ProcessBuilder(CD_DISCID);
        pb.inheritIO();
        try {
            final Process proc = pb.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            final StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
            reader.lines().iterator().forEachRemaining(sj::add);
            proc.waitFor();
            if (proc.exitValue() != 0) {
                throw new DiscIdException(sj.toString());
            }
            final DiscIdData data = new DiscIdData(sj.toString());
            return data;
        } catch (IOException | InterruptedException ex) {
            throw new DiscIdException(ex);
        }
    }

}
