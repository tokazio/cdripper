package fr.tokazio.player.decoders;

import fr.tokazio.player.Callback;
import fr.tokazio.player.Decoder;
import org.jflac.PCMProcessor;
import org.justcodecs.dsd.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;

public class DsdDecoder implements Decoder {

    org.justcodecs.dsd.Decoder decoder;

    DSDFormat<?> dsd;
    byte[] samples = new byte[2 * 2048];
    long sampleCount = 0;

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
            /*
            if (file.getAbsolutePath().toUpperCase().startsWith("FILE:/")) {
                try {
                    file = new URL(URLDecoder.decode(file, "UTF-8")).getFile();
                } catch (Exception e) {
                    // ignore
                }
            }

             */
            dsd.init(new Utils.RandomDSDStream(file));
            decoder.init(dsd);
            /*
            System.out.printf("Playing ... %s%n", dsd);

            SourceDataLine dl = getDSDLine(dsd);
            if (dl != null) {
                byte[] samples = new byte[2 * 2048];
                dl.open();
                dl.start();
                do {
                    int nsampl = decoder.decodeDSD(dsd.getNumChannels(), samples);
                    if (nsampl <= 0)
                        break;
                    dl.write(samples, 0, nsampl);
                    sampleCount += nsampl;
                } while (true);
                dl.stop();
                dl.close();
            } else {
                //System.out.printf("Samples %d duration %ds%n",  decoder.getSampleCount(), decoder.getSampleCount()/decoder.getSampleRate());
                org.justcodecs.dsd.Decoder.PCMFormat pcmf = new org.justcodecs.dsd.Decoder.PCMFormat();
                pcmf.sampleRate = 44100 * 2 * 2;
                pcmf.bitsPerSample = 16;
                //System.out.printf("clip: %x %x  %x-%x%n",((1 << pcmf.bitsPerSample) - 1) >> 1, 1 << pcmf.bitsPerSample, Short.MAX_VALUE, Short.MIN_VALUE);
                pcmf.channels = 2;
                AudioFormat af = new AudioFormat(pcmf.sampleRate, pcmf.bitsPerSample, pcmf.channels, true, pcmf.lsb);
                dl = AudioSystem.getSourceDataLine(af);
                pcmf.bitsPerSample = 24;
                decoder.setPCMFormat(pcmf);
                dl.open();
                dl.start();
                int[][] samples = new int[pcmf.channels][2048];
                int channels = (pcmf.channels > 2 ? 2 : pcmf.channels);
                int bytesChannelSample = 2; //pcmf.bitsPerSample / 8;
                int bytesSample = channels * bytesChannelSample;
                byte[] playBuffer = new byte[bytesSample * 2048];
                if (off > 0) {
                    //System.out.printf("search %d sampl rate %d%n", off, decoder.getSampleRate());
                    decoder.seek(((long)off) * decoder.getSampleRate() //44100* 64l
                    );

                } else
                    decoder.seek(0);
                int testSeek = 0;
                do {
                    int nsampl = decoder.decodePCM(samples);
                    if (nsampl <= 0)
                        break;
                    int bp = 0;
                    for (int s = 0; s < nsampl; s++) {
                        for (int c = 0; c < channels; c++) {
                            //System.out.printf("%x", samples[c][s]);
                            samples[c][s] >>=8;
                            for (int b = 0; b < bytesChannelSample; b++)
                                playBuffer[bp++] = (byte) ((samples[c][s] >> (b * 8)) & 255);
                        }
                    }
                    //for (int k=0;k<bp; k++)
                    //System.out.printf("%x", playBuffer[k]);
                    dl.write(playBuffer, 0, bp);
                    sampleCount += nsampl;
                    if (testSeek > 0 && sampleCount > pcmf.sampleRate * 10) {
                        decoder.seek((long) decoder.getSampleRate() * (testSeek));
                        testSeek = 0;
                    }
                } while (true);
                dl.stop();
                dl.close();
            }

             */
        } catch (org.justcodecs.dsd.Decoder.DecodeException e) {
            throw new IOException(e);
        }

        //decoder.dispose();
        //System.out.printf("Total samples: %d%n", sampleCount);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void decode(SourceDataLine line) throws Throwable {
        do {
            int nsampl = decoder.decodeDSD(dsd.getNumChannels(), samples);
            if (nsampl <= 0)
                break;
            line.write(samples, 0, nsampl);
            sampleCount += nsampl;
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

        return new AudioFormat(new AudioFormat.Encoding("PCM_UNSIGNED"),
                dsd.getSampleRate(), 1, dsd.getNumChannels(), 4, dsd.getSampleRate() / 32,
                true);

    }

    @Override
    public void onEnd(Callback callback) {

    }

    @Override
    public void addPCMProcessor(PCMProcessor pcmProcessor) {

    }

    /*
    protected SourceDataLine getDSDLine(DSDFormat<?> dsd) {
        try {
            return AudioSystem.getSourceDataLine(new AudioFormat(new AudioFormat.Encoding("DSD_UNSIGNED"),
                    dsd.getSampleRate(), 1, dsd.getNumChannels(), 4, dsd.getSampleRate()/32,
                    true));
        } catch (IllegalArgumentException e) {
            System.out.printf("No DSD %s%n", e);
        } catch (LineUnavailableException e) {
            System.out.printf("No DSD %s%n", e);
        }
        return null;
    }

     */
}
