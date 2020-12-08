package fr.tokazio;

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

    @Inject
    FolderService folderService;

    @GET
    public List<Folder> all() {
        return folderService.all();
    }

    @Path("/eject")
    @GET
    public void eject() throws IOException {
        folderService.eject();
    }


}
