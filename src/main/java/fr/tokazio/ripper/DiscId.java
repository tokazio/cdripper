package fr.tokazio.ripper;

import java.io.IOException;

/**
 * brew install cd-discid
 */
public class DiscId {

    public void getDiscId() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("cd-discid");
        pb.inheritIO();//ça c'est cool
        Process proc = pb.start();
        proc.waitFor();

        if (proc.exitValue() != 0) {
            System.out.println("Error with DiscId");
        } else {
            System.out.println("DiscId ok");
        }
    }

}
