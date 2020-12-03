package fr.tokazio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/discogs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class DiscogsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscogsResource.class);

    @Inject
    DiscogsService service;

    @GET
    public String search(@QueryParam("query") String query) {
        return service.search(query);
    }

    //http://127.0.0.1:8080/discogs/callback?oauth_token=rntZDwsiSmyllXShdmsLMrVBGdZdpNaXnVbrLFDw&oauth_verifier=NDNjJLSLuV
    @GET
    @Path("/callback")
    public void callback(@QueryParam("oauth_token") String token, @QueryParam("oauth_verifier") String verifier) {
        service.callback(token, verifier);
    }

}
