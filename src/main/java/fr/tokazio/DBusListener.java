package fr.tokazio;

import fr.tokazio.ripper.RipperService;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.boncey.cdripper.CdInfoException;
import org.boncey.cdripper.RipException;
import org.freedesktop.dbus.DBusMatchRule;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.messages.DBusSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;

@Startup
@ApplicationScoped
public class DBusListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBusListener.class);

    @Inject
    private RipperService ripperService;

    public DBusListener() {
        try {
            DBusConnection conn = DBusConnection.getConnection(DBusConnection.DBusBusType.SYSTEM);

            conn.addGenericSigHandler(new DBusMatchRule("signal", "org.freedesktop.systemd1.Manager", "UnitNew"), new DBusSigHandler() {

                @Override
                public void handle(DBusSignal s) {
                    LOGGER.info("DBus signal sig: " + s.getSig() + ", name: " + s.getName() + ", path: " + s.getPath() + ", source: " + s.getSource());
                    LOGGER.info("A disc was inserted, ripping it...");
                    try {
                        ripperService.rip();
                    } catch (IOException | InterruptedException | RipException | CdInfoException e) {
                        LOGGER.error("Error ripping/encoding", e);
                    }
                }
            });
            LOGGER.info("Listening DBus...");
        } catch (DBusException e) {
            LOGGER.error("Error listening DBud", e);
        }
    }

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("DBus listener is starting...");
    }


    /*
    signal time=1606918824.451285 sender=:1.1 -> destination=(null destination) serial=1920 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
   string "dev-cdrom.device"
   object path "/org/freedesktop/systemd1/unit/dev_2dcdrom_2edevice"
     */

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("DBus listener is stopping...");
    }
}
