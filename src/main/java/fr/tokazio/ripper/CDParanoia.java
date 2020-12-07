package fr.tokazio.ripper;

import java.util.LinkedList;
import java.util.List;

public class CDParanoia {

    private final List<String> args = new LinkedList<>();

    public String analyzeDrive() {
        //TODO
        return "TODO";
    }

    public CDParanoia verbose() {
        //todo can't if -q
        args.add("-v");
        return this;
    }

    public CDParanoia quiet() {
        //todo can't if -v
        args.add("-q");
        return this;
    }

    public CDParanoia forceOutputProgressToErr() {
        args.add("-e");
        return this;
    }

    public CDParanoia log() {
        args.add("-l");
        return this;
    }

    public CDParanoia logDebug() {
        args.add("-L");
        return this;
    }

    public String version() {
        //TODO
        return "TODO";
    }

    public String query() {
        //TODO
        return "todo";
    }

    public CDParanoia batch() {
        args.add("-B");
        return this;
    }

    public CDParanoia searchDrive() {
        args.add("-s");
        return this;
    }

    public String help() {
        return "todo";
    }

    public CDParanoia outputRawHostByteOrder() {
        args.add("-p");
        return this;
    }

    public CDParanoia outputLittleEndian() {
        //todo can't if -w or -R or -f or -a
        args.add("-r");
        return this;
    }

    public CDParanoia outputBigEndian() {
        //todo can't if -r or -w or -f or -a
        args.add("-R");
        return this;
    }

    //default
    public CDParanoia outputWave() {
        //todo can't if -r or -R or -f or -a
        args.add("-w");
        return this;
    }

    public CDParanoia outputAiff() {
        //todo can't if -r or -R or -w or -a
        args.add("-f");
        return this;
    }

    public CDParanoia outputAifc() {
        args.add("-a");
        //todo can't if -r or -R or -f or -w
        return this;
    }

    public CDParanoia forceLittleEndianDrive() {
        args.add("-c");
        //todo can't if -C
        return this;
    }

    public CDParanoia forceBigEndianDrive() {
        args.add("-C");
        //todo can't if -c
        return this;
    }

    public CDParanoia forceReadSectors() {
        args.add("-n");
        return this;
    }

    public CDParanoia forceSearchOverloap() {
        args.add("-o");
        return this;
    }

    public CDParanoia device(String device) {
        //can't if -k or -g
        args.add("-d");
        args.add(device);//TODO null/empty check
        return this;
    }

    public CDParanoia cookedDevice(String device) {
        //can't if -d or -g
        args.add("-k");
        args.add(device);//TODO null/empty check
        return this;
    }

    public CDParanoia genericDevice(String device) {
        //can't if -d or -k
        args.add("-g");
        args.add(device);//TODO null/empty check
        return this;
    }

    public CDParanoia tocOffset(int n) {
        //can't if -d or -k
        args.add("-t");
        args.add(n);//TODO null/empty check
        return this;
    }

    public CDParanoia tocBias() {
        args.add("-T");
        return this;
    }

    /**
     * -O --sample-offset <n>          : Add <n> samples to the offset when
     * reading data.  May be negative.
     */
    public CDParanoia sampleOffset(int n) {
        args.add("-O");
        args.add(n + "");
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
        args.add(n + "");
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


}

/*
cdparanoia III release 10.2 (September 11, 2008)

(C) 2008 Monty <monty@xiph.org> and Xiph.Org

Report bugs to paranoia@xiph.org
http://www.xiph.org/paranoia/
USAGE:
  cdparanoia [options] <span> [outfile]

OPTIONS:
  -A --analyze-drive              : run and log a complete analysis of drive
                                    caching, timing and reading behavior;
                                    verifies that cdparanoia is correctly
                                    modelling a sprcific drive's cache and
                                    read behavior. Implies -vQL

  -v --verbose                    : extra verbose operation
  -q --quiet                      : quiet operation
  -e --stderr-progress            : force output of progress information to
                                    stderr (for wrapper scripts)
  -l --log-summary [<file>]       : save result summary to file, default
                                    filename cdparanoia.log
  -L --log-debug   [<file>]       : save detailed device autosense and
                                    debugging output to file, default
                                    filename cdparanoia.log
  -V --version                    : print version info and quit
  -Q --query                      : autosense drive, query disc and quit
  -B --batch                      : 'batch' mode (saves each track to a
                                    seperate file.
  -s --search-for-drive           : do an exhaustive search for drive
  -h --help                       : print help

  -p --output-raw                 : output raw 16 bit PCM in host byte
                                    order
  -r --output-raw-little-endian   : output raw 16 bit little-endian PCM
  -R --output-raw-big-endian      : output raw 16 bit big-endian PCM
  -w --output-wav                 : output as WAV file (default)
  -f --output-aiff                : output as AIFF file
  -a --output-aifc                : output as AIFF-C file

  -c --force-cdrom-little-endian  : force treating drive as little endian
  -C --force-cdrom-big-endian     : force treating drive as big endian
  -n --force-default-sectors <n>  : force default number of sectors in read
                                    to n sectors
  -o --force-search-overlap  <n>  : force minimum overlap search during
                                    verification to n sectors
  -d --force-cdrom-device   <dev> : use specified device; disallow
                                    autosense
  -k --force-cooked-device  <dev> : use specified cdrom device and force
                                    use of the old 'cooked ioctl' kernel
                                    interface. -k cannot be used with -d
                                    or -g.
  -g --force-generic-device <dev> : use specified generic scsi device and
                                    force use of the old SG kernel
                                    interface. -g cannot be used with -k.
  -S --force-read-speed <n>       : read from device at specified speed; by
                                    default, cdparanoia sets drive to full
                                    speed.
  -t --toc-offset <n>             : Add <n> sectors to the values reported
                                    when addressing tracks. May be negative
  -T --toc-bias                   : Assume that the beginning offset of
                                    track 1 as reported in the TOC will be
                                    addressed as LBA 0.  Necessary for some
                                    Toshiba drives to get track boundaries
                                    correct
  -O --sample-offset <n>          : Add <n> samples to the offset when
                                    reading data.  May be negative.
  -z --never-skip[=n]             : never accept any less than perfect
                                    data reconstruction (don't allow 'V's)
                                    but if [n] is given, skip after [n]
                                    retries without progress.
  -Z --disable-paranoia           : disable all paranoia checking
  -Y --disable-extra-paranoia     : only do cdda2wav-style overlap checking
  -X --abort-on-skip              : abort on imperfect reads/skips

OUTPUT SMILIES:
  :-)   Normal operation, low/no jitter
  :-|   Normal operation, considerable jitter
  :-/   Read drift
  :-P   Unreported loss of streaming in atomic read operation
  8-|   Finding read problems at same point during reread; hard to correct
  :-0   SCSI/ATAPI transport error
  :-(   Scratch detected
  ;-(   Gave up trying to perform a correction
  8-X   Aborted (as per -X) due to a scratch/skip
  :^D   Finished extracting

PROGRESS BAR SYMBOLS:
<space> No corrections needed
   -    Jitter correction required
   +    Unreported loss of streaming/other error in read
   !    Errors are getting through stage 1 but corrected in stage2
   e    SCSI/ATAPI transport error (corrected)
   V    Uncorrected error/skip

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