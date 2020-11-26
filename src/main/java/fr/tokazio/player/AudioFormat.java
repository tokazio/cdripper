package fr.tokazio.player;

/**
 * @author rpetit
 */
public class AudioFormat {

    public static final int NOT_SPECIFIED = -1;

    protected float sampleRate;
    protected int sampleSizeInBits;
    protected int channels;
    protected int frameSize;
    protected float frameRate;
    protected boolean bigEndian;
    private String encoding;

    public AudioFormat() {
        //for json
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public AudioFormat sampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
        return this;
    }

    public int getSampleSizeInBits() {
        return sampleSizeInBits;
    }

    public AudioFormat sampleSizeInBits(int sampleSizeInBits) {
        this.sampleSizeInBits = sampleSizeInBits;
        return this;
    }

    public int getChannels() {
        return channels;
    }

    public AudioFormat channels(int channels) {
        this.channels = channels;
        return this;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public AudioFormat frameSize(int frameSize) {
        this.frameSize = frameSize;
        return this;
    }

    public float getFrameRate() {
        return frameRate;
    }

    public AudioFormat frameRate(float frameRate) {
        this.frameRate = frameRate;
        return this;
    }

    public boolean isBigEndian() {
        return bigEndian;
    }

    public AudioFormat bigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
        return this;
    }

    public AudioFormat encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    @Override
    public String toString() {
        String sEncoding = "";
        if (getEncoding() != null) {
            sEncoding = getEncoding().toString() + " ";
        }

        String sSampleRate;
        if (getSampleRate() == (float) NOT_SPECIFIED) {
            sSampleRate = "unknown sample rate, ";
        } else {
            sSampleRate = "" + getSampleRate() + " Hz, ";
        }

        String sSampleSizeInBits;
        if (getSampleSizeInBits() == (float) NOT_SPECIFIED) {
            sSampleSizeInBits = "unknown bits per sample, ";
        } else {
            sSampleSizeInBits = "" + getSampleSizeInBits() + " bit, ";
        }

        String sChannels;
        switch (getChannels()) {
            case 1:
                sChannels = "mono, ";
                break;
            case 2:
                sChannels = "stereo, ";
                break;
            default:
                if (getChannels() == NOT_SPECIFIED) {
                    sChannels = " unknown number of channels, ";
                } else {
                    sChannels = "" + getChannels() + " channels, ";
                }
                break;
        }

        String sFrameSize;
        if (getFrameSize() == (float) NOT_SPECIFIED) {
            sFrameSize = "unknown frame size, ";
        } else {
            sFrameSize = "" + getFrameSize() + " bytes/frame, ";
        }

        String sFrameRate = "";
        if (Math.abs(getSampleRate() - getFrameRate()) > 0.00001) {
            if (getFrameRate() == (float) NOT_SPECIFIED) {
                sFrameRate = "unknown frame rate, ";
            } else {
                sFrameRate = getFrameRate() + " frames/second, ";
            }
        }

        String sEndian = "";
        if ((getEncoding().equals(javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED)
                || getEncoding().equals(javax.sound.sampled.AudioFormat.Encoding.PCM_UNSIGNED))
                && ((getSampleSizeInBits() > 8)
                || (getSampleSizeInBits() == NOT_SPECIFIED))) {
            if (isBigEndian()) {
                sEndian = "big-endian";
            } else {
                sEndian = "little-endian";
            }
        }

        return sEncoding
                + sSampleRate
                + sSampleSizeInBits
                + sChannels
                + sFrameSize
                + sFrameRate
                + sEndian;

    }
}
