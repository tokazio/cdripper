package fr.tokazio.ripper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tokazio.DiscogsService;
import fr.tokazio.OS;
import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.CddbData;
import fr.tokazio.cddb.discid.DiscId;
import fr.tokazio.cddb.discid.DiscIdData;
import fr.tokazio.cddb.discid.DiscIdException;
import org.boncey.cdripper.*;
import org.boncey.cdripper.encoder.Encoder;
import org.boncey.cdripper.encoder.FlacEncoder;
import org.boncey.cdripper.model.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@ApplicationScoped
public class RippingSession implements Serializable {

    @JsonIgnore
    private static final Logger LOGGER = LoggerFactory.getLogger(RippingSession.class);
    @JsonIgnore
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    @JsonProperty
    private final String uuid = UUID.randomUUID().toString();
    @JsonIgnore
    private final Encoded monitor = new FileDeletingTrackMonitor();
    //@Inject
    //EventBus bus;
    @Inject
    DiscogsService discogsService;
    @JsonProperty
    private DiscIdData discIdData;
    @JsonProperty
    private CddbData cddbData;
    @JsonIgnore
    private CDRipper ripper;
    @JsonProperty
    private State state = State.NEW;

    public void start() throws RippingSessionException, DiscIdException, CDDBException, RipException {
        if (!state.equals(State.NEW)) {
            throw new RippingSessionException("Ripping session already started: " + state.name());
        }
        state = State.STARTED;
        LOGGER.info("Started ripping session #" + uuid);
        run();
    }

    public State state() {
        return state;
    }

    public int progress() {
        if (state.equals(State.RIPPING_STARTED)) {
            return ripper.progress();
        }
        return -1;
    }

    public void run() throws DiscIdException, CDDBException, RipException, RippingSessionException {
        switch (state) {
            case STARTED:
                findDiscId();
                break;
            case DISCIID:
                findCddb();
                break;
            case CDDB:
                rip();
                getAlbumMetas();
                break;
            case RIPPING_STARTED:
                //ripping in progress
                break;
            case TERMINATED:
                LOGGER.info("Ripping session #" + uuid + " for discid " + discId() + " is terminated");
                break;
            case UNDEFINED:
            default:
                throw new RippingSessionException("The ripping session #" + uuid + " is in an undefined state!");
        }
    }

    public String discId() {
        return this.discIdData.getDiscId();
    }

    public String uuid() {
        return uuid;
    }

    private void findDiscId() throws DiscIdException, CDDBException, RippingSessionException, RipException {
        this.state = State.DISCIID;
        this.discIdData = new DiscId().getDiscId();
        LOGGER.info("Ripping session #" + uuid + ": discid is " + discId());
        run();
    }

    private void findCddb() throws CDDBException, RipException, DiscIdException, RippingSessionException {
        this.state = State.CDDB;
        this.cddbData = new Cddb().getCddb(this.discIdData);
        LOGGER.info("Ripping session #" + uuid + ": " + cddbData.getAlbum() + " by " + cddbData.getArtist());
        run();
    }

    private void rip() throws RipException {
        this.state = State.RIPPING_STARTED;
        final File baseDir = new File("/audio");

        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        ripper = provideRipper(baseDir)
                .setTrackRippedListener(file -> {
                    //bus.publish("ripping-track-end", file);
                    File dest = new File("encoded");
                    LOGGER.debug("Flac encoder to " + dest.getAbsolutePath());
                    final Encoder encoder = new FlacEncoder(monitor, dest);
                    LOGGER.debug("Preparing encoding for " + file.getAbsolutePath() + " (baseDir: " + baseDir.getAbsolutePath() + ")");
                    final Track track = Track.createTrack(file, baseDir, "flac");
                    encoder.queue(track, false);
                    LOGGER.debug("Will encode " + file.getName() + " to FLAC...");
                    executor.execute(encoder);
                })
                .setEndListener(new Function() {
                    @Override
                    public Object apply(Object o) {
                        //bus.publish("ripping-disc-end", this);
                        return null;
                    }
                })
                .start(this.uuid, this.cddbData);
        //bus.publish("ripping-disc-start", this);
    }

    private void getTrackMetas(final String artist, String title) {
        LOGGER.info(discogsService.search(artist + " " + title));
    }

    private void getAlbumMetas() {
        LOGGER.info(discogsService.search(this.cddbData.getArtist() + " " + this.cddbData.getAlbum()));
        for (CddbData.Track track : this.cddbData.getTracks()) {
            getTrackMetas(track.getArtist(), track.getTitle());
        }
    }

    private CDRipper provideRipper(final File baseDir) {
        if (OS.isUnix()) {
            return new LinuxCDRipper(baseDir, Collections.emptyList());
        }
        if (OS.isMac()) {
            return new MacOSCDRipper(baseDir, Collections.emptyList());
        }
        throw new UnsupportedOperationException("OS not supported");
    }

    enum State {
        UNDEFINED, NEW, STARTED, DISCIID, CDDB, RIPPING_STARTED, TERMINATED;
    }


}
