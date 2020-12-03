package fr.tokazio.ripper;

import fr.tokazio.OS;
import org.boncey.cdripper.*;
import org.boncey.cdripper.encoder.Encoder;
import org.boncey.cdripper.encoder.FlacEncoder;
import org.boncey.cdripper.model.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@ApplicationScoped
public class RipperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RipperService.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final Encoded monitor = new FileDeletingTrackMonitor();

    private volatile boolean ripping;

    public List<CDDB.Entry> cddb(final String cdid, int[] offsets, int length) throws IOException, CDDBException {
        CDDB cddb = new CDDB();
        cddb.connect("gnudb.gnudb.org", 8880);
        cddb.setTimeout(30 * 1000);
        //"1b037b03"
        //  int[] offsets = { 150, 18130, 48615 };
        //  int length = 893;
        CDDB.Entry[] entries = cddb.query(cdid, offsets, length);
        return Arrays.asList(entries);
    }

    public void rip() throws IOException, InterruptedException, RipException {
        if (!ripping) {
            ripping = true;
            final File baseDir = new File("./");//FolderService.ROOT);
            final CDRipper cdr = provideRipper(baseDir);
            cdr.setTrackRippedListener(file -> {
                Encoder encoder = new FlacEncoder(monitor, new File("encoded"));
                Track track = Track.createTrack(file, baseDir, "flac");
                encoder.queue(track, false);
                LOGGER.info("Will encode " + file.getName() + " to FLAC...");
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
        } catch (CdInfoException ex) {
            LOGGER.warn("Error getting CD infos, will retry after 5sec", ex);
            tentative(cdr, nb + 1);
        }
    }
}
