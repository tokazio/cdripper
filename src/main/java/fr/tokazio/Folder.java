package fr.tokazio;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class Folder {

    @JsonProperty
    private final String fullPath;

    public Folder(File f) {
        this.fullPath = f.getAbsolutePath();
    }
}
