package fr.tokazio.ripper;

import fr.tokazio.cddb.CDDBException;
import fr.tokazio.cddb.discid.DiscId;
import fr.tokazio.cddb.discid.DiscIdData;
import fr.tokazio.cddb.discid.DiscIdException;
import org.boncey.cdripper.RipException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class RippingSessionFactory {

    private final Map<String, RippingSession> sessions = new HashMap<>();

    @Inject
    Instance<RippingSession> provider;

    public RippingSession resume() throws CDDBException, RippingSessionException, DiscIdException, RipException {
        //no session started
        if (sessions.isEmpty()) {
            return provideNew();
        }
        //find a session for the current discid
        final DiscIdData discIdData = getDiscId();
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
            return provideNew();
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
}
