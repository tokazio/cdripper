package fr.tokazio.ripper;

import fr.tokazio.cddb.CDDB;
import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.CddbData;
import fr.tokazio.cddb.discid.DiscIdData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Cddb {

    private static final Logger LOGGER = LoggerFactory.getLogger(Cddb.class);

    protected static String pad(int value) {
        return ((value > 9) ? "" : " ") + value;
    }

    public CddbData getCddb(final DiscIdData discIdData) throws CDDBException {

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
            final CddbData cddbData = new CddbData(entries[0].cdid, entries[0].title);
            try {
                CDDB.Detail detail = cddb.read(entries[0].category,
                        entries[0].cdid);
                LOGGER.debug("Title: " + detail.title);
                for (int j = 0; j < detail.trackNames.length; j++) {
                    LOGGER.debug(pad(j) + ": " + detail.trackNames[j]);
                    cddbData.addTrack(detail.trackNames[j]);
                }
                LOGGER.debug("Extended data: " + detail.extendedData);
                for (int j = 0; j < detail.extendedTrackData.length; j++) {
                    LOGGER.debug(pad(j) + ": " + detail.extendedTrackData[j]);
                }
            } catch (IOException ex) {
                throw new CDDBException(ex);
            }
            return cddbData;
        }
    }
}
