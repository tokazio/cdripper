package fr.tokazio;

import fr.tokazio.player.AudioFormat;
import fr.tokazio.player.VolumeInfo;
import org.jflac.PlayerState;

public class PlayerStateData {

    private final String file;
    private final String state;
    private final VolumeInfo volume;
    private final AudioFormat audioFormat;

    public PlayerStateData(String playingFileName, AudioFormat audioFormat, PlayerState state, VolumeInfo volume) {
        this.state = state.name();
        this.audioFormat = audioFormat;
        this.file = playingFileName;
        this.volume = volume;
    }

    public String getFile() {
        return file;
    }

    public String getState() {
        return state;
    }

    public VolumeInfo getVolume() {
        return volume;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }
}
