package fr.tokazio.player;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @link http://www.topherlee.com/software/pcm-tut-wavformat.html
 * @linl https://howtodoinjava.com/java7/nio/memory-mapped-files-mappedbytebuffer/
 * @see javax.sound.sampled.AudioInputStream
 */
public class WavInputStream {

    protected static final int MMAP_LEN = 1024 * 4;
    private static final int DECAY = 44;//44bytes of header in a wav file
    protected RandomAccessFile file;
    protected MappedByteBuffer in;
    protected AudioFormat format;

    public WavInputStream(final String filename) {
        this(new File(filename));
    }

    public WavInputStream(final File file) {
        super();
        try {
            this.file = new RandomAccessFile(file, "r");
            in = this.file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            in.order(ByteOrder.LITTLE_ENDIAN);
            readHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHeader() {
        //1 - 4     "RIFF"	Marks the file as a riff file. Characters are each 1 byte long.
        byte r = in.get();//[#1]
        byte i = in.get();//[#2]
        byte f1 = in.get();//[#3]
        byte f2 = in.get();//[#4]
        //todo verify riff

        //5 - 8 File size (integer)	Size of the overall file - 8 bytes, in bytes (32-bit integer). Typically, you'd fill this in after creation.
        int fileSize = in.getInt();//[#8]
        //todo verify with os file size

        //9 -12	"WAVE"	File Type Header. For our purposes, it always equals "WAVE".
        byte w = in.get();//[#9]
        byte a = in.get();//[#10]
        byte v = in.get();//[#11]
        byte e = in.get();//[#12]
        //todo verify wave

        //13-16	Format chunk marker. Includes trailing null
        int fmtChunk = in.getInt();//[#16] ("fmt ")
        //17-20	Length of format data as listed above
        int fmtData = in.getInt();//[#20]

        //21-22	Type of format (1 is PCM) - 2 byte integer
        short type = in.getShort();//[#22] (1) is PCM

        //23-24	Number of Channels - 2 byte integer
        short channels = in.getShort();//[#24] (2)

        //25-28	Sample Rate - 32 byte integer. Common values are 44100 (CD), 48000 (DAT). Sample Rate = Number of Samples per second, or Hertz.
        int sampleRate = in.getInt();//[#28] (44100)

        //29-32	(Sample Rate * BitsPerSample * Channels) / 8
        int sampleSize = in.getInt();//[#32] (176400)

        //33-34	(BitsPerSample * Channels) / 8 | 1 = 8 bit mono | 2 = 8 bit stereo or 16 bit mono | 4 = 16 bit stereo
        short frameSize = in.getShort();//[#34] (4)

        //35-36	Bits per sample
        short bitsPerSample = in.getShort();//[#36] (16)

        //37-40	"data"	"data" chunk header. Marks the beginning of the data section.
        int dataChunk = in.getInt();//[#40]
        //41-44	File size (data)	Size of the data section.
        int dataLen = in.getInt();//[#44]

        format = new AudioFormat(sampleRate, bitsPerSample, channels, true, false);
        System.out.println("file size is " + fileSize + " for " + channels + " channels " + sampleRate + "hz " + bitsPerSample + "bits. There are " + dataLen + "bytes of audio data");
        assert (in.position() == DECAY);

        //one frame is n channels samples
        //frameSize = 2 * 16bits = 2 * 2bytes = 4bytes
    }

    public AudioFormat getFormat() {
        return format;
    }

    public void position(int l) {
        if (l > 0 && l < in.limit()) {
            in.position(l);
        }
    }

    public int read(byte[] dst, int offset, int len) {
        try {
            in.get(dst, offset, len);
            return len;
        } catch (BufferUnderflowException ex) {
            //end
        }
        return 0;
    }
}