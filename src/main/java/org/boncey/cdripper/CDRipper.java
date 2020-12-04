package org.boncey.cdripper;

import fr.tokazio.cddb.CddbData;
import org.boncey.cdripper.model.CDInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for ripping Audio CDs. Copyright (c) 2000-2005 Darren Greaves.
 *
 * @author Darren Greaves
 * @version $Id: CDRipper.java,v 1.8 2008-11-14 11:48:58 boncey Exp $
 */
public abstract class CDRipper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CDRipper.class);

    /**
     * The file extension for encoded files.
     */
    private static final String EXT = ".wav";

    private final File _baseDir;

    private final List<String> _trackListing;
    private CDRipperListener trackRippedlistener;
    private Function endListener;

    public CDRipper(File baseDir, List<String> trackListing) {
        _baseDir = baseDir;
        _trackListing = trackListing;
    }

    /**
     * Rip the CD.
     *
     * @return
     * @throws IOException          if unable to interact with the external processes.
     * @throws InterruptedException if this thread is interrupted.
     */
    public CDRipper start(String tempDir, CddbData cddbData) throws RipException {
        File tmpDir = new File(tempDir);
        boolean exists = tmpDir.exists() && !tmpDir.delete();
        if (!exists) {
            tmpDir.mkdirs();
            /* TODO by ui!
            File dir;
            if (!cdInfo.recognised() && !_trackListing.isEmpty()) {
                cdInfo.fromTrackListing(_trackListing);
            } else if (!cdInfo.recognised()) {
                fail("Unable to recognise disk - provide a track listing file; aborting");
            }
             */

            File dir = new File(_baseDir, cddbData.getArtist() + "-" + cddbData.getAlbum());
            rip(cddbData, tmpDir);
            dir.mkdirs();
            tmpDir.renameTo(dir);

            /*
            CDInfo cdInfo;
            try {
                cdInfo = getCDInfo(tmpDir);
                System.out.println(cdInfo);
            } catch (IOException | InterruptedException ex) {
                throw new CdInfoException(ex);
            }

            if (cdInfo != null) {
                File dir;
                if (!cdInfo.recognised() && !_trackListing.isEmpty()) {
                    cdInfo.fromTrackListing(_trackListing);
                } else if (!cdInfo.recognised()) {
                    fail("Unable to recognise disk - provide a track listing file; aborting");
                }

                System.out.println(String.format("%s by %s", cdInfo.getAlbum(), cdInfo.getArtist()));
                dir = new File(_baseDir, cdInfo.getDir());
                try {
                    rip(cdInfo, tmpDir);
                    dir.mkdirs();
                    tmpDir.renameTo(dir);
                } catch (IOException | InterruptedException e) {
                    throw new RipException(e);
                }
            }

             */
            return this;
        }
        throw new RipException(String.format("%s exists; clean up required", tmpDir));
    }


    /**
     * Fail with an error message.
     *
     * @param message
     * @throws IOException
     */
    private void fail(String message) {
        System.err.println(message);
    }


    /**
     * Rip the tracks from the CD.
     *
     * @param cdInfo  the CD info.
     * @param baseDir the base directory to rip and encode within.
     * @throws IOException          if unable to interact with the cdparanoia process.
     * @throws InterruptedException if this thread is interrupted.
     */
    private void rip(CddbData cdInfo, File baseDir) throws RipException {
        int index = 1;
        cdInfo.getTracks().stream().map(t -> {
            return t.getIndex() + "-" + t.getArtist() + "-" + t.getTitle() + EXT;
        }).forEach(trackName -> {
            try {
                File wavFile = new File(baseDir, trackName);
                File tempFile = File.createTempFile("wav", null, baseDir);
                System.out.println(String.format("Ripping %s (to %s)", tempFile.getAbsolutePath(), wavFile.getAbsolutePath()));

                //osx "-e"
                //linux "-e" et "-E"

                ProcessBuilder pb = new ProcessBuilder(getRipCommand(), "-v", "-z", String.valueOf(index), tempFile.getAbsolutePath());
                pb.inheritIO();//ça c'est cool
                Process proc = pb.start();
                proc.waitFor();
                if (proc.exitValue() != 0) {
                    System.err.println("\nError ripping to " + tempFile.getAbsolutePath());
                } else {
                    if (!tempFile.renameTo(wavFile)) {
                        System.err.println("Unable to rename " + tempFile.getAbsolutePath() + " to " + wavFile.getAbsolutePath());
                    } else {
                        System.out.println("Done ripping " + wavFile.getAbsolutePath());
                        if (trackRippedlistener != null) {
                            System.out.println("entering trackRippedlistener...");
                            trackRippedlistener.done(wavFile);
                            System.out.println("trackRippedlistener.done...");
                        } else {
                            System.out.println("No trackRippedlistener, go to next track...");
                        }
                    }
                }
            } catch (IOException | InterruptedException ex) {
                LOGGER.error("Error ripping disc", ex);
            }
        });
        if (endListener != null) {
            endListener.apply(null);
        }
    }

    public CDRipper setTrackRippedListener(CDRipperListener listener) {
        this.trackRippedlistener = listener;
        return this;
    }

    public CDRipper setEndListener(Function listener) {
        this.endListener = listener;
        return this;
    }

    /**
     * Strip characters that can't be used in a filename.
     *
     * @param filename the filename to tidy.
     * @return the tidied filename.
     */
    protected String tidyFilename(String filename) {

        String ret;

        Pattern bad = Pattern.compile("[\\:*?\"`<>|]");
        Matcher badMatcher = bad.matcher(filename);
        ret = badMatcher.replaceAll("");

        Pattern slash = Pattern.compile("/");
        Matcher slashMatcher = slash.matcher(ret);
        ret = slashMatcher.replaceAll("-");

        return ret;
    }

    protected abstract String getInfoCommand();

    public abstract String getEjectCommand();

    protected abstract String getRipCommand();

    protected abstract CDInfo getCDInfo(File dir)
            throws IOException, InterruptedException;


    public int progress() {
        //TODO handle stdout progress bar to get progress
        return 0;
    }
    /**
     * Rip and encode the CD.
     *
     * @param args the base dir.
     */
    /*
    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("Usage: CDRipper <base dir> [track names text file]");
            System.exit(-1);
        }

        File baseDir = new File(args[0]);
        if (!baseDir.canRead() || !baseDir.isDirectory()) {
            System.err.printf("Unable to access %s as a directory%n", baseDir);
            System.exit(-1);
        }

        List<String> trackListing = Collections.EMPTY_LIST;
        if (args.length > 1) {
            trackListing = Files.readAllLines(Paths.get(args[1]));
        }

        try {
            // TODO Select based on OS
            CDRipper cdr = new MacOSRipper(baseDir, trackListing);
            cdr.start();
        } catch (CDRipperException e) {
            e.printStackTrace();
        }
    }

     */
}
