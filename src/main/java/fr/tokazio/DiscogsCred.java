package fr.tokazio;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscogsCred {

    private final String oauth_token;
    private final String oauth_token_secret;

    @JsonCreator
    public DiscogsCred(@JsonProperty("oauth_token") String oauth_token, @JsonProperty("oauth_token_secret") String oauth_token_secret) {
        this.oauth_token = oauth_token;
        this.oauth_token_secret = oauth_token_secret;
    }

    public String getOauth_token() {
        return oauth_token;
    }

    public String getOauth_token_secret() {
        return oauth_token_secret;
    }
}
