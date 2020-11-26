package fr.tokazio;

import fr.tokazio.player.Device;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("devices")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class DeviceResource {

    @Inject
    private DeviceService service;

    @GET
    public List<Device> devices() {
        return service.devices();
    }

}
