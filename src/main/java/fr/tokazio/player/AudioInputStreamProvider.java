package fr.tokazio.player;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public interface AudioInputStreamProvider {
    AudioInputStream provide(File file) throws IOException, UnsupportedAudioFileException;
}
