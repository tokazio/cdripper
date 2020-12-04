package fr.tokazio;

import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.discid.DiscIdException;
import fr.tokazio.ripper.RipperService;
import fr.tokazio.ripper.RippingSessionException;
import org.boncey.cdripper.RipException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("/folders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class FolderResource {

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
    public CompletionStage<Void> rip() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.thenRunAsync(() -> {
            try {
                ripperService.rip(null);
            } catch (RipException | CDDBException | DiscIdException | RippingSessionException e) {
                e.printStackTrace();
            }
        });
        return future;
    }
}
