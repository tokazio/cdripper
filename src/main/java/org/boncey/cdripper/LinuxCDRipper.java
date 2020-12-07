package org.boncey.cdripper;

import java.io.File;

/**
 * Class for ripping Audio CDs.
 * Copyright (c) 2000-2005 Darren Greaves.
 *
 * @author Darren Greaves
 * @version $Id: CDRipper.java,v 1.8 2008-11-14 11:48:58 boncey Exp $
 */
public class LinuxCDRipper extends CDRipper {

    /**
     * The command for getting CD info.
     */
    private static final String CD_RIP_CMD = "cdparanoia";

    /**
     * The command for ejecting a CD when done.
     */
    private static final String CD_EJECT_CMD = "eject";

    public LinuxCDRipper(final File rippingDir) {
        super(rippingDir);
    }

    @Override
    public String getEjectCommand() {
        return CD_EJECT_CMD;
    }

    @Override
    protected String getRipCommand() {
        return CD_RIP_CMD;
    }

}