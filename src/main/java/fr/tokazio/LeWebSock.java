package fr.tokazio;

import fr.tokazio.events.WebsocketEvent;
import io.quarkus.vertx.ConsumeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint("/websock")
@ApplicationScoped
public class LeWebSock {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeWebSock.class);

    private final List<Session> sessions = new CopyOnWriteArrayList<>();

    @OnOpen
    public void onOpen(final Session session) {
        sessions.add(session);
        //broadcast(JOINED + CMD_SEPARATOR + "L'utilisateur " + username + " nous à rejoint");
        LOGGER.info("[WEBSOCKET] un utilisateur s'est connecté");
    }

    @OnClose
    public void onClose(final Session session) {
        sessions.remove(session);
        //broadcast(LEFT + CMD_SEPARATOR + "L'utilisateur " + username + " nous à quitté");
        LOGGER.info("[WEBSOCKET] un utilisateur s'est DEconnecté");
    }

    @OnError
    public void onError(final Session session, final Throwable throwable) {
        sessions.remove(session);
        //broadcast(LEFT + CMD_SEPARATOR + "L'utilisateur " + username + " à eu un problème: " + throwable);
        LOGGER.error("[WEBSOCKET] error", throwable);
    }

    @OnMessage
    public void onMessage(final String message) {
        //broadcast(MESSAGE + CMD_SEPARATOR + username + ": " + message);
        LOGGER.info("[WEBSOCKET] " + message);
    }

    @ConsumeEvent(value = "websocket", blocking = true)
    void message(@ObservesAsync WebsocketEvent message) {
        broadcast(message.getMessage());
    }

    private void broadcast(final String message) {
        sessions.forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    LOGGER.error("Unable to send message: " + result.getException());
                }
            });
        });
    }

}