package fr.tokazio.ripper;

import org.boncey.cdripper.*;
import org.boncey.cdripper.encoder.Encoder;
import org.boncey.cdripper.encoder.FlacEncoder;
import org.boncey.cdripper.model.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class RipperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RipperService.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final Encoded monitor = new FileDeletingTrackMonitor();

    private boolean ripping;

    public synchronized void rip() throws IOException, InterruptedException, RipException {
        if (!ripping) {
            ripping = true;
            final File baseDir = new File("./");//FolderService.ROOT);
            final CDRipper cdr = new LinuxCDRipper(baseDir, Collections.emptyList());
            cdr.setTrackRippedListener(file -> {
                Encoder encoder = new FlacEncoder(monitor, new File("encoded"));
                Track track = Track.createTrack(file, baseDir, "flac");
                encoder.queue(track, false);
                LOGGER.info("Will encode " + file.getName() + " to FLAC...");
                executor.execute(encoder);
            });
            tentative(cdr, 1);
        }
    }

    private void tentative(CDRipper cdr, int nb) throws RipException, InterruptedException, IOException {
        if (nb >= 5) {
            Runtime rt = Runtime.getRuntime();
            LOGGER.info("Ejecting after " + nb + " ripping tentatives: " + cdr.getEjectCommand());
            Process proc = rt.exec(cdr.getEjectCommand(), null);
            proc.waitFor();
            return;
        }
        LOGGER.info("Tentative #" + nb);
        Thread.sleep(5000);
        try {
            cdr.start();
        } catch (CdInfoException ex) {
            LOGGER.warn("Error getting CD infos, will retry after 5sec", ex);
            tentative(cdr, nb + 1);
        }
    }
}
