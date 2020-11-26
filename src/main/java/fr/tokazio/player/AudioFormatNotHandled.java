package fr.tokazio.player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Mixer;

public class AudioFormatNotHandled extends Exception {

    private final AudioFormat audioFormat;
    private final Mixer.Info mixerInfo;

    public AudioFormatNotHandled(AudioFormat audioFormat, Mixer.Info info, Exception ex) {
        super(ex);
        this.audioFormat = audioFormat;
        this.mixerInfo = info;
    }

    public AudioFormat audioFormat() {
        return audioFormat;
    }

    public Mixer.Info mixerInfo() {
        return mixerInfo;
    }

    public String getMessage() {
        return "The audio format " + audioFormat + " is not handled by " + mixerInfo.getName();
    }
}
