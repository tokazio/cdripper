package fr.tokazio.player;

import fr.tokazio.player.decoders.DsdDecoder;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;

public class Player {

    public static void main(String[] args) throws IOException, AudioFormatNotHandled, LineUnavailableException {

        new Devices().list();

        /*
        ImageIO.write( new WaveForm(file-> new FlacAudioFileReader().getAudioInputStream(file))
                .load(new File("encoded/Tryo - De bouches à oreilles - À l'Olympia/01 - Dans les nuages.flac"))
                .drawWave(640,240),"PNG", new File("01 - Dans les nuages.png"));

        ImageIO.write( new WaveForm(file-> new FlacAudioFileReader().getAudioInputStream(file))
                .load(new File("encoded/Tryo - De bouches à oreilles - À l'Olympia/02 - G8.flac"))
                .drawWave(640,240),"PNG", new File("02 - G8.png"));

        ImageIO.write( new WaveForm(file-> new FlacAudioFileReader().getAudioInputStream(file))
                .load(new File("2L-038_01_stereo_FLAC_44k_16b.flac"))
                .drawWave(640,240),"PNG", new File("2L-038_01_stereo_FLAC_44k_16b.png"));


         */

        /*
        AudioPlayer player = new AudioPlayer(2,new FlacDecoder())
                .volume(70)
                .load(new File("2L-038_01_stereo_FLAC_44k_16b.flac"))
                .play();

         */

        AudioPlayer player = new AudioPlayer(6, new DsdDecoder())
                .volume(70)
                .load(new File("2L-056_03_stereo_DSD64.dsf"))
                .play();

        /*

        AudioPlayer player = new AudioPlayer(2,new FlacDecoder())
                .volume(70)
                .load(new File("encoded/Tryo - De bouches à oreilles - À l'Olympia/01 - Dans les nuages.flac"))
                .play();

        Thread.sleep(2000);

        player.interrupt();

        player = new AudioPlayer(2,new FlacDecoder())
                .volume(70)
                .load(new File("encoded/Tryo - De bouches à oreilles - À l'Olympia/02 - G8.flac"))
                .play();

        Thread.sleep(2000);
        player.volume(60);


         */
    }
}
