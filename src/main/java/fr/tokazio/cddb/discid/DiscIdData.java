package fr.tokazio.cddb.discid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

public class DiscIdData implements Serializable {

    @JsonIgnore
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscIdData.class);

    @JsonProperty
    private final String discId;
    @JsonProperty
    private final int nbTracks;
    @JsonProperty
    private final Integer[] frameOffsets;
    @JsonProperty
    private final int totalLengthInSec;

    //It outputs the:
    // * discid,
    // * the number of  tracks,
    // * the  frame  offset  of  all  of the tracks,
    // * and the total length of the CD in seconds,
    // on one line in a space-delimited format.
    //example: cc10ae0f 15 182 13005 28870 46377 62425 87930 112267 128607 143960 159022 190262 233922 248090 275132 303485 4272

    public DiscIdData(final String str) {
        LOGGER.debug("Creating DiscIdData with: " + str + "...");
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("Can't create a DiscIdData from an null/empty string.");
        }
        final String[] out = str.trim().split("\\s");
        if (out.length < 4) {
            throw new IllegalArgumentException("It seems to miss data to build a DiscIdData with: " + str + ". It should be {discId} {nbTracks} {offsets...} {totalLenInSec}.");
        }
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
