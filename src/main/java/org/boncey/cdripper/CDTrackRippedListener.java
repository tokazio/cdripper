package org.boncey.cdripper;

import fr.tokazio.cddb.CddbData;

import java.io.File;

public interface CDTrackRippedListener {

    void ripped(CddbData discData, CddbData.Track trackData, File file);
}
