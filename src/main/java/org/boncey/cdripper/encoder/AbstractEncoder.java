package org.boncey.cdripper.encoder;


import fr.tokazio.cddb.CddbData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public abstract class AbstractEncoder implements Encoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEncoder.class);

    private final CddbData discData;
    private final CddbData.Track trackData;
    private final File fromFile;
    private final File toFile;

    protected AbstractEncoder(final CddbData discData, final CddbData.Track trackData, final File fromFile, final File toDir) {
        this.discData = discData;
        this.trackData = trackData;
        this.fromFile = fromFile;
        if (!toDir.exists()) {
            toDir.mkdirs();
        }
        this.toFile = new File(toDir, trackData.getIndex() + "-" + trackData.getArtist() + "-" + trackData.getTitle() + "." + getExt());
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Encoding " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath() + "...");
            if (encode()) {
                //encoded ok
            } else {
                LOGGER.error("Unable to encode " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.error("Error encoding " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath(), e);
        }
    }

    private boolean encode() throws IOException, InterruptedException {
        boolean success;
        final String[] args = getEncodeCommand(discData, trackData, fromFile, toFile);
        try {
            success = exec(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Unable to parse track name from " + Arrays.toString(args));
            success = false;
        }
        return success;
    }

    protected boolean exec(final String[] args) throws IOException, InterruptedException {
        boolean success;
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(args);

        // TODO read in separate threads
        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        String line = in.readLine();

        while (line != null) {
            System.err.println(line);
            line = in.readLine();
        }
        proc.waitFor();

        success = (proc.exitValue() == 0);
        return success;
    }


    protected abstract String[] getEncodeCommand(final CddbData album, final CddbData.Track track, final File fromFile, final File toFile);


    protected abstract String getExt();

}
