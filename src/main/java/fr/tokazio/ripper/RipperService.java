package fr.tokazio.ripper;

import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.CddbData;
import fr.tokazio.cddb.discid.DiscId;
import fr.tokazio.cddb.discid.DiscIdData;
import fr.tokazio.cddb.discid.DiscIdException;
import fr.tokazio.events.CDinsertedEvent;
import org.boncey.cdripper.RipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

@ApplicationScoped
public class RipperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RipperService.class);

    @Inject
    RippingSessionFactory rippingSessionFactory;

    public DiscIdData discid() throws DiscIdException {
        return new DiscId().getDiscId();
    }

    public CddbData cddb(final DiscIdData discIdData) throws CDDBException {
        return new Cddb().getCddb(discIdData);
    }

    //    @ConsumeEvent(value = CDinsertedEvent.EVENT_NAME, blocking = true)
    public void resume(@ObservesAsync CDinsertedEvent event) throws RipException, CDDBException, DiscIdException, RippingSessionException {
        rippingSessionFactory.resume();
    }

    public RippingStatus status() {
        if (rippingSessionFactory.hasActiveSession()) {
            return rippingSessionFactory.status();
        }
        return new RippingStatus().setServiceState("NO_ACTIVE_SESSION");
    }

    public boolean isRipping() {
        return rippingSessionFactory.hasActiveSession();
    }

    public void abort() {
        LOGGER.debug("Aborting ripping session from service...");
        rippingSessionFactory.abort();
    }
}
