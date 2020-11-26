package fr.tokazio.player;

import org.jflac.FLACDecoder;
import org.jflac.PCMProcessor;
import org.jflac.metadata.StreamInfo;
import org.jflac.util.ByteData;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author romainpetit
 */
public class JavaFlacWaveform extends Thread implements PCMProcessor {

    final List<ByteData> a = new ArrayList<>();
    private FLACDecoder decoder;
    private AudioFormat format;
    private byte[] audioBytes;
    private int[] audioData;

    public void decode(final String filename) {
        try {
            decoder = new FLACDecoder(filename);
            format = decoder.getStreamInfo().getAudioFormat();
            decoder.addPCMProcessor(this);
            start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            decoder.decode();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        terminate();
    }

    private void terminate() {
        if (decoder != null) {
            try {
                decoder.stop();
            } catch (IOException ex) {
                //
            }
        }
        la();
    }

    @Override
    public void interrupt() {
        terminate();
        super.interrupt();
    }

    @Override
    public void processStreamInfo(StreamInfo streamInfo) {
        //not used
    }

    @Override
    public void processPCM(ByteData pcm) {
        //line.write(pcm.getData(), 0, pcm.getLen());
        a.add(pcm);
    }

    private int[] la() {

        int t = 0;
        for (ByteData bd : a) {
            t += bd.getLen();
        }
        audioBytes = new byte[t];
        int k = 0;
        for (ByteData bd : a) {
            for (int i = 0; i < bd.getLen(); i++) {
                audioBytes[k] = bd.getData(i);
                k++;
            }
        }


        if (format.getSampleSizeInBits() == 16) {
            int nlengthInSamples = audioBytes.length / 2;
            audioData = new int[nlengthInSamples];
            if (format.isBigEndian()) {
                for (int i = 0; i < nlengthInSamples; i++) {
                    /* First byte is MSB (high order) */
                    int MSB = audioBytes[2 * i];
                    /* Second byte is LSB (low order) */
                    int LSB = audioBytes[2 * i + 1];
                    audioData[i] = MSB << 8 | (255 & LSB);
                }
            } else {
                for (int i = 0; i < nlengthInSamples; i++) {
                    /* First byte is LSB (low order) */
                    int LSB = audioBytes[2 * i];
                    /* Second byte is MSB (high order) */
                    int MSB = audioBytes[2 * i + 1];
                    audioData[i] = MSB << 8 | (255 & LSB);
                }
            }
        } else if (format.getSampleSizeInBits() == 8) {
            int nlengthInSamples = audioBytes.length;
            audioData = new int[nlengthInSamples];
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                // PCM_SIGNED
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i];
                }
            } else {
                // PCM_UNSIGNED
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i] - 128;
                }
            }
        }// end of if..else
        System.out.println("PCM Returned===============" + audioData.length);
        return audioData;
    }

}
