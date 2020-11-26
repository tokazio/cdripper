package fr.tokazio.player.decoders;

import fr.tokazio.player.Callback;
import fr.tokazio.player.Decoder;
import org.jflac.PCMProcessor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;

public class WaveDecoder implements Decoder {
    @Override
    public void load(File file) throws IOException {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void decode(SourceDataLine line) throws Throwable {

    }

    @Override
    public void stop() throws IOException {

    }

    @Override
    public AudioFormat getAudioFormat() {
        return null;
    }

    @Override
    public void onEnd(Callback callback) {

    }

    @Override
    public void addPCMProcessor(PCMProcessor pcmProcessor) {

    }
}
