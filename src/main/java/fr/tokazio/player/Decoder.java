package fr.tokazio.player;

import org.jflac.PCMProcessor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;

public interface Decoder {

    void load(File file) throws IOException;

    void pause();

    void resume();

    void decode(SourceDataLine line) throws Throwable;

    void stop() throws IOException;

    AudioFormat getAudioFormat() throws LineUnavailableException;

    void onEnd(Callback callback);

    void addPCMProcessor(PCMProcessor pcmProcessor);
}
