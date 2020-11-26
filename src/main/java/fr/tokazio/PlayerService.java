package fr.tokazio;

import fr.tokazio.player.*;
import fr.tokazio.player.decoders.DsdOverPcmDecoder;
import fr.tokazio.player.decoders.FlacDecoder;
import fr.tokazio.player.decoders.WaveDecoder;
import org.jflac.PlayerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@ApplicationScoped
public class PlayerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    private AudioPlayer player;

    public void play(final String filename) throws IOException, AudioFormatNotHandled {
        stop();
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + file.getAbsolutePath() + " not found");
        }
        player = new AudioPlayer(2, getDecoder(file.getName()))
                .load(file)
                .play();
    }

    private Decoder getDecoder(final String filename) throws IOException {
        if (filename.endsWith(".flac")) {
            LOGGER.info("Using FLAC decoder");
            return new FlacDecoder();
        }
        if (filename.endsWith(".dsf")) {
            LOGGER.info("Using DSD decoder");
            return new DsdOverPcmDecoder();
        }
        if (filename.endsWith(".wav")) {
            LOGGER.info("Using WAVE decoder");
            return new WaveDecoder();
        }
        throw new IOException("Can't handle " + filename + " format (only flac or dsf)");
    }

    public void stop() {
        if (player != null) {
            player.interrupt();
        }
    }

    public void volume(int volume) {
        if (player != null) {
            player.volume(volume);
        }
    }

    public PlayerState getState() {
        if (player != null) {
            return player.getPlayerState();
        }
        return PlayerState.STOPPED;
    }

    public String getPlayingFileName() {
        if (player != null) {
            return player.filename();
        }
        return "";
    }

    public VolumeInfo getVolume() {
        if (player != null) {
            return player.volume();
        }
        return new VolumeInfo();
    }

    public AudioFormat getAudioFormat() {
        return player.getAudioFormat();
    }
}
