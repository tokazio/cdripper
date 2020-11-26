package fr.tokazio.player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rpetit
 */
public class Device {

    private int id;
    private String name;
    private String description;
    private List<AudioFormat> formats = new ArrayList<>();

    public Device() {
        //for json
    }

    public Device(final int id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void addFormat(final AudioFormat format) {
        this.formats.add(format);
    }

    public List<AudioFormat> getFormats() {
        return new ArrayList<>(formats);
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

}
