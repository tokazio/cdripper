package fr.tokazio;

import com.adamdonegan.Discogs4J.client.DiscogsClient;
import com.adamdonegan.Discogs4J.models.PaginatedResult;
import com.adamdonegan.Discogs4J.models.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.tokazio.events.WebsocketEvent;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * https://github.com/ajdons/discogs4j
 */
@ApplicationScoped
public class DiscogsService {

    public static final String DISCOGS_CRED = "discogs.cred";
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscogsService.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.67 Safari/537.36";
    private static final String CONSUMER_KEY = "qrwjUeBmOaGjweCplcaj";
    private static final String CONSUMER_SECRET = "OUIgMEYWsgeHvuThZKMZVlAHHASGDpIM";

    @Inject
    EventBus bus;

    @Inject
    ObjectMapper mapper;// = new ObjectMapper();

    private DiscogsClient client;

    private DiscogsClient client() {
        if (client != null) {
            return client;
        }
        connect();
        return client;
    }

    private void connect() {
        //new JSONModuleCustomizer().customize(mapper);
        final File credsFile = new File(DISCOGS_CRED);
        if (credsFile.exists()) {
            try {
                LOGGER.info("Connecting to Discogs with existing credentials...");
                DiscogsCred creds = mapper.readValue(credsFile, DiscogsCred.class);
                client = new DiscogsClient(CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, creds.getOauth_token(), creds.getOauth_token_secret());
                return;
            } catch (IOException e) {
                LOGGER.error("Error reading Discogs credentials");
            }
        }
        LOGGER.info("Connecting to Discogs (fresh)...");

        client = new DiscogsClient(USER_AGENT);
        client.setConsumerKey(CONSUMER_KEY);
        client.setConsumerSecret(CONSUMER_SECRET);
        client.setCallbackUrl(getCallbackUrl());

        client.getRequestToken();

        final String url = client.getAuthorizationURL();
        //TODO automatiser
        LOGGER.warn("!!!!!!!!!!!!!!!!\nCliquez ici pour autoriser: " + url + "\n!!!!!!!!!!!!!!!!");
        bus.publish("websocket", new WebsocketEvent("discogsAuthRequired::" + url));
    }


    static String getCallbackUrl() {
        String url = "http://127.0.0.1:8080/discogs/callback";
        try {
            InetAddress adr = InetAddress.getLocalHost();
            url = "http://" + adr.getHostAddress() + ":8080/discogs/callback";
        } catch (UnknownHostException ex) {
            LOGGER.warn("Can't get the ip address", ex);
        }
        LOGGER.debug("Discogs callback url is " + url);
        return url;
    }

    public void callback(String token, String verifier) {
        LOGGER.debug("Discogs callback: " + token + " | " + verifier);
        client().getAccessToken(verifier);
        String oauth_token = client().getOauthToken();
        String oauth_token_secret = client().getOauthTokenSecret();

        try {
            mapper.writeValue(new File(DISCOGS_CRED), new DiscogsCred(oauth_token, oauth_token_secret));
        } catch (IOException e) {
            LOGGER.error("Error saving discogs credentials");
        }


        //TODO save it
    }

    public List<Result> search(String artist, String title, String year) throws JsonProcessingException {
        String body = client().search(artist, title, year);
        System.out.println(body);
        PaginatedResult all = mapper.readValue(body, PaginatedResult.class);

        //prendre year si possible
        //prendre country = france si présent
        //prendre le plus de community have

        long nbFr = all.getResults().stream().filter(r -> "FRANCE".equals(r.getCountry().toUpperCase())).count();


        if (nbFr > 0) {
            return all.getResults().stream().filter(r -> {
                return "FRANCE".equals(r.getCountry().toUpperCase());
            }).sorted(Comparator.comparing(a -> a.getCommunity().getHave()))
                    .collect(Collectors.toList());
        }
        return all.getResults().stream().sorted(Comparator.comparing(a -> a.getCommunity().getHave())).collect(Collectors.toList());
    }
}
