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

    public CDParanoia() {
        args.add("cdparanoia");
    }

    public int rip(int trackNo, File outFile) throws IOException, InterruptedException {
        args.add(String.valueOf(trackNo));
        args.add(outFile.getAbsolutePath());
        try {
            run();
        } catch (ProcException e) {
            LOGGER.error("\nError ripping track #" + trackNo + " to " + outFile.getAbsolutePath());
            return e.getCode();
        }
        return 0;
    }

    private String run() throws IOException, InterruptedException, ProcException {
        final String[] cmd = args.toArray(new String[0]);
        LOGGER.info("Ripping command: " + Arrays.toString(cmd));
        final ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();//ça c'est cool
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
                final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
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
        t2.setDaemon(true);
        t2.setName("CDParanoia-errorStream-reader");
        t2.start();
        proc.waitFor();
        t1.interrupt();
        t2.interrupt();
        if (proc.exitValue() != 0) {
            throw new ProcException(proc.exitValue(), sb.toString());
        }
        LOGGER.info("Ripping track ended");

        return sb.toString();
    }

    /**
     * -A --analyze-drive              : run and log a complete analysis of drive
     *                                     caching, timing and reading behavior;
     *                                     verifies that cdparanoia is correctly
     *                                     modelling a sprcific drive's cache and
     *                                     read behavior. Implies -vQL
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