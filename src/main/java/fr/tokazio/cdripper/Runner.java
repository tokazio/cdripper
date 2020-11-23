package fr.tokazio.cdripper;

import org.boncey.cdripper.*;
import org.boncey.cdripper.encoder.Encoder;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Runner {

    public static void main(String[] args) {
        rip();
        encode();
    }

    private static void encode() {
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

    private static void rip() {
        try {
            CDRipper cdr = new MacOSRipper(new File("ripped"), Collections.emptyList());
            cdr.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
