package fr.tokazio.ripper;

import org.boncey.cdripper.CDRipper;
import org.boncey.cdripper.Encoded;
import org.boncey.cdripper.FileDeletingTrackMonitor;
import org.boncey.cdripper.LinuxCDRipper;
import org.boncey.cdripper.encoder.Encoder;
import org.boncey.cdripper.encoder.FlacEncoder;
import org.boncey.cdripper.model.Track;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {

    static ExecutorService executor = Executors.newFixedThreadPool(2);

    static Encoded monitor = new FileDeletingTrackMonitor();

    public static void main(String[] args) {
        try {
            File baseDir = new File("ripped");
            CDRipper cdr = new LinuxCDRipper(baseDir, Collections.emptyList());
            cdr.setTrackRippedListener(file -> {
                Encoder encoder = new FlacEncoder(monitor, new File("encoded"));
                Track track = Track.createTrack(file, baseDir, "flac");
                encoder.queue(track, false);
                System.out.println("Will encode " + file.getName() + " to FLAC...");
                executor.execute(encoder);
            });
            cdr.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    private static void encodeAll() {
        try {
            File props = new File("encoders.properties");
            File baseDir = new File("ripped");
            boolean dryRun = false;

            Encoded monitor = new FileDeletingTrackMonitor();
            List<Encoder> encoders = new EncoderLoader().loadEncoders(props, monitor);
            ExecutorService executor = executeEncoders(encoders);
            EncoderQueue encoderQueue = new EncoderQueue(baseDir, encoders, monitor, dryRun);
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.MINUTES);


            encoderQueue.cleanup(baseDir, dryRun);
            if (encoderQueue.getTracksEncoded() == 0) {
                // Return -1 so we don't trigger success notifications in any caller
                System.exit(-1);
            } else {
                System.out.println(String.format("Encoded %d tracks", encoderQueue.getTracksEncoded()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ExecutorService executeEncoders(List<Encoder> encoders) {
        ExecutorService executor = Executors.newFixedThreadPool(encoders.size());
        for (Encoder encoder : encoders) {
            executor.execute(encoder);
        }
        return executor;
    }
    */


}
