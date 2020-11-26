package fr.tokazio.player;

import org.jflac.PlayerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;

import static org.jflac.PlayerState.*;

/**
 * @author rpetit
 */
public class AudioPlayer extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioPlayer.class);
    javax.sound.sampled.AudioFormat fmt;
    private int MIXERID = 2;//todo from external prop file
    private SourceDataLine line;
    private String filename;
    private Callback onEnd;
    private Decoder decoder;
    private PlayerState state = PlayerState.STOPPED;
    private int vol = 100;

    public AudioPlayer(int mixerId, Decoder decoder) {
        this.MIXERID = mixerId;
        this.decoder = decoder;
    }

    /*
    public void unpause(){
        LOGGER.info("[BEGIN] play: state is "+state);
        if(state!=PAUSED){
            throw new RuntimeException("Can't resume because not paused");
        }
        state = PLAYING;
        onPlay();
        LOGGER.info("[END] play: state is "+state);
    }

     */

    public void pauseORresume() {
        LOGGER.info("[BEGIN] pauseORresume: state is " + state);
        switch (state) {
            case PLAYING:
                state = PAUSED;
                decoder.pause();
                //onPause();
                break;
            case PAUSED:
                state = PLAYING;
                decoder.resume();
                //onResume();
                break;
            default:
                throw new RuntimeException("Can't pause nor resume because not playing nor paused");
        }
        LOGGER.info("[END] pauseORresume: state is " + state);
    }

    public AudioPlayer play() throws AudioFormatNotHandled {
        LOGGER.info("[BEGIN] play '" + filename + "': state is " + state);
        if (!state.equals(STOPPED)) {
            throw new RuntimeException("Allready playing?!");
        }

        try {
            fmt = decoder.getAudioFormat();
            LOGGER.info("Getting audio line on mixer #" + MIXERID + " (" + AudioSystem.getMixerInfo()[MIXERID].getName() + ") for " + fmt + "...");
            line = AudioSystem.getSourceDataLine(fmt, AudioSystem.getMixerInfo()[MIXERID]);
            // try {
            line.open(fmt);

            doVol();
            line.start();
            //  } catch (LineUnavailableException ex) {
            // LOGGER.error("Error opening line for " + fmt + " on mixer " + AudioSystem.getMixerInfo()[MIXERID], ex);
            //  }
            decoder.onEnd(onEnd);
            state = PlayerState.PLAYING;
            //onPlay();
            LOGGER.info("Line " + line.getLineInfo() + " is ready, go to play " + filename);

            start();
            //return filename + " " + fmt.toString();
        } catch (LineUnavailableException | IllegalArgumentException ex) {
            throw new AudioFormatNotHandled(fmt, AudioSystem.getMixerInfo()[MIXERID], ex);
        }
        return this;
    }


    public AudioPlayer onEnd(final Callback cb) {
        LOGGER.info("ended '" + filename() + "'");
        this.onEnd = cb;
        return this;
    }

    public String filename() {
        return filename;
    }

    @Override
    public void run() {
        try {
            decoder.decode(line);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        terminate();
    }

    private void terminate() {
        LOGGER.info("terminate: state is " + state);
        state = STOPPED;
        fmt = null;
        if (decoder != null) {
            try {
                decoder.stop();
                LOGGER.info("decoder stopped");
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
        if (line != null) {
            line.drain();
            line.close();
            LOGGER.info("line closed");
        }
        //onStop();
    }

    @Override
    public void interrupt() {
        LOGGER.info("interrupt: state is " + state);
        terminate();
        super.interrupt();
    }

    /**
     * @param vol 0 to 100%
     */
    public AudioPlayer volume(int vol) {
        if (vol < 0) {
            vol = 0;
        }
        if (vol > 100) {
            vol = 100;
        }
        this.vol = vol;
        if (line != null) {
            doVol();
        }
        return this;
    }

    private void doVol() {
        if (line != null) {
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            final float range = Math.abs(gainControl.getMinimum()) + Math.abs(gainControl.getMaximum());
            final float f = (range * (this.vol / 100f)) - Math.abs(gainControl.getMinimum());
            gainControl.setValue(f);
            LOGGER.info("Volume set to " + this.vol + "% (min=" + volume().getMin() + ", max=" + volume().getMax() + ", actual=" + volume().getVal() + " " + volume().percent() + "%)");
        } else {
            LOGGER.warn("No line to set volume on");
        }
    }

    public VolumeInfo volume() {
        if (line != null) {
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            return new VolumeInfo(gainControl.getMinimum(), gainControl.getValue(), gainControl.getMaximum());
        }
        return new VolumeInfo();
    }

    public boolean isStopped() {
        return this.state.equals(STOPPED);
    }

    public boolean isPaused() {
        return this.state.equals(PAUSED);
    }

    public boolean isPlaying() {
        return this.state.equals(PLAYING);
    }

    public AudioPlayer load(File f) throws IOException {
        this.filename = f.getAbsolutePath();
        decoder.load(f);
        return this;
    }

    public PlayerState getPlayerState() {
        return state;
    }

    public AudioFormat getAudioFormat() {
        return new AudioFormat()
                .encoding(fmt.getEncoding().toString())
                .sampleRate(fmt.getSampleRate())
                .sampleSizeInBits(fmt.getSampleSizeInBits())
                .channels(fmt.getChannels())
                .frameSize(fmt.getFrameSize())
                .frameRate(fmt.getFrameRate())
                .bigEndian(fmt.isBigEndian());
    }
}
