package fr.tokazio;

import fr.tokazio.filecollector.CollectorEngine;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

@ApplicationScoped
public class FolderService {

    public static final String ROOT = "/media/usb-drive/hifi";

    public List<Folder> all() {
        final List<File> files = new CollectorEngine().collect(ROOT);

        List<Folder> out = new LinkedList<>();
        for (File f : files) {
            out.add(new Folder(f));
        }

        return out;

    }
}
