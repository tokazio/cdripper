package org.boncey.cdripper;

import fr.tokazio.cddb.CddbData;
import fr.tokazio.ripper.CDParanoia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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
            if (!rippingDir.mkdirs()) {
                LOGGER.error("Error creating riping dir: " + rippingDir.getAbsolutePath());
            }
        }
        this.rippingDir = rippingDir;
    }

    public CDRipper start(final CddbData cddbData) throws RipException {
        final File tmpDir = new File(rippingDir, "/tmp");
        LOGGER.debug("Start ripping to temp dir: " + tmpDir.getAbsolutePath());
        if (!tmpDir.exists()) {
            if (!tmpDir.mkdirs()) {
                LOGGER.error("Error creating temp dir: " + tmpDir.getAbsolutePath());
            }
        } else {
            LOGGER.error("Temp dir " + tmpDir.getAbsolutePath() + " exists, cleaning it...");
            tmpDir.delete();
            //TODO delete failed
            tmpDir.mkdirs();
            //TODO create failed
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
    }

    private void rip(final CddbData discData, final File tmpDir) {
        LOGGER.debug("Ripping " + discData.getTracks().size() + " track to " + tmpDir.getAbsolutePath() + "...");
        discData.getTracks().forEach(trackData -> {
            try {
                final String trackName = trackData.getIndex() + "-" + trackData.getArtist() + "-" + trackData.getTitle() + EXT;

                File wavFile = new File(tmpDir, trackName);
                File tempFile = File.createTempFile("wav", null, tmpDir);
                LOGGER.debug("Ripping " + tempFile.getAbsolutePath() + " to tmp " + wavFile.getAbsolutePath() + "...");

                new CDParanoia()
                        .verbose()
                        .neverSkip(0)
                        .forceOutputProgressToErr()
                        .rip(Integer.parseInt(trackData.getIndex()) + 1, tempFile);

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

            } catch (IOException | InterruptedException ex) {
                LOGGER.error("Error ripping disc to " + tmpDir.getAbsolutePath(), ex);
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


    public abstract String getEjectCommand();

    public int progress() {
        //TODO handle stdout progress bar to get progress
        return 0;
    }
}
