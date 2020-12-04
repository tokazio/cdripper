package fr.tokazio;

import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.discid.DiscIdException;
import fr.tokazio.ripper.RipperService;
import fr.tokazio.ripper.RippingSessionException;
import org.boncey.cdripper.RipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/folders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class FolderResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerResource.class);

    @Inject
    FolderService folderService;

    @Inject
    RipperService ripperService;

    @GET
    public List<Folder> all() {
        return folderService.all();
    }

    @Path("/eject")
    @GET
    public void eject() throws IOException {
        folderService.eject();
    }

    @Path("/rip")
    @GET
    public void rip() throws RipException, CDDBException, DiscIdException, RippingSessionException {
        ripperService.rip(null);
    }
}
