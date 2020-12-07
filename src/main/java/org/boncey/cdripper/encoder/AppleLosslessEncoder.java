package org.boncey.cdripper.encoder;


import fr.tokazio.cddb.CddbData;

import java.io.File;

public class AppleLosslessEncoder extends AppleEncoder {

    public AppleLosslessEncoder(final CddbData discData, final CddbData.Track trackData, final File fromFile, final File toDir) {
        super(discData, trackData, fromFile, toDir);
    }

    @Override
    protected String getCodecName() {
        return "alac";
    }

}
