package fr.tokazio;

import com.adamdonegan.Discogs4J.client.DiscogsClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

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
    private static final String CALLBACK_URL = "http://127.0.0.1:8080/discogs/callback";

    @Inject
    ObjectMapper mapper;
    private DiscogsClient client;

    public DiscogsService() {
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
        client.setCallbackUrl(CALLBACK_URL);

        client.getRequestToken();

        String url = client.getAuthorizationURL();
        //TODO automatiser
        LOGGER.warn("!!!!!!!!!!!!!!!!\nCliquez ici pour autoriser: " + url + "\n!!!!!!!!!!!!!!!!");

    }

    public void callback(String token, String verifier) {
        LOGGER.debug("Discogs callback: " + token + " | " + verifier);
        client.getAccessToken(verifier);
        String oauth_token = client.getOauthToken();
        String oauth_token_secret = client.getOauthTokenSecret();

        try {
            mapper.writeValue(new File(DISCOGS_CRED), new DiscogsCred(oauth_token, oauth_token_secret));
        } catch (IOException e) {
            LOGGER.error("Error saving discogs credentials");
        }


        //TODO save it
    }

    public String search(String query) {
        return client.search(query);
    }
}
