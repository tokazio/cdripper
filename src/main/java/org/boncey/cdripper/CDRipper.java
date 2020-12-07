package org.boncey.cdripper;

import fr.tokazio.cddb.CddbData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CDRipper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CDRipper.class);

    private static final String EXT = ".wav";

    private final File rippingDir;
    private CDTrackRippedListener trackRippedlistener;
    private CDDiscRippedListener discRippedListener;

    public CDRipper(final File rippingDir) {
        LOGGER.debug("Ripping to folder " + rippingDir.getAbsolutePath() + "...");
        if (!rippingDir.exists()) {
            LOGGER.warn("The ripping dir " + rippingDir + " doesn't exists, creating it...");
            rippingDir.mkdirs();
        }
        this.rippingDir = rippingDir;
    }

    public CDRipper start(final CddbData cddbData) throws RipException {
        final File tmpDir = new File(rippingDir, "/tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        } else {
            tmpDir.delete();
            //TODO delete failed...
        }
            /* TODO by ui!
            File dir;
            if (!cdInfo.recognised() && !_trackListing.isEmpty()) {
                cdInfo.fromTrackListing(_trackListing);
            } else if (!cdInfo.recognised()) {
                fail("Unable to recognise disk - provide a track listing file; aborting");
            }
             */


        rip(cddbData, tmpDir);
        final File toDir = new File(rippingDir, cddbData.getArtist() + "-" + cddbData.getAlbum());
        toDir.mkdirs();
        tmpDir.renameTo(toDir);
        return this;

//        throw new RipException(String.format("%s exists; clean up required", tmpDir));
    }

    private void rip(final CddbData discData, final File tmpDir) throws RipException {

        discData.getTracks().forEach(trackData -> {
            try {
                final String trackName = trackData.getIndex() + "-" + trackData.getArtist() + "-" + trackData.getTitle() + EXT;

                File wavFile = new File(tmpDir, trackName);
                File tempFile = File.createTempFile("wav", null, tmpDir);
                LOGGER.debug("Ripping " + tempFile.getAbsolutePath() + " to tmp " + wavFile.getAbsolutePath() + "...");

                //TODO osx "-e"
                //TODO linux "-e" et "-E"

                final ProcessBuilder pb = new ProcessBuilder(getRipCommand(), "-v", "-z", String.valueOf(trackData.getIndex()), tempFile.getAbsolutePath());
                pb.inheritIO();//ça c'est cool
                Process proc = pb.start();
                proc.waitFor();
                if (proc.exitValue() != 0) {
                    LOGGER.error("\nError ripping to " + tempFile.getAbsolutePath());
                } else {
                    if (!tempFile.renameTo(wavFile)) {
                        LOGGER.error("Unable to rename " + tempFile.getAbsolutePath() + " to " + wavFile.getAbsolutePath());
                    } else {
                        LOGGER.info("Done ripping " + wavFile.getAbsolutePath());
                        if (trackRippedlistener != null) {
                            LOGGER.debug("entering trackRippedlistener...");
                            trackRippedlistener.ripped(discData, trackData, wavFile);
                            LOGGER.debug("trackRippedlistener.done...");
                        } else {
                            LOGGER.debug("No trackRippedlistener, go to next track...");
                        }
                    }
                }
            } catch (IOException | InterruptedException ex) {
                LOGGER.error("Error ripping disc", ex);
            }
        });
        if (discRippedListener != null) {
            discRippedListener.ripped();
        }
    }

    public CDRipper setTrackRippedListener(CDTrackRippedListener listener) {
        this.trackRippedlistener = listener;
        return this;
    }

    public CDRipper setDiscRippedListener(CDDiscRippedListener listener) {
        this.discRippedListener = listener;
        return this;
    }

    /**
     * Strip characters that can't be used in a filename.
     *
     * @param filename the filename to tidy.
     * @return the tidied filename.
     */
    protected String tidyFilename(String filename) {

        String ret;

        Pattern bad = Pattern.compile("[\\:*?\"`<>|]");
        Matcher badMatcher = bad.matcher(filename);
        ret = badMatcher.replaceAll("");

        Pattern slash = Pattern.compile("/");
        Matcher slashMatcher = slash.matcher(ret);
        ret = slashMatcher.replaceAll("-");

        return ret;
    }

    public abstract String getEjectCommand();

    protected abstract String getRipCommand();

    public int progress() {
        //TODO handle stdout progress bar to get progress
        return 0;
    }
}
