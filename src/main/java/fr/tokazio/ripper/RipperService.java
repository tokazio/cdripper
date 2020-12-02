package fr.tokazio.ripper;

import fr.tokazio.FolderService;
import org.boncey.cdripper.*;
import org.boncey.cdripper.encoder.Encoder;
import org.boncey.cdripper.encoder.FlacEncoder;
import org.boncey.cdripper.model.Track;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class RipperService {

    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final Encoded monitor = new FileDeletingTrackMonitor();

    private volatile boolean ripping;

    public void rip() throws IOException, InterruptedException, RipException, CdInfoException {
        if (!ripping) {
            ripping = true;
            final File baseDir = new File(FolderService.ROOT);
            final CDRipper cdr = new LinuxCDRipper(baseDir, Collections.emptyList());
            cdr.setTrackRippedListener(file -> {
                Encoder encoder = new FlacEncoder(monitor, new File("encoded"));
                Track track = Track.createTrack(file, baseDir, "flac");
                encoder.queue(track, false);
                System.out.println("Will encode " + file.getName() + " to FLAC...");
                executor.execute(encoder);
            });
            tentative(cdr, 1);
        }
    }

    private void tentative(CDRipper cdr, int nb) throws RipException, InterruptedException, IOException {
        if (nb >= 5) {
            Runtime rt = Runtime.getRuntime();
            System.out.println("Ejecting: " + cdr.getEjectCommand());
            Process proc = rt.exec(cdr.getEjectCommand(), null);
            proc.waitFor();
            return;
        }
        Thread.sleep(2000);
        try {
            cdr.start();
        } catch (CdInfoException ex) {
            tentative(cdr, nb + 1);
        }
    }
}
