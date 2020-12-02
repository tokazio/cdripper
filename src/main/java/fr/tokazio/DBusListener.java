package fr.tokazio;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
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

@Startup
@ApplicationScoped
public class DBusListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBusListener.class);

    public DBusListener() {
        try {
            DBusConnection conn = DBusConnection.getConnection(DBusConnection.DBusBusType.SYSTEM);
            conn.addSigHandler(new DBusMatchRule((String) null, "org.freedesktop.systemd1.Manager", "UnitNew"), new DBusSigHandler() {

                @Override
                public void handle(DBusSignal s) {
                    LOGGER.info("DBus signal: " + s);
                }
            });
            LOGGER.info("Listening DBus...");
        } catch (DBusException e) {
            LOGGER.error("Error listening DBud", e);
        }
    }

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");
    }


    /*
    signal time=1606918824.451285 sender=:1.1 -> destination=(null destination) serial=1920 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
   string "dev-cdrom.device"
   object path "/org/freedesktop/systemd1/unit/dev_2dcdrom_2edevice"
     */

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }
}
