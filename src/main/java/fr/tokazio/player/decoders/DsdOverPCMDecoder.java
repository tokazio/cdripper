package fr.tokazio.player.decoders;

import fr.tokazio.player.Callback;
import fr.tokazio.player.Decoder;
import org.jflac.PCMProcessor;
import org.justcodecs.dsd.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;

public class DsdOverPCMDecoder implements Decoder {

    org.justcodecs.dsd.Decoder decoder;

    DSDFormat<?> dsd;
    long sampleCount = 0;

    int[][] samples = new int[2][2048];

    @Override
    public void load(File file) throws IOException {
        long sampleCount = 0;
        try {
            decoder = new org.justcodecs.dsd.Decoder();
            if (file.getName().endsWith(".dsf")) {
                dsd = new DSFFormat();
            } else if (file.getName().endsWith(".iso")) {
                dsd = new DISOFormat();
            } else
                dsd = new DFFFormat();

            dsd.init(new Utils.RandomDSDStream(file));
            decoder.init(dsd);
            decoder.seek(0);
        } catch (org.justcodecs.dsd.Decoder.DecodeException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void decode(SourceDataLine line) throws Throwable {
        org.justcodecs.dsd.Decoder.PCMFormat pcmf = pcmFormat();
        pcmf.bitsPerSample = 24;
        decoder.setPCMFormat(pcmf);
        int channels = 2;
        int bytesChannelSample = 2; //pcmf.bitsPerSample / 8;
        int bytesSample = channels * bytesChannelSample;
        byte[] playBuffer = new byte[bytesSample * 2048];
        int testSeek = 0;
        do {
            int nsampl = decoder.decodePCM(samples);
            if (nsampl <= 0) {
                break;
            }
            int bp = 0;
            for (int s = 0; s < nsampl; s++) {
                for (int c = 0; c < channels; c++) {
                    //System.out.printf("%x", samples[c][s]);
                    samples[c][s] >>= 8;
                    for (int b = 0; b < bytesChannelSample; b++)
                        playBuffer[bp++] = (byte) ((samples[c][s] >> (b * 8)) & 255);
                }
            }
            //for (int k=0;k<bp; k++)
            //System.out.printf("%x", playBuffer[k]);
            line.write(playBuffer, 0, bp);
            sampleCount += nsampl;
            if (testSeek > 0 && sampleCount > pcmf.sampleRate * 10) {
                decoder.seek((long) decoder.getSampleRate() * (testSeek));
                testSeek = 0;
            }
        } while (true);

    }

    @Override
    public void stop() throws IOException {
        try {
            decoder.suspend();
        } catch (org.justcodecs.dsd.Decoder.DecodeException e) {
            throw new IOException(e);
        }
        decoder.dispose();
    }

    @Override
    public AudioFormat getAudioFormat() {
        org.justcodecs.dsd.Decoder.PCMFormat pcmf = pcmFormat();
        return new AudioFormat(pcmf.sampleRate, pcmf.bitsPerSample, pcmf.channels, true, pcmf.lsb);
    }

    //devrait marcher: PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian
    private org.justcodecs.dsd.Decoder.PCMFormat pcmFormat() {
        //System.out.printf("Samples %d duration %ds%n",  decoder.getSampleCount(), decoder.getSampleCount()/decoder.getSampleRate());
        org.justcodecs.dsd.Decoder.PCMFormat pcmf = new org.justcodecs.dsd.Decoder.PCMFormat();
        pcmf.sampleRate = 44100;// * 2 * 2;
        pcmf.bitsPerSample = 16;
        //System.out.printf("clip: %x %x  %x-%x%n",((1 << pcmf.bitsPerSample) - 1) >> 1, 1 << pcmf.bitsPerSample, Short.MAX_VALUE, Short.MIN_VALUE);
        pcmf.channels = 2;
        return pcmf;
    }

    @Override
    public void onEnd(Callback callback) {

    }

    @Override
    public void addPCMProcessor(PCMProcessor pcmProcessor) {

    }

}
