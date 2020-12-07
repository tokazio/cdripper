package org.boncey.cdripper.encoder;

import fr.tokazio.cddb.CddbData;

import java.io.File;

public abstract class AppleEncoder extends AbstractEncoder {

    private static final String CMD = "ffmpeg";

    private static final String EXT = ".m4a";

    protected AppleEncoder(final CddbData discData, final CddbData.Track trackData, final File fromFile, final File toDir) {
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
                        CMD, "-y", "-loglevel", "warning", "-ac", "2", "-i", fromFile.getAbsolutePath(), "-metadata", "title=" + track.getTitle(), "-metadata", "album=" + album.getAlbum(), "-metadata",
                        "artist=" + track.getArtist(), "-metadata", "track=" + track.getIndex(), "-c:a", getCodecName(), toFile.getAbsolutePath()
                };

        return args;
    }

    protected abstract String getCodecName();

    @Override
    public String command()
    {
        return CMD;
    }

}
