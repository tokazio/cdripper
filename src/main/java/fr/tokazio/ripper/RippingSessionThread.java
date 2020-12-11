package fr.tokazio.ripper;

import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.discid.DiscIdException;
import org.boncey.cdripper.RipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RippingSessionThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(RippingSessionThread.class);

    private final RippingSession session;

    public RippingSessionThread(RippingSession session) {
        this.session = session;
    }

    public RippingSession currentSession() {
        return session;
    }

    @Override
    public void interrupt() {
        LOGGER.debug("Interrupting ripping session thread...");
        currentSession().abort();
        super.interrupt();
    }

    @Override
    public void run() {
        re();
    }

    public RippingStatus status() {
        return session.status();
    }

    public void re() {
        try {
            currentSession().run();
        } catch (DiscIdException | CDDBException | RipException | RippingSessionException e) {
            LOGGER.error("Error in ripping session #" + session.uuid(), e);
        }
    }
}
