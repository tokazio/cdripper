package org.boncey.cdripper.encoder;


import fr.tokazio.cddb.CddbData;

import java.io.File;

/**
 * For encoding an audio file to MP3.
 * <p>
 * Copyright (c) 2000-2005 Darren Greaves.
 *
 * @author Darren Greaves
 * @version $Id: Mp3Encoder.java,v 1.2 2008-11-14 11:48:58 boncey Exp $
 */
public class Mp3Encoder extends AbstractEncoder {

    private static final String MP3_CMD = "lame";

    private static final String EXT = ".mp3";

    public Mp3Encoder(final CddbData discData, final CddbData.Track trackData, final File fromFile, final File toDir) {
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
                        MP3_CMD, "--quiet", "--vbr-new", "-h", "-b", "192", "--add-id3v2", "--tt", track.getTitle(), "--tl", album.getAlbum(), "--ta",
                        track.getArtist(), "--tn", track.getIndex(), fromFile.getAbsolutePath(), toFile.getAbsolutePath()
                };
        return args;
    }


    @Override
    public String command() {
        return MP3_CMD;
    }
}
