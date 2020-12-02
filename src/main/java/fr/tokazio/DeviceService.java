package fr.tokazio;

import fr.tokazio.player.AudioFormat;
import fr.tokazio.player.Device;

import javax.enterprise.context.ApplicationScoped;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DeviceService {


    public List<Device> devices() {
        List<Device> list = new ArrayList<>();
        int i = 0;
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            //todo complete infos from mixerInfo‹
            Device device = new Device(i++, mixerInfo.getName(), mixerInfo.getDescription());
            Mixer m = AudioSystem.getMixer(mixerInfo);
            for (Line.Info lineInfo : m.getSourceLineInfo()) {
                if (lineInfo instanceof DataLine.Info) {
                    DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;
                    javax.sound.sampled.AudioFormat[] formats = dataLineInfo.getFormats();
                    for (final javax.sound.sampled.AudioFormat format : formats) {
                        device.addFormat(new AudioFormat()
                                .encoding(format.getEncoding().toString())
                                .sampleRate(format.getSampleRate())
                                .sampleSizeInBits(format.getSampleSizeInBits())
                                .channels(format.getChannels())
                                .frameSize(format.getFrameSize())
                                .frameRate(format.getFrameRate())
                                .bigEndian(format.isBigEndian())
                        );
                    }
                }
            }
            list.add(device);
        }
        return list;
    }
}
