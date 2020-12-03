package fr.tokazio.ripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;

/**
 * brew install cd-discid
 */
public class DiscId {

    private final StringBuilder sb = new StringBuilder();

    public void getDiscId() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("cd-discid");

        //pb.inheritIO();//ça c'est cool

        String result = "";

        Process proc = pb.start();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
        reader.lines().iterator().forEachRemaining(sj::add);
        result = sj.toString();


        proc.waitFor();

        //It outputs the discid, the number
        //       of  tracks,  the  frame  offset  of  all  of the tracks, and the total length of the CD in
        //       seconds, on one line in a space-delimited format.
        //cc10ae0f 15 182 13005 28870 46377 62425 87930 112267 128607 143960 159022 190262 233922 248090 275132 303485 4272

        if (proc.exitValue() != 0) {
            System.out.println("Error with DiscId");
        } else {
            System.out.println("DiscId ok");
            System.out.println(result);
        }
    }

}
