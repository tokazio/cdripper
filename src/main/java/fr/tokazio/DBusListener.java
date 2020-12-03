package fr.tokazio;

import fr.tokazio.dbus.Manager;
import fr.tokazio.ripper.RipperService;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.boncey.cdripper.RipException;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
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

    /*
    signal time=1607002991.464384 sender=:1.2 -> destination=(null destination) serial=385 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
   string "sys-devices-lm1-usb1-1\x2d1-1\x2d1.2-1\x2d1.2:1.0-host0-target0:0:0-0:0:0:0-block-sr0.device"
   object path "/org/freedesktop/systemd1/unit/sys_2ddevices_2dlm1_2dusb1_2d1_5cx2d1_2d1_5cx2d1_2e2_2d1_5cx2d1_2e2_3a1_2e0_2dhost0_2dtarget0_3a0_3a0_2d0_3a0_3a0_3a0_2dblock_2dsr0_2edevice"
signal time=1607002991.464490 sender=:1.2 -> destination=(null destination) serial=386 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
   string "dev-sr0.device"
   object path "/org/freedesktop/systemd1/unit/dev_2dsr0_2edevice"
signal time=1607002991.464543 sender=:1.2 -> destination=(null destination) serial=387 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
   string "dev-cdrw.device"
   object path "/org/freedesktop/systemd1/unit/dev_2dcdrw_2edevice"
signal time=1607002991.464591 sender=:1.2 -> destination=(null destination) serial=388 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
   string "dev-dvdrw.device"
   object path "/org/freedesktop/systemd1/unit/dev_2ddvdrw_2edevice"
signal time=1607002991.464641 sender=:1.2 -> destination=(null destination) serial=389 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
   string "dev-disk-by\x2did-usb\x2dTSSTcorp_DVD\x2b\x2dRW_TS\x2dT633A_302043333435363738394B4C\x2d0:0.device"
   object path "/org/freedesktop/systemd1/unit/dev_2ddisk_2dby_5cx2did_2dusb_5cx2dTSSTcorp_5fDVD_5cx2b_5cx2dRW_5fTS_5cx2dT633A_5f302043333435363738394B4C_5cx2d0_3a0_2edevice"
signal time=1607002991.464694 sender=:1.2 -> destination=(null destination) serial=390 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
   string "dev-dvd.device"
   object path "/org/freedesktop/systemd1/unit/dev_2ddvd_2edevice"
signal time=1607002991.464743 sender=:1.2 -> destination=(null destination) serial=391 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
   string "dev-cdrom.device"
   object path "/org/freedesktop/systemd1/unit/dev_2dcdrom_2edevice"

     */

    public DBusListener() {
        start();
    }

    public void start() {
        if (!OS.isUnix()) {
            LOGGER.warn("DBus not supported with this OS");
            return;
        }
        try {
            DBusConnection conn = DBusConnection.getConnection(DBusConnection.DBusBusType.SYSTEM);

            conn.addSigHandler(Manager.UnitNew.class, new DBusSigHandler<Manager.UnitNew>() {

                @Override
                public void handle(Manager.UnitNew s) {
                    LOGGER.info("DBus signal sig: " + s.getSig() + ", name: " + s.getName() + ", path: " + s.getPath() + ", source: " + s.getSource());
                    LOGGER.info("DBus signal unitName: " + s.getUnitName() + " (wanted: dev-sr0.device)");
                    if ("dev-sr0.device".equals(s.getUnitName())) {
                        LOGGER.info("A disc was inserted, ripping it...");
                        try {
                            ripperService.rip();
                        } catch (IOException | InterruptedException | RipException e) {
                            LOGGER.error("Error ripping/encoding", e);
                        }
                    }
                }
            });

            /*
            conn.addGenericSigHandler(new DBusMatchRule("signal", "org.freedesktop.systemd1.Manager", "UnitNew"), new DBusSigHandler() {

                @Override
                public void handle(DBusSignal s) {
                    LOGGER.info("DBus signal sig: " + s.getSig() + ", name: " + s.getName() + ", path: " + s.getPath() + ", source: " + s.getSource());
                    //TODO get string "dev-cdrom.device" from signal
                    LOGGER.info("A disc was inserted, ripping it...");
                    try {
                        ripperService.rip();
                    } catch (IOException | InterruptedException | RipException e) {
                        LOGGER.error("Error ripping/encoding", e);
                    }
                }
            });

             */
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
