package fr.tokazio.dbus;

import fr.tokazio.ripper.RipperService;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
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

    @Inject
    private RipperService ripperService;

    void onStart(@Observes StartupEvent ev) throws DBusException {
        LOGGER.info("DBus listener is starting...");
        final ProcessBuilder pb = new ProcessBuilder(" dbus-monitor --system");
        //pb.inheritIO();
        String result = "";
        try {
            final Process proc = pb.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println("DBus> " + line);
            }
            proc.waitFor();
            if (proc.exitValue() != 0) {
                throw new DBusException(result);
            }
        } catch (IOException | InterruptedException ex) {
            throw new DBusException(ex);
        }
    }
}
