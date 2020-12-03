package fr.tokazio.cddb.discid;

import java.util.Arrays;
import java.util.LinkedList;

public class DiscIdData {

    private final String discId;
    private final int nbTracks;
    private final Integer[] frameOffsets;
    private final int totalLengthInSec;

    //It outputs the discid, the number
    //       of  tracks,  the  frame  offset  of  all  of the tracks, and the total length of the CD in
    //       seconds, on one line in a space-delimited format.
    //cc10ae0f 15 182 13005 28870 46377 62425 87930 112267 128607 143960 159022 190262 233922 248090 275132 303485 4272

    public DiscIdData(String str) {
        final String[] out = str.split(" ");
        final LinkedList<String> l = new LinkedList<String>(Arrays.asList(out));
        discId = l.pollFirst();
        nbTracks = Integer.parseInt(l.pollFirst());
        totalLengthInSec = Integer.parseInt(l.pollLast());
        frameOffsets = l.stream().map(Integer::parseInt).toArray(Integer[]::new);
    }

    public String getDiscId() {
        return discId;
    }

    public int getNbTracks() {
        return nbTracks;
    }

    public Integer[] getFrameOffsets() {
        return frameOffsets;
    }

    public int getTotalLengthInSec() {
        return totalLengthInSec;
    }
}
