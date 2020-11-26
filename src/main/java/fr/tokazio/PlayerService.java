package fr.tokazio;

import fr.tokazio.player.AudioFormat;
import fr.tokazio.player.AudioPlayer;
import fr.tokazio.player.Decoder;
import fr.tokazio.player.VolumeInfo;
import fr.tokazio.player.decoders.DsdDecoder;
import fr.tokazio.player.decoders.FlacDecoder;
import org.jflac.PlayerState;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class PlayerService {

    private AudioPlayer player;

    public void play(final String filename) throws IOException {
        stop();
        player = new AudioPlayer(6, getDecoder(filename))
                .load(new File(filename))
                .play();
    }

    private Decoder getDecoder(final String filename) throws IOException {
        if (filename.endsWith(".flac")) {
            return new FlacDecoder();
        }
        if (filename.endsWith(".dsf")) {
            return new DsdDecoder();
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
