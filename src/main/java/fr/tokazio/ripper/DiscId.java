package fr.tokazio.ripper;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * brew install cd-discid
 */
public class DiscId {

    private final StringBuilder sb = new StringBuilder();

    public void getDiscId() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("cd-discid");
        //pb.inheritIO();//ça c'est cool


        Process proc = pb.start();

        inheritIO(proc.getInputStream(), System.out);
        inheritIO(proc.getErrorStream(), System.err);

        proc.waitFor();

        //It outputs the discid, the number
        //       of  tracks,  the  frame  offset  of  all  of the tracks, and the total length of the CD in
        //       seconds, on one line in a space-delimited format.
        //cc10ae0f 15 182 13005 28870 46377 62425 87930 112267 128607 143960 159022 190262 233922 248090 275132 303485 4272

        if (proc.exitValue() != 0) {
            System.out.println("Error with DiscId");
        } else {
            System.out.println("DiscId ok");
            System.out.println(sb.toString());
        }
    }

    private Thread inheritIO(final InputStream src, final PrintStream dest) {
        return new Thread(new Runnable() {
            public void run() {
                Scanner sc = new Scanner(src);
                while (sc.hasNextLine()) {
                    String s = sc.nextLine();
                    dest.println(s);
                    sb.append(s);
                }
                dest.flush();
            }
        });
    }
}
