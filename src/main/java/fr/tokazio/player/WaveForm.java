package fr.tokazio.player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

//@link https://stackoverflow.com/questions/26452026/how-to-make-waveform-rendering-more-interesting
public class WaveForm {

    private final AudioInputStreamProvider provider;
    private float[] samples = AudioUtil.ZERO_FLOAT;

    public WaveForm(AudioInputStreamProvider provider) {
        this.provider = provider;
    }

    public WaveForm() {
        this.provider = file -> {
            return AudioSystem.getAudioInputStream(file);
        };
    }

    public WaveForm load(final File file) {
        try {
            final AudioInputStream in = provider.provide(file);
            final AudioFormat fmt = in.getFormat();

            /*
            if (fmt.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                throw new UnsupportedAudioFileException("unsigned");
            }

             */

            boolean big = fmt.isBigEndian();
            int chans = fmt.getChannels();
            int bits = fmt.getSampleSizeInBits();
            int bytes = bits + 7 >> 3;

            int frameLength = (int) in.getFrameLength();
            int bufferLength = chans * bytes * 1024;

            float[] samples = new float[frameLength];
            byte[] buf = new byte[bufferLength];

            int i = 0;
            int bRead;
            while ((bRead = in.read(buf)) > -1) {

                for (int b = 0; b < bRead; ) {
                    double sum = 0;

                    // (sums to mono if multiple channels)
                    for (int c = 0; c < chans; c++) {
                        if (bytes == 1) {
                            sum += buf[b++] << 8;

                        } else {
                            int sample = 0;

                            // (quantizes to 16-bit)
                            if (big) {
                                sample |= (buf[b++] & 0xFF) << 8;
                                sample |= (buf[b++] & 0xFF);
                                b += bytes - 2;
                            } else {
                                b += bytes - 2;
                                sample |= (buf[b++] & 0xFF);
                                sample |= (buf[b++] & 0xFF) << 8;
                            }

                            final int sign = 1 << 15;
                            final int mask = -1 << 16;
                            if ((sample & sign) == sign) {
                                sample |= mask;
                            }

                            sum += sample;
                        }
                    }

                    samples[i++] = (float) (sum / chans);
                }
            }
            this.samples = samples;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    public BufferedImage drawWave(int w, int h) {
        final BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        float max = 0;
        for (float f : samples) {
            if (f > max) {
                max = f;
            }
        }
        int x = 0;
        int k = 0;
        int h2i = h / 2;
        while (k < samples.length) {
            for (int m = k; m < k + AudioUtil.SAMPLES_BY_PIXELS; m++) {
                if (m % 20354 == 0) {
                    g2d.setColor(Colors.waveColorSep);
                    g2d.drawLine(x, 0, x, h);
                }
            }
            int sample = (int) ((samples[k] / max) * h2i);
            int posY = h2i - sample;
            int negY = h2i + sample;
            g2d.setColor(Colors.waveColor);
            g2d.drawLine(x, posY, x, negY);
            k += AudioUtil.SAMPLES_BY_PIXELS;
            x += 1;
            if (x > (samples.length / AudioUtil.SAMPLES_BY_PIXELS) - 1) {
                break;
            }
        }
        g2d.dispose();
        return img;
    }

}
