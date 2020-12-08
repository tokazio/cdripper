package fr.tokazio.ripper;

import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.discid.DiscId;
import fr.tokazio.cddb.discid.DiscIdData;
import fr.tokazio.cddb.discid.DiscIdException;
import org.boncey.cdripper.RipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class RippingSessionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RippingSessionFactory.class);

    private final Map<String, RippingSession> sessions = new HashMap<>();

    private RippingSession currentSession;

    @Inject
    Instance<RippingSession> provider;

    public RippingSession resume() throws CDDBException, RippingSessionException, DiscIdException, RipException {
        this.currentSession = doResume();
        return this.currentSession;
    }

    private RippingSession doResume() throws CDDBException, RippingSessionException, DiscIdException, RipException {
        if (currentSession != null) {
            LOGGER.debug("Resuming session " + currentSession.uuid());
            currentSession.run();
            return currentSession;
        }
        //no session started
        if (sessions.isEmpty()) {
            RippingSession session = provideNew();
            LOGGER.debug("No session registered, starting new session " + session.uuid());
            return session;
        }
        //find a session for the current discid
        final DiscIdData discIdData = getDiscId();
        LOGGER.debug(sessions.size() + " session(s) are registered, searching one for disc id " + discIdData.getDiscId() + "...");
        RippingSession existing = null;
        for (Map.Entry<String, RippingSession> e : sessions.entrySet()) {
            if (discIdData.getDiscId().equals(e.getValue().discId())) {
                if (existing == null) {
                    existing = e.getValue();//1 session
                } else {
                    //multi session, chose one ?
                    throw new RippingSessionException("Multiple ripping sessions exists for the discid " + discIdData.getDiscId() + ", wich one to choose ?!");
                }
            }
        }
        if (existing == null) {
            RippingSession session = provideNew();
            LOGGER.debug("No session found for disc id " + discIdData.getDiscId() + ", starting session " + session.uuid());
            return session;
        } else {
            LOGGER.debug("One session found for disc id " + discIdData.getDiscId() + ", resuming session " + existing.uuid());
        }
        existing.run();
        return existing;
    }

    private RippingSession provideNew() throws RippingSessionException, DiscIdException, CDDBException, RipException {
        RippingSession session = provider.get();
        sessions.put(session.uuid(), session);
        session.start();
        return session;
    }

    private DiscIdData getDiscId() throws DiscIdException {
        return new DiscId().getDiscId();
    }

    public RippingSession currentSession() {
        return currentSession;
    }
}
