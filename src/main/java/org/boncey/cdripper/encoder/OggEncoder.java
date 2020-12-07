package org.boncey.cdripper.encoder;


import fr.tokazio.cddb.CddbData;

import java.io.File;

/**
 * For encoding an audio file to OGG.
 * <p>
 * Copyright (c) 2000-2005 Darren Greaves.
 *
 * @author Darren Greaves
 * @version $Id: OggEncoder.java,v 1.5 2008-11-14 11:48:58 boncey Exp $
 */
public class OggEncoder extends AbstractEncoder {

    private static final String OGG_CMD = "oggenc";

    private static final String EXT = ".ogg";

    public OggEncoder(final CddbData discData, final CddbData.Track trackData, final File fromFile, final File toDir) {
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
                        OGG_CMD, "--quiet", "--quality=5", "--title=" + track.getTitle(), "--album=" + album.getAlbum(), "--artist=" + track.getArtist(),
                        "--tracknum=" + track.getIndex(), "-n", toFile.getAbsolutePath(), fromFile.getAbsolutePath()
                };

        return args;
    }

    @Override
    public String command() {
        return OGG_CMD;
    }

}
