package fr.tokazio.ripper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * cdparanoia III release 10.2 (September 11, 2008)
 * <p>
 * (C) 2008 Monty <monty@xiph.org> and Xiph.Org
 * <p>
 * Report bugs to paranoia@xiph.org
 * http://www.xiph.org/paranoia/
 */
public class CDParanoia {

    //OUTPUT SMILIES:
    public static final String LOW_JITTER = ":-)";//  :-)   Normal operation, low/no jitter

    private final List<String> args = new LinkedList<>();
    public static final String JITTER = ":-|";// :-|   Normal operation, considerable jitter
    public static final String READ_DRIFT = ":-/";// :-/   Read drift
    public static final String LOSS_OF_STREAMING = ":-P";//:-P   Unreported loss of streaming in atomic read operation
    public static final String READ_PB = "8-|";//8-|   Finding read problems at same point during reread; hard to correct
    public static final String TRANSPORT_ERROR = ":-0";//:-0   SCSI/ATAPI transport error
    public static final String SCRATCH = ":-(";//:-(   Scratch detected
    public static final String CORRECTION = ";-(";//        ;-(   Gave up trying to perform a correction
    public static final String ABORTED = "8-X";//8-X   Aborted (as per -X) due to a scratch/skip
    public static final String FINISHED = ":^D";//:^D   Finished extracting
    //PROGRESS BAR SYMBOLS:
    public static final String PG_OK = " ";//<space> No corrections needed
    public static final String PG_CORRECTION_REQUIRED = "-";//-    Jitter correction required
    public static final String PG_READ_ERROR = "+";//+    Unreported loss of streaming/other error in read
    public static final String PG_STAGE2_CORRECTED = "!";//!    Errors are getting through stage 1 but corrected in stage2
    public static final String PG_TRANSPORT_ERROR = "e";//e    SCSI/ATAPI transport error (corrected)
    public static final String PG_UNCORRECTED_SKIPPED = "V";//V    Uncorrected error/skip
    private static final Logger LOGGER = LoggerFactory.getLogger(CDParanoia.class);
    private final StringBuilder sb = new StringBuilder();
    private ProgressListener progressListener;

    public CDParanoia() {
        args.add("cdparanoia");
    }

    public CDParanoia onProgress(ProgressListener listener) {
        this.progressListener = listener;
        return this;
    }

    private String run() throws IOException, InterruptedException, ProcException {
        final String[] cmd = args.toArray(new String[0]);
        LOGGER.info("Ripping command: " + Arrays.toString(cmd));
        final ProcessBuilder pb = new ProcessBuilder(cmd);
        //pb.inheritIO();//ça c'est cool
        final long start = System.currentTimeMillis();
        final Process proc = pb.start();
        Thread t1 = new Thread() {
            public void run() {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                while (proc.isAlive()) {
                    String line = "";
                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                            LOGGER.debug(line);
                        }
                    } catch (IOException ex) {
                        LOGGER.warn("Error reading ripping process output", ex);
                    }
                }
            }
        };
        t1.setDaemon(true);
        t1.setName("CDParanoia-inputStream-reader");
        t1.start();
        Thread t2 = new Thread() {
            public void run() {
                int at = 0;
                int maxAt = 0;
                int nbCorr = 0;
                int nbOverlap = 0;
                int nbJitter = 0;
                final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                while (proc.isAlive()) {
                    String line = "";
                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                            if (line.contains("[read]")) {
                                at = Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1));
                                if (at > maxAt) {
                                    maxAt = at;
                                }
                                if (progressListener != null) {
                                    progressListener.onProgress(maxAt, nbCorr, nbOverlap, nbJitter);
                                }
                            }

                            if (line.contains("[correction]")) {
                                nbCorr++;
                            }

                            if (line.contains("[overlap]")) {
                                nbOverlap++;
                            }

                            if (line.contains("[jitter]")) {
                                nbJitter++;
                            }
                        }
                    } catch (IOException ex) {
                        LOGGER.warn("Error reading ripping process output", ex);
                    }
                }
                LOGGER.info("Quality info: overlapped: " + nbOverlap + "x corrected: " + nbCorr + "x jitter: " + nbJitter + "x");
            }
        };
        t2.setDaemon(true);
        t2.setName("CDParanoia-errorStream-reader");
        t2.start();
        proc.waitFor();
        final long end = System.currentTimeMillis();
        t1.interrupt();
        t2.interrupt();
        if (proc.exitValue() != 0) {
            throw new ProcException(proc.exitValue(), sb.toString());
        }
        LOGGER.info("Ripping track ended in " + (end - start) + "ms");
        return sb.toString();
    }

    public void rip(int trackNo, File outFile) throws IOException, InterruptedException, ProcException {
        args.add(String.valueOf(trackNo));
        args.add(outFile.getAbsolutePath());
        run();
    }

    //1 6 970 152

    /*

track_num = 160 start sector 4350 msf: 1,0,0
track_num = 161 start sector 107850 msf: 24,0,0
track_num = 162 start sector 330642 msf: 73,30,42
track_num = 1 start sector 0 msf: 0,2,0
track_num = 2 start sector 4777 msf: 1,5,52
track_num = 3 start sector 22585 msf: 5,3,10
track_num = 4 start sector 42932 msf: 9,34,32
track_num = 5 start sector 57150 msf: 12,44,0
track_num = 6 start sector 72070 msf: 16,2,70
track_num = 7 start sector 88362 msf: 19,40,12
track_num = 8 start sector 105290 msf: 23,25,65
track_num = 9 start sector 118987 msf: 26,28,37
track_num = 10 start sector 134922 msf: 30,0,72
track_num = 11 start sector 149090 msf: 33,9,65
track_num = 12 start sector 164567 msf: 36,36,17
track_num = 13 start sector 169595 msf: 37,43,20
track_num = 14 start sector 185380 msf: 41,13,55
track_num = 15 start sector 202267 msf: 44,58,67
track_num = 16 start sector 212790 msf: 47,19,15
track_num = 17 start sector 226995 msf: 50,28,45
track_num = 18 start sector 239875 msf: 53,20,25
track_num = 19 start sector 253197 msf: 56,17,72
track_num = 20 start sector 276497 msf: 61,28,47
track_num = 21 start sector 291592 msf: 64,49,67
track_num = 22 start sector 299282 msf: 66,32,32
track_num = 23 start sector 302995 msf: 67,21,70
track_num = 24 start sector 312430 msf: 69,27,55


    1:
        début à 36456
        fin à 7006608
        soit taille de 7006608-36456 = 6970152
        380 read
        0 à 4777 frames
        6970152 / 4777 = 1460
        7006608 / 4777 = 1466
    2:
        début à 5644800
        fin à 26717544
        soit taille de 26717544-5644800 = 21072744
        1178 read
        4777 à 22585 = 17808 frames(sector)
        21072744 / 17808 = 1183
        26717544 / 17808 = 1500


    1: 4927-150 in frames: 4777 frames - 1 frame is 2352 bytes
       -> 4777*2352 = 11235504 bytes
    2: 22735-4927 : 17808 frames
    */

    public interface ProgressListener {

        void onProgress(int position, int nbCorr, int nbOverlap, int nbJitter);

    }

    /**
     * -A --analyze-drive              : run and log a complete analysis of drive
     * caching, timing and reading behavior;
     * verifies that cdparanoia is correctly
     * modelling a sprcific drive's cache and
     * read behavior. Implies -vQL
     */
    public String analyzeDrive() throws InterruptedException, ProcException, IOException {
        args.add("-A");
        return run();
    }

    /**
     * -v --verbose                    : extra verbose operation
     */
    public CDParanoia verbose() {
        //todo can't if -q
        args.add("-v");
        return this;
    }

    /**
     * -q --quiet                      : quiet operation
     */
    public CDParanoia quiet() {
        //todo can't if -v
        args.add("-q");
        return this;
    }

    /**
     *   -e --stderr-progress            : force output of progress information to
     *                                     stderr (for wrapper scripts)
     */
    public CDParanoia forceOutputProgressToErr() {
        args.add("-e");
        return this;
    }

    /**
     * -l --log-summary [<file>]       : save result summary to file, default
     *                                     filename cdparanoia.log
     */
    public CDParanoia log() {
        args.add("-l");
        return this;
    }

    /**
     * -L --log-debug   [<file>]       : save detailed device autosense and
     *                                     debugging output to file, default
     *                                     filename cdparanoia.log
     */
    public CDParanoia logDebug() {
        args.add("-L");
        return this;
    }

    /**
     *   -V --version                    : print version info and quit
     */
    public String version() throws InterruptedException, ProcException, IOException {
        args.add("-V");
        return run();
    }

    /**
     *   -Q --query                      : autosense drive, query disc and quit
     */
    public String query() throws InterruptedException, ProcException, IOException {
        args.add("-Q");
        return run();
    }

    /**
     *   -B --batch                      : 'batch' mode (saves each track to a
     *                                     seperate file.
     */
    public CDParanoia batch() {
        args.add("-B");
        return this;
    }

    /**
     *   -s --search-for-drive           : do an exhaustive search for drive
     */
    public CDParanoia searchDrive() {
        args.add("-s");
        return this;
    }

    /**
     *   -h --help                       : print help
     */
    public String help() throws InterruptedException, ProcException, IOException {
        args.add("-h");
        return run();
    }

    /**
     * -p --output-raw                 : output raw 16 bit PCM in host byte
     *                                     order
     */
    public CDParanoia outputRawHostByteOrder() {
        args.add("-p");
        return this;
    }

    /**
     *   -r --output-raw-little-endian   : output raw 16 bit little-endian PCM
     */
    public CDParanoia outputLittleEndian() {
        //todo can't if -w or -R or -f or -a
        args.add("-r");
        return this;
    }

    /**
     * -Z --disable-paranoia           : disable all paranoia checking
     */
    public CDParanoia disableParanoia() {
        args.add("-Z");
        return this;
    }

    /**
     * -Y --disable-extra-paranoia     : only do cdda2wav-style overlap checking
     */
    public CDParanoia disableExtraParanoia() {
        args.add("-Y");
        return this;
    }

    /**
     * -X --abort-on-skip              : abort on imperfect reads/skips
     */
    public CDParanoia abortOnSkip() {
        args.add("-X");
        return this;
    }

    /**
     *  -R --output-raw-big-endian      : output raw 16 bit big-endian PCM
     */
    public CDParanoia outputBigEndian() {
        //todo can't if -r or -w or -f or -a
        args.add("-R");
        return this;
    }

    /**
     *   -w --output-wav                 : output as WAV file (default)
     */
    //default
    public CDParanoia outputWave() {
        //todo can't if -r or -R or -f or -a
        args.add("-w");
        return this;
    }

    /**
     *   -f --output-aiff                : output as AIFF file
     */
    public CDParanoia outputAiff() {
        //todo can't if -r or -R or -w or -a
        args.add("-f");
        return this;
    }

    /**
     * -a --output-aifc                : output as AIFF-C file
     */
    public CDParanoia outputAifc() {
        args.add("-a");
        //todo can't if -r or -R or -f or -w
        return this;
    }

    /**
     *   -c --force-cdrom-little-endian  : force treating drive as little endian
     */
    public CDParanoia forceLittleEndianDrive() {
        args.add("-c");
        //todo can't if -C
        return this;
    }

    /**
     * -C --force-cdrom-big-endian     : force treating drive as big endian
     */
    public CDParanoia forceBigEndianDrive() {
        args.add("-C");
        //todo can't if -c
        return this;
    }

    /**
     *  -n --force-default-sectors <n>  : force default number of sectors in read
     *                                     to n sectors
     */
    public CDParanoia forceReadSectors() {
        args.add("-n");
        return this;
    }

    /**
     * -o --force-search-overlap  <n>  : force minimum overlap search during
     *                                     verification to n sectors
     */
    public CDParanoia forceSearchOverlap() {
        args.add("-o");
        return this;
    }

    /**
     * -d --force-cdrom-device   <dev> : use specified device; disallow
     *                                     autosense
     */
    public CDParanoia device(String device) {
        //can't if -k or -g
        args.add("-d");
        args.add(device);//TODO null/empty check
        return this;
    }

    /**
     * -k --force-cooked-device  <dev> : use specified cdrom device and force
     *                                     use of the old 'cooked ioctl' kernel
     *                                     interface. -k cannot be used with -d
     *                                     or -g.
     */
    public CDParanoia cookedDevice(String device) {
        //can't if -d or -g
        args.add("-k");
        args.add(device);//TODO null/empty check
        return this;
    }

    /**
     * -g --force-generic-device <dev> : use specified generic scsi device and
     *                                     force use of the old SG kernel
     *                                     interface. -g cannot be used with -k.
     */
    public CDParanoia genericDevice(String device) {
        //can't if -d or -k
        args.add("-g");
        args.add(device);//TODO null/empty check
        return this;
    }

    /**
     * -S --force-read-speed <n>       : read from device at specified speed; by
     * default, cdparanoia sets drive to full
     * speed.
     *
     */
    public CDParanoia forceReadSpeed(int n) {
        if (n != 0) {
            args.add("-S");
            args.add(n + "");
        }
        return this;
    }

    /**
     * -t --toc-offset <n>             : Add <n> sectors to the values reported
     *                                     when addressing tracks. May be negative
     */
    public CDParanoia tocOffset(int n) {
        //can't if -d or -k
        if (n != 0) {
            args.add("-t");
            args.add(n + "");
        }
        return this;
    }

    /**
     * -T --toc-bias                   : Assume that the beginning offset of
     *                                     track 1 as reported in the TOC will be
     *                                     addressed as LBA 0.  Necessary for some
     *                                     Toshiba drives to get track boundaries
     *                                     correct
     */
    public CDParanoia tocBias() {
        args.add("-T");
        return this;
    }

    /**
     *  -O --sample-offset <n>          : Add <n> samples to the offset when
     *                                     reading data.  May be negative.
     */
    public CDParanoia sampleOffset(int n) {
        if (n != 0) {
            args.add("-O");
            args.add(n + "");
        }
        return this;
    }

    /**
     * -z --never-skip[=n]             : never accept any less than perfect
     * data reconstruction (don't allow 'V's)
     * but if [n] is given, skip after [n]
     * retries without progress.
     */
    public CDParanoia neverSkip(int n) {
        args.add("-z");
        if (n != 0) {
            args.add(n + "");
        }
        return this;
    }

}

/*

SPAN ARGUMENT:
The span argument may be a simple track number or a offset/span
specification.  The syntax of an offset/span takes the rough form:

                       1[ww:xx:yy.zz]-2[aa:bb:cc.dd]

Here, 1 and 2 are track numbers; the numbers in brackets provide a
finer grained offset within a particular track. [aa:bb:cc.dd] is in
hours/minutes/seconds/sectors format. Zero fields need not be
specified: [::20], [:20], [20], [20.], etc, would be interpreted as
twenty seconds, [10:] would be ten minutes, [.30] would be thirty
sectors (75 sectors per second).

When only a single offset is supplied, it is interpreted as a starting
offset and ripping will continue to the end of he track.  If a single
offset is preceeded or followed by a hyphen, the implicit missing
offset is taken to be the start or end of the disc, respectively. Thus:

    1:[20.35]    Specifies ripping from track 1, second 20, sector 35 to
                 the end of track 1.

    1:[20.35]-   Specifies ripping from 1[20.35] to the end of the disc

    -2           Specifies ripping from the beginning of the disc up to
                 (and including) track 2

    -2:[30.35]   Specifies ripping from the beginning of the disc up to
                 2:[30.35]

    2-4          Specifies ripping from the beginning of track two to the
                 end of track 4.

Don't forget to protect square brackets and preceeding hyphens from
the shell...

A few examples, protected from the shell:
  A) query only with exhaustive search for a drive and full reporting
     of autosense:
       cdparanoia -vsQ

  B) extract up to and including track 3, putting each track in a seperate
     file:
       cdparanoia -B -- "-3"

  C) extract from track 1, time 0:30.12 to 1:10.00:
       cdparanoia "1[:30.12]-1[1:10]"

Submit bug reports to paranoia@xiph.org
 */