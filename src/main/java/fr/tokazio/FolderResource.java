package fr.tokazio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/folders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class FolderResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerResource.class);

    @Inject
    FolderService service;

    @GET
    public List<Folder> state() {
        return service.all();
    }
}
