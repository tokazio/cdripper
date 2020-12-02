package fr.tokazio;

import fr.tokazio.filecollector.CollectorEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@ApplicationScoped
public class FolderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FolderService.class);

    public static final String ROOT = "/media/usb-drive/hifi";

    public List<Folder> all() {
        final List<File> files = new CollectorEngine().collect(ROOT);

        List<Folder> out = new LinkedList<>();
        for (File f : files) {
            out.add(new Folder(f));
        }

        return out;

    }

    public void eject() throws InterruptedException, IOException {
        Runtime rt = Runtime.getRuntime();
        LOGGER.info("Ejecting: eject");
        Process proc = rt.exec("eject", null);
        proc.waitFor();
    }
}
