package fr.tokazio.ripper;

import fr.tokazio.FolderService;
import org.boncey.cdripper.CDRipper;
import org.boncey.cdripper.Encoded;
import org.boncey.cdripper.FileDeletingTrackMonitor;
import org.boncey.cdripper.LinuxCDRipper;
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

    private boolean ripping;

    public void rip() throws IOException, InterruptedException {
        if (!ripping) {
            final File baseDir = new File(FolderService.ROOT);
            final CDRipper cdr = new LinuxCDRipper(baseDir, Collections.emptyList());
            cdr.setTrackRippedListener(file -> {
                Encoder encoder = new FlacEncoder(monitor, new File("encoded"));
                Track track = Track.createTrack(file, baseDir, "flac");
                encoder.queue(track, false);
                System.out.println("Will encode " + file.getName() + " to FLAC...");
                executor.execute(encoder);
            });
            cdr.start();
            ripping = true;
        }
    }
}
