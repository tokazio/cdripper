package fr.tokazio.player;


import javax.sound.sampled.AudioFormat;

public class AudioMeter {

    private final AudioFormat format;

    public AudioMeter(final AudioFormat format) {
        super();
        this.format = format;
    }

    public float[] process(final byte[] abData) {
        float[][] samples = AudioUtil.getAmplitudes(format, abData);
        return (db(samples));
    }

    private float[] db(final float[][] samples) {
        final float[] db = new float[format.getChannels()];
        for (float[] sample : samples) {
            for (int c = 0; c < format.getChannels(); c++) {
                db[c] += Math.abs(sample[c]);
            }
        }
        for (int c = 0; c < format.getChannels(); c++) {
            db[c] = (20f * (float) Math.log10(db[c] / samples.length)) / -48;//-60 to 12db
        }
        return db;
    }

    private float[] rms(final float[][] samples) {
        final float[] rms = new float[format.getChannels()];
        for (float[] sample : samples) {
            for (int c = 0; c < format.getChannels(); c++) {
                rms[c] += sample[c] * sample[c];
            }
        }
        for (int c = 0; c < format.getChannels(); c++) {
            rms[c] = (float) Math.sqrt(rms[c] / samples.length);
        }
        return rms;
    }


}
