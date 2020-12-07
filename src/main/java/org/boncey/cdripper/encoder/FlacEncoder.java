package org.boncey.cdripper.encoder;


import fr.tokazio.cddb.CddbData;

import java.io.File;

public class FlacEncoder extends AbstractEncoder {

    private static final String FLAC_CMD = "flac";

    private static final String EXT = ".flac";

    public FlacEncoder(final CddbData discData, final CddbData.Track trackData, final File fromFile, final File toDir) {
        super(discData, trackData, fromFile, toDir);
    }

    @Override
    protected String getExt() {
        return EXT;
    }

    @Override
    protected String[] getEncodeCommand(final CddbData album, final CddbData.Track track, final File fromFile, final File toFile) {
        String[] args =
                {
                        FLAC_CMD, "--silent", "--force", "--verify", "--tag", "title=" + track.getTitle(), "--tag", "album=" + album.getAlbum(), "--tag",
                        "artist=" + track.getArtist(), "--tag", "tracknumber=" + track.getIndex(), "-o", toFile.getAbsolutePath(), fromFile.getAbsolutePath()
                };
        return args;
    }

    @Override
    public String command() {
        return FLAC_CMD;
    }

}
