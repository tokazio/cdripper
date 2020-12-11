package fr.tokazio;

import com.adamdonegan.Discogs4J.models.Result;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/discogs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class DiscogsResource {

    @Inject
    DiscogsService service;

    @GET
    public List<Result> search(@QueryParam("artist") String artist, @QueryParam("title") String title, @QueryParam("year") String year) throws JsonProcessingException {
        return service.search(artist, title, year);
    }

    //http://127.0.0.1:8080/discogs/callback?oauth_token=rntZDwsiSmyllXShdmsLMrVBGdZdpNaXnVbrLFDw&oauth_verifier=NDNjJLSLuV
    @GET
    @Path("/callback")
    public void callback(@QueryParam("oauth_token") String token, @QueryParam("oauth_verifier") String verifier) {
        service.callback(token, verifier);
    }

}
