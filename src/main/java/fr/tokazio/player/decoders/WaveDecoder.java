package fr.tokazio.player.decoders;

import fr.tokazio.player.Callback;
import fr.tokazio.player.Decoder;
import fr.tokazio.player.WavInputStream;
import org.jflac.PCMProcessor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;

public class WaveDecoder implements Decoder {

    private WavInputStream wavInputStream;

    @Override
    public void load(File file) throws IOException {
        wavInputStream = new WavInputStream(file);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void decode(final SourceDataLine line) throws Throwable {
        byte[] playBuffer = new byte[2048];//*bytesPerSample
        do {
            int r = wavInputStream.read(playBuffer, 0, playBuffer.length);
            System.out.println("> " + r);
            line.write(playBuffer, 0, r);
        } while (true);
    }

    @Override
    public void stop() throws IOException {

    }

    @Override
    public AudioFormat getAudioFormat() {
        return wavInputStream.getFormat();
    }

    @Override
    public void onEnd(Callback callback) {

    }

    @Override
    public void addPCMProcessor(PCMProcessor pcmProcessor) {

    }
}
