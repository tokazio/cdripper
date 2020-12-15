package fr.tokazio.ripper;

import com.adamdonegan.Discogs4J.models.Result;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tokazio.DiscogsService;
import fr.tokazio.OS;
import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.CddbData;
import fr.tokazio.cddb.discid.DiscId;
import fr.tokazio.cddb.discid.DiscIdData;
import fr.tokazio.cddb.discid.DiscIdException;
import fr.tokazio.events.WebsocketEvent;
import io.vertx.core.eventbus.EventBus;
import org.boncey.cdripper.CDRipper;
import org.boncey.cdripper.LinuxCDRipper;
import org.boncey.cdripper.MacOSCDRipper;
import org.boncey.cdripper.RipException;
import org.boncey.cdripper.encoder.Encoder;
import org.boncey.cdripper.encoder.FlacEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class RippingSession implements Serializable {

    @JsonIgnore
    private static final Logger LOGGER = LoggerFactory.getLogger(RippingSession.class);

    @JsonIgnore
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Inject
    EventBus bus;

    @JsonProperty
    private final String uuid = UUID.randomUUID().toString();

    @Inject
    DiscogsService discogsService;

    @JsonIgnore
    private CDRipper ripper;

    @JsonIgnore
    private Thread rippingThread;

    @JsonIgnore
    private File rippingDir;

    @JsonProperty
    private DiscIdData discIdData;

    @JsonProperty
    private CddbData cddbData;

    @JsonProperty
    private State state = State.NEW;


    public void start() throws RippingSessionException, DiscIdException, CDDBException, RipException {
        if (!state.equals(State.NEW)) {
            throw new RippingSessionException("Ripping session already started: " + state.name());
        }
        state = State.STARTED;
        LOGGER.info("Started ripping session #" + uuid);
        bus.publish("websocket", new WebsocketEvent("Started ripping session #" + uuid));
        run();
    }

    public State state() {
        return state;
    }

    public void run() throws DiscIdException, RipException, RippingSessionException {
        switch (state) {
            case STARTED:
                findDiscId();
                break;
            case DISCIID:
                findCddb();
                break;
            case CDDB:
                ripAsync();
                try {
                    getAlbumMetas();
                } catch (IOException e) {
                    LOGGER.error("Error getting album meta data", e);
                }
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

    private void findDiscId() throws DiscIdException, RippingSessionException, RipException {
        this.state = State.DISCIID;
        this.discIdData = new DiscId().getDiscId();
        LOGGER.info("Ripping session #" + uuid + ": discid is " + discId());
        run();
    }

    private void findCddb() throws RipException, DiscIdException, RippingSessionException {
        this.state = State.CDDB;
        try {
            this.cddbData = new Cddb().getCddb(this.discIdData);
            LOGGER.info("Ripping session #" + uuid + ": " + cddbData.getAlbum() + " by " + cddbData.getArtist());
        } catch (CDDBException ex) {
            this.cddbData = new CddbData(discIdData.getNbTracks());
            LOGGER.error("Error getting cddb infos", ex);
        }
        run();
    }

    private void ripAsync() {
        if (rippingThread == null) {
            rippingThread = new Thread() {

                @Override
                public void run() {
                    try {
                        rip();
                    } catch (RipException e) {
                        LOGGER.error("Error ripping async", e);
                    }
                }

            };
            rippingThread.start();
        }
    }

    private void rip() throws RipException {
        this.state = State.RIPPING_STARTED;

        if (OS.isMac()) {
            rippingDir = new File("/users/romain/audio");
        } else {
            rippingDir = new File("/audio");
        }

        if (!rippingDir.exists()) {
            LOGGER.debug("Creating ripping dir " + rippingDir.getAbsolutePath());
            if (!rippingDir.mkdirs()) {
                LOGGER.error("Error creating ripping dir " + rippingDir.getAbsolutePath());
            }
        }

        ripper = provideRipper(rippingDir)
                .setProgressListener((status) -> {
                    bus.publish("websocket", new WebsocketEvent("rippingProgress::" + status.asJson()));
                })
                .setStartedListener(status -> {
                    bus.publish("websocket", new WebsocketEvent("Ripping " + status.getTrackArtist() + " - " + status.getTrackTitle() + " started"));
                })
                .setTrackRippedListener((discData, trackData, file) -> {
                    bus.publish("websocket", new WebsocketEvent("Ripping " + file.getAbsolutePath() + " ended"));
                    LOGGER.debug("Flac encoder for " + file.getAbsolutePath() + " to " + rippingDir.getAbsolutePath());
                    final Encoder encoder = new FlacEncoder(discData, trackData, file, RipperUtils.tidyFilename(new File(rippingDir, discData.getArtist() + "-" + discData.getAlbum())));
                    executor.execute(encoder);
                })
                .setDiscRippedListener(() -> bus.publish("websocket", new WebsocketEvent("Ripping disc ended")));
        LOGGER.info("Ripping with " + ripper.getClass().getName());
        ripper.start(this.discIdData, this.cddbData);
        bus.publish("websocket", new WebsocketEvent("Ripping disc started"));
    }

    private void getTrackMetas(final String artist, final String title, final String year) throws IOException {
        if (this.cddbData.isEmpty()) {
            //no CD data to find cover on discogs
        } else {
            final List<Result> results = discogsService.search(artist, title, year);
            if (results != null) {
                LOGGER.info(results.toString());
                File coverDir = new File(rippingDir, "/covers");
                if (!coverDir.exists()) {
                    coverDir.mkdirs();
                }
                if (!results.isEmpty()) {
                    imageFromUrl(results.get(0).getCover_image(), coverDir.getAbsolutePath() + "/" + artist + "-" + title + ".jpg");
                }
            }
        }
    }

    private String urlEncode(String str) {
        /*
        try {
            return URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error encoding url",e);
        }
         */
        return str;//str.replaceAll("\\(", "%28").replaceAll("\\)", "%29");
    }

    private void getAlbumMetas() throws IOException {
        if (this.cddbData.isEmpty()) {
            //no CD data to find cover on discogs
        } else {
            List<Result> results = discogsService.search(this.cddbData.getArtist(), this.cddbData.getAlbum(), this.cddbData.getYear());
            if (results != null) {
                LOGGER.info(results.toString());
                File coverDir = new File(rippingDir, "/covers");
                if (!coverDir.exists()) {
                    coverDir.mkdirs();
                }
                imageFromUrl(results.get(0).getCover_image(), coverDir.getAbsolutePath() + "/" + this.cddbData.getArtist() + "-" + this.cddbData.getAlbum() + ".jpg");
            }
            for (CddbData.Track track : this.cddbData.getTracks()) {
                getTrackMetas(track.getArtist(), track.getTitle(), this.cddbData.getYear());
            }
        }
    }

    private void imageFromUrl(String urlStr, String destinationFile) throws IOException {
        URL url = new URL(urlStr);
        try (InputStream is = url.openStream()) {
            try (OutputStream os = new FileOutputStream(destinationFile)) {
                byte[] b = new byte[2048];
                int length;
                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }
            }
        }
    }

    private CDRipper provideRipper(final File rippingDir) {
        if (OS.isUnix()) {
            return new LinuxCDRipper(rippingDir);
        }
        if (OS.isMac()) {
            return new MacOSCDRipper(rippingDir);
        }
        throw new UnsupportedOperationException("OS not supported");
    }

    public RippingStatus status() {
        if (ripper != null) {
            return ripper.status().setServiceState(state.name());
        }
        return new RippingStatus().setServiceState(state.name());
    }

    public boolean isActive() {
        return !State.TERMINATED.equals(state);
    }

    public void abort() {
        LOGGER.debug("Aborting ripping session #" + uuid + "...");
        ripper.stop();
    }

    public enum State {
        UNDEFINED, NEW, STARTED, DISCIID, CDDB, RIPPING_STARTED, TERMINATED;
    }


}
