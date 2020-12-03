package fr.tokazio.ripper;

import fr.tokazio.cddb.CDDB;
import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.discid.DiscIdData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Cddb {

    protected static String pad(int value) {
        return ((value > 9) ? "" : " ") + value;
    }

    public List<CDDB.Entry> getCddb(final DiscIdData discIdData) throws CDDBException {

        try (CDDB cddb = new CDDB()) {
            cddb.connect("gnudb.gnudb.org", 8880);
            cddb.setTimeout(30 * 1000);
            //"1b037b03"
            //  int[] offsets = { 150, 18130, 48615 };
            //  int length = 893;
            CDDB.Entry[] entries = cddb.query(discIdData.getDiscId(), discIdData.getFrameOffsets(), discIdData.getTotalLengthInSec());

            if (entries == null || entries.length == 0) {
                throw new CDDBException("No match for disc id " + discIdData.getDiscId() + ".");

            }

            for (int i = 0; i < entries.length; i++) {
                System.out.println("Match " + entries[i].category + "/" +
                        entries[i].cdid + "/" +
                        entries[i].title);
                try {
                    CDDB.Detail detail = cddb.read(entries[i].category,
                            entries[i].cdid);
                    System.out.println("Title: " + detail.title);
                    for (int j = 0; j < detail.trackNames.length; j++) {
                        System.out.println(pad(j) + ": " + detail.trackNames[i]);
                    }
                    System.out.println("Extended data: " + detail.extendedData);
                    for (int j = 0; j < detail.extendedTrackData.length; j++) {
                        System.out.println(pad(j) + ": " + detail.extendedTrackData[i]);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            return Arrays.asList(entries);
        }
    }
}
