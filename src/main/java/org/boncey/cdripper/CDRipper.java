package org.boncey.cdripper;

import fr.tokazio.cddb.CddbData;
import fr.tokazio.cddb.discid.DiscIdData;
import fr.tokazio.ripper.CDParanoia;
import fr.tokazio.ripper.ProcException;
import fr.tokazio.ripper.RippingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class CDRipper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CDRipper.class);

    private static final String EXT = ".wav";

    private final File rippingDir;
    private CDTrackRippingListener trackRippinglistener;
    private CDTrackRippedListener trackRippedlistener;
    private CDDiscRippedListener discRippedListener;
    private CDRippingProgressListener progressListener;

    private final RippingStatus status = new RippingStatus();
    private CDParanoia cdparanoia;


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

    public CDRipper start(final DiscIdData discIdData, final CddbData cddbData) throws RipException {
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


        rip(discIdData, cddbData, tmpDir);
        final File toDir = new File(rippingDir, cddbData.getArtist() + "-" + cddbData.getAlbum());
        toDir.mkdirs();
        tmpDir.renameTo(toDir);
        return this;
    }

    private void rip(final DiscIdData discIdData, final CddbData discData, final File tmpDir) {
        LOGGER.debug("Ripping " + discData.getTracks().size() + " track to " + tmpDir.getAbsolutePath() + "...");
        status.setServiceState("RIPPING");
        status.setDiscArtist(discData.getArtist());
        status.setDiscTitle(discData.getAlbum());
        status.setTrackNb(discData.getTracks().size());
        discData.getTracks().forEach(trackData -> {
            status.setTrackId(-1);
            try {
                final String trackName = trackData.getIndex() + "-" + trackData.getArtist() + "-" + trackData.getTitle() + EXT;

                File wavFile = new File(tmpDir, trackName);
                File tempFile = File.createTempFile("wav", null, tmpDir);
                LOGGER.debug("Ripping " + tempFile.getAbsolutePath() + " to tmp " + wavFile.getAbsolutePath() + "...");

                status.setTrackId(Integer.parseInt(trackData.getIndex()) + 1);
                status.setTrackArtist(trackData.getArtist());
                status.setTrackTitle(trackData.getTitle());


                final float maxReadPos = discIdData.getFrameLenOf(status.getTrackId()) * 1500;

                LOGGER.debug("Reading track #" + status.getTrackId() + " to pos " + maxReadPos);

                if (trackRippinglistener != null) {
                    trackRippinglistener.started(status);
                }

                cdparanoia = new CDParanoia()
                        .verbose()
                        .neverSkip(0)
                        .forceOutputProgressToErr()
                        .logDebug()
                        .onProgress((position, nbCorr, nbOverlap, nbJitter) -> {
                            int trackProgress = (int) ((position / maxReadPos) * 100);
                            status.setTrackProgress(trackProgress);
                            if (progressListener != null) {
                                progressListener.onProgress(status);
                            }
                            //LOGGER.debug("Track #" + status.getTrackId() + " " + status.getTrackProgress() + "% (" + position + ")");
                            //LOGGER.debug("Track #" + status.getTrackId() + " @" + position);
                            //LOGGER.debug("\tQuality info: overlapped: " + nbOverlap + "x corrected: " + nbCorr + "x jitter: " + nbJitter + "x");
                        })
                        .rip(status.getTrackId(), tempFile);
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


            } catch (ProcException | IOException | InterruptedException ex) {
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

    public RippingStatus status() {
        return status;
    }

    public void stop() {
        LOGGER.debug("Stopping CDRipper...");
        cdparanoia.stop();
    }

    public CDRipper setProgressListener(CDRippingProgressListener listener) {
        this.progressListener = listener;
        return this;
    }

    public CDRipper setStartedListener(CDTrackRippingListener listener) {
        this.trackRippinglistener = listener;
        return this;
    }
}
