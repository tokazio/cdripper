package fr.tokazio.dbus;

import fr.tokazio.OS;
import fr.tokazio.events.CDinsertedEvent;
import io.quarkus.runtime.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * @link https://dbus.freedesktop.org/doc/dbus-monitor.1.html
 */
@Startup
@ApplicationScoped
public class DBusListener2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBusListener.class);

    //@Inject
    //EventBus bus;

    //@Inject
    //Event<CDinsertedEvent> event;

    //  @Inject
    //  RipperService ripperService;

    DBusListener2() throws DBusException {
        onStart();
    }

    void onStart() throws DBusException {//@Observes StartupEvent ev) throws DBusException {
        LOGGER.info("DBus system listener (2) is starting...");
        if (!OS.isUnix()) {
            LOGGER.warn("DBus not supported with this OS");
            return;
        }
        final ProcessBuilder pb = new ProcessBuilder("dbus-monitor", "--system");
        //pb.inheritIO();

        try {
            final Process proc = pb.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            //  new Thread() {

            //    @Override
            //    public void run() {
            boolean getNext = false;
            while (proc.isAlive()) {
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        //LOGGER.debug("DBus> " + line);
                        if (line.trim().startsWith("signal")) {
                            LOGGER.debug("New Signal detected: '" + line.trim() + "'");
                            if (line.contains("interface=org.freedesktop.systemd1.Manager; member=UnitNew")) {
                                LOGGER.debug("New unit detected");
                                        getNext = true;
                                        break;
                                    }
                                }
                                if (getNext) {
                                    LOGGER.debug("New unit detected, wich ? '" + line.trim() + "'");
                                    getNext = false;
                                    if (line.trim().startsWith("string") && line.contains("dev-sr0.device")) {
                                        LOGGER.debug("dev-sr0.device detected >>>>>> handle");
                                        //handle();
                                        CDinsertedEvent message = new CDinsertedEvent();
                                        //event.fireAsync(message);
                                        //bus.publish(CDinsertedEvent.EVENT_NAME, message);

                                    }
                                }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            //  }
            //}.start();

            proc.waitFor();
        } catch (IOException | InterruptedException ex) {
            throw new DBusException(ex);
        }
    }

    /*
    private void handle() {
        LOGGER.info("A disc was inserted, ripping it...");
        try {
            ripperService.rip();
        } catch (IOException | InterruptedException | RipException e) {
            LOGGER.error("Error ripping/encoding", e);
        }
    }

     */
}

/*
signal time=1607002991.464384 sender=:1.2 -> destination=(null destination) serial=385 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
string "sys-devices-lm1-usb1-1\x2d1-1\x2d1.2-1\x2d1.2:1.0-host0-target0:0:0-0:0:0:0-block-sr0.device"
object path "/org/freedesktop/systemd1/unit/sys_2ddevices_2dlm1_2dusb1_2d1_5cx2d1_2d1_5cx2d1_2e2_2d1_5cx2d1_2e2_3a1_2e0_2dhost0_2dtarget0_3a0_3a0_2d0_3a0_3a0_3a0_2dblock_2dsr0_2edevice"

signal time=1607002991.464490 sender=:1.2 -> destination=(null destination) serial=386 path=/org/freedesktop/systemd1; interface=org.freedesktop.systemd1.Manager; member=UnitNew
string "dev-sr0.device"1
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