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

@Path("/ripping")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class RippingResource {

    @Inject
    RipperService ripperService;


    @Path("/rip")
    @GET
    public void rip() throws DiscIdException, RippingSessionException, CDDBException, RipException {
        new Thread() {

            public void run() {
                try {
                    ripperService.rip(null);
                } catch (RipException | CDDBException | DiscIdException | RippingSessionException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @GET
    public String status() {
        return ripperService.status();
    }
}
