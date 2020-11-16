package org.boncey.cdripper;

import org.tritonus.sampled.cdda.CddaURLStreamHandlerFactory;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * CddaPlayer
 * <p>
 * <p>
 * <p>
 * Reads data digitally from an audio CD and plays them via Java Sound.
 * <p>
 * <p>
 * Advanced
 * <p>
 * <p>
 * java CddaPlayer
 * <p>
 * <p>
 * java CddaPlayer
 * tracknumber
 * <p>
 * <p>
 * <p>
 * tracknumber
 * the number of the track on the audio CD that should be played (starting with 1).
 * <p>
 * <p>
 * If no argument is given, the table of content is displayed.
 * <p>
 * <p>
 * The CDROM drive is hardcoded to '/dev/cdrom'.
 * <p>
 * It is reported that there are problems with mixed mode CDs.
 * <p>
 * <p>
 * CddaPlayer.java
 */
public class CddaPlayer {
    /**
     * Flag for debugging messages.
     * If true, some messages are dumped to the console
     * during operation.
     */
    private static boolean DEBUG = false;
    private static int DEFAULT_EXTERNAL_BUFFER_SIZE = 128000;

    static {
        URL.setURLStreamHandlerFactory(new CddaURLStreamHandlerFactory());
    }

    public static void main(String[] args) {
        int nExternalBufferSize = DEFAULT_EXTERNAL_BUFFER_SIZE;
        int nInternalBufferSize = AudioSystem.NOT_SPECIFIED;

        /** CDROM drive number.
         Defaults to first drive. [how is the order constituted?]
         Not used for now. Hardcoded default to /dev/cdrom.
         */
        int nDrive = 0;

        boolean bTocOnly = true;
        int nTrack = 0;

        if (args.length < 1) {
            bTocOnly = true;
        } else if (args.length == 1) {
            nTrack = Integer.parseInt(args[0]);
            bTocOnly = false;
        }

        // TODO: should not be hardcoded
        String strDrive = "/dev/cdrom";

        if (bTocOnly) {
            URL tocURL = null;
            try {
                tocURL = new URL("cdda:" + strDrive);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            InputStream tocInputStream = null;
            try {
                tocInputStream = tocURL.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            output(tocInputStream);
        }

        if (!bTocOnly) {
            URL trackURL = null;
            try {
                trackURL = new URL("cdda://" + strDrive + "#" + nTrack);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            InputStream trackInputStream = null;
            try {
                trackInputStream = trackURL.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            AudioInputStream audioInputStream = (AudioInputStream) trackInputStream;

            SourceDataLine line = null;
            AudioFormat audioFormat = audioInputStream.getFormat();
            Line.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open();
                line.start();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }

            int nBytesRead = 0;
            byte[] abData = new byte[nExternalBufferSize];
            while (nBytesRead != -1) {
                try {
                    nBytesRead = audioInputStream.read(abData, 0, abData.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (DEBUG) {
                    out("AudioPlayer.main(): read from AudioInputStream (bytes): " + nBytesRead);
                }
                if (nBytesRead >= 0) {
                    int nBytesWritten = line.write(abData, 0, nBytesRead);
                    if (DEBUG) {
                        out("AudioPlayer.main(): written to SourceDataLine (bytes): " + nBytesWritten);
                    }
                }
            }
        }
    }


    private static void output(InputStream inputStream) {
        byte[] buffer = new byte[4096];
        OutputStream outputStream = System.out;

        try {
            int nBytesRead = 0;
            nBytesRead = inputStream.read(buffer);
            while (nBytesRead >= 0) {
                outputStream.write(buffer, 0, nBytesRead);
                nBytesRead = inputStream.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void out(String strMessage) {
        System.out.println(strMessage);
    }
}