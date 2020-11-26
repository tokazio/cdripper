package fr.tokazio.player;

import javax.sound.sampled.AudioFormat;

public final class AudioUtil {

    //Master out audio format (CD quality)
    public static final AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
    public static final int BYTES_PER_SAMPLES = audioFormat.getFrameSize();//todo get it from format of the file

    //Project BPM
    public static final int bpm = 130;
    public static final byte[] ZERO_BYTE = new byte[0];
    public static final float[] ZERO_FLOAT = new float[0];
    public static final int bytesPerSecond = 44100 * 2 * 2;//rate hz * bytes (bits/8) * channels todo (int) format.getSampleRate() * format.getFrameSize();
    //Player speed
    public static final int bytesPerPeriod = mod4(bytesPerBpm(audioFormat, bpm) / 8);//10176;//important, doit être synchro avec la startPosition pour ne pas 'manger' le début d'un track, cf AbstractAudioTrack read from>startPosition
    public static final int periodMs = (int) (((float) bytesPerPeriod / bytesPerSecond) * 1000);
    //Project ZOOM
    public static float SAMPLES_BY_PIXELS = 200;//less is more zoom


    private AudioUtil() {
        //hide
    }

    /**
     * Round to 4 bytes compatible size
     */
    public static int mod4(int v) {
        while (v % 4 != 0) {
            v++;
        }
        return v;
    }

    //bytesPerBpm = (sampleRate*bytesPerSample) / (bpm/60)
    //81666 = (44100 * 4) / (130 / 60)
    // 130bpm -> 130/60 -> 2.16666666666bps
    //44100 samples / s -> 44100*4=176400 -> bytes / s
    //1bpm = 176400/2.166666666 = 81416 -> !!! mod4 = 0

    /**
     * How many bytes for one BPM
     */
    public static int bytesPerBpm(AudioFormat format, int bpm) {
        return mod4((int) Math.ceil((format.getSampleRate() * format.getFrameSize()) / (bpm / 60f)));
    }

    public static float[][] getAmplitudes(final AudioFormat format, final byte[] in) {
        if (in.length == 0) {
            return new float[0][format.getChannels()];
        }
        final float[][] samples = new float[(int) format.getSampleRate()][format.getChannels()];
        for (int i = 0, s = 0; i < in.length; ) {
            for (int c = 0; c < format.getChannels(); c++) {
                int sample = 0;
                switch (format.getSampleSizeInBits()) {
                    //signed: -128 to 127 (inclusive)
                    //unsigned: 0 to 255
                    case 8:
                        samples[s++][c] = sample;
                        break;
                    //signed: -32,768 to 32,767
                    //unsigned: 0 to 65,535
                    case 16:
                        //merging the 2 bytes value as one with the endianess
                        if (format.isBigEndian()) {
                            sample |= in[i++] << 8;
                            sample |= in[i++] & 0xFF;
                        } else {
                            sample |= in[i++] & 0xFF;
                            sample |= in[i++] << 8;
                        }

                        samples[s++][c] = sample / (format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED ? 32768f : 65535);
                        break;
                    //signed: -8388608 to -8388607
                    //unsigned: 0 to 16777215
                    case 24:
                        //merging the 3 bytes value as one with the endianess
                        if (format.isBigEndian()) {
                            sample |= in[i++] << 8;
                            sample |= in[i++] << 8;
                            sample |= in[i++] & 0xFF;
                        } else {
                            sample |= in[i++] & 0xFF;
                            sample |= in[i++] << 8;
                            sample |= in[i++] << 8;
                        }
                        samples[s++][c] = sample / (format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED ? 8388608 : 16777215f);
                        break;
                    default:
                        throw new UnsupportedOperationException("Sample size of " + format.getSampleSizeInBits() + "bits not supported. Only 8/16/24bits are supported");
                }
            }
        }
        return samples;
    }
}
