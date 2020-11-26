package fr.tokazio;

import fr.tokazio.player.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/player")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PlayerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerResource.class);

    @Inject
    PlayerService service;

    @GET
    public PlayerStateData state() {
        return new PlayerStateData(service.getPlayingFileName(), service.getAudioFormat(), service.getState(), service.getVolume());
    }

    @Path("play")
    @POST
    public void play(PlayRequestData req) throws IOException {
        LOGGER.info("[REQ] play " + req.getFile());
        service.play(req.getFile());
    }

    @Path("stop")
    @POST
    public void stop() {
        LOGGER.info("[REQ] stop");
        service.stop();
    }

    @Path("volume")
    @POST
    public void volume(VolumeRequestData req) {
        LOGGER.info("[REQ] volume " + req.getVolume());
        service.volume(req.getVolume());
    }

}
