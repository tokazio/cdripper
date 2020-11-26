package fr.tokazio.player.decoders;

import fr.tokazio.player.AudioMeter;
import fr.tokazio.player.Callback;
import fr.tokazio.player.Decoder;
import org.jflac.FLACDecoder;
import org.jflac.PCMProcessor;
import org.jflac.metadata.StreamInfo;
import org.jflac.util.ByteData;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;

public class FlacDecoder implements Decoder, PCMProcessor {

    private final FLACDecoder decoder = new FLACDecoder();
    private SourceDataLine line;
    private AudioMeter meter;

    public FlacDecoder() {
        decoder.addPCMProcessor(this);
    }

    @Override
    public void load(File file) throws IOException {
        decoder.load(file.getAbsolutePath());
    }

    @Override
    public void pause() {
        decoder.pause();
    }

    @Override
    public void resume() {
        decoder.resume();
    }

    @Override
    public void decode(SourceDataLine line) throws Throwable {
        this.line = line;
        meter = new AudioMeter(line.getFormat());
        decoder.decode();
    }

    @Override
    public void stop() throws IOException {
        decoder.stop();
    }

    @Override
    public AudioFormat getAudioFormat() {
        return decoder.getStreamInfo().getAudioFormat();
    }

    @Override
    public void onEnd(Callback callback) {
        decoder.onEnd(callback);
    }


    @Override
    public void processStreamInfo(StreamInfo streamInfo) {
        //not used
    }

    @Override
    public void processPCM(ByteData pcm) {
        /*
        if (line == null) {
            LOGGER.warn("NOOOOOOO LINE for " + decoder.getStreamInfo().getAudioFormat());
            interrupt();
            return;
        }

         */
        line.write(pcm.getData(), 0, pcm.getLen());
        //TODO meter.process(pcm.getData());
    }

    @Override
    public void addPCMProcessor(PCMProcessor pcmProcessor) {
        decoder.addPCMProcessor(pcmProcessor);
    }
}
