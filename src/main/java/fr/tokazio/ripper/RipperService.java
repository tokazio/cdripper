package fr.tokazio.ripper;

import fr.tokazio.OS;
import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.CddbData;
import fr.tokazio.cddb.discid.DiscId;
import fr.tokazio.cddb.discid.DiscIdData;
import fr.tokazio.cddb.discid.DiscIdException;
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
import java.util.function.Function;

@ApplicationScoped
public class RipperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RipperService.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final Encoded monitor = new FileDeletingTrackMonitor();

    private volatile boolean ripping;

    public DiscIdData discid() throws DiscIdException {
        return new DiscId().getDiscId();
    }

    public CddbData cddb(final DiscIdData discIdData) throws CDDBException {
        return new Cddb().getCddb(discIdData);
    }

    public void rip() throws IOException, InterruptedException, RipException {
        if (!ripping) {
            LOGGER.info("Start ripping...");
            ripping = true;
            final File baseDir = new File("/root");//FolderService.ROOT);
            final CDRipper cdr = provideRipper(baseDir);
            cdr.setTrackRippedListener(file -> {
                Encoder encoder = new FlacEncoder(monitor, new File("encoded"));
                Track track = Track.createTrack(file, baseDir, "flac");
                encoder.queue(track, false);
                LOGGER.debug("Will encode " + file.getName() + " to FLAC...");
                executor.execute(encoder);
            });
            cdr.setEndListener(new Function() {
                @Override
                public Object apply(Object o) {
                    ripping = false;
                    return null;
                }
            });
            tentative(cdr, 1);
        } else {
            LOGGER.warn("Already ripping!");
        }
    }

    private CDRipper provideRipper(final File baseDir) throws IOException, InterruptedException {
        if (OS.isUnix()) {
            return new LinuxCDRipper(baseDir, Collections.emptyList());
        }
        if (OS.isMac()) {
            return new MacOSCDRipper(baseDir, Collections.emptyList());
        }
        throw new UnsupportedOperationException("OS not supported");
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
        } catch (DiscIdException | CDDBException ex) {
            LOGGER.warn("Error getting CD infos, will retry after 5sec", ex);
            tentative(cdr, nb + 1);
        }
    }
}
