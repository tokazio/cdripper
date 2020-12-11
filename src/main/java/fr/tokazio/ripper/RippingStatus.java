package fr.tokazio.ripper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RippingStatus {

    private static final Logger LOGGER = LoggerFactory.getLogger(RippingStatus.class);

    private String serviceState;

    private int trackId;
    private int trackNb;
    private String discTitle;
    private String discArtist;
    private String trackTitle;
    private String trackArtist;
    private int trackProgress;

    public String getServiceState() {
        return serviceState;
    }

    public RippingStatus setServiceState(final String serviceState) {
        this.serviceState = serviceState;
        return this;
    }

    public int getTrackNb() {
        return this.trackNb;
    }

    public RippingStatus setTrackNb(int trackNb) {
        this.trackNb = trackNb;
        return this;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(final int trackId) {
        this.trackId = trackId;
    }

    public String getDiscTitle() {
        return discTitle;
    }

    public void setDiscTitle(final String discTitle) {
        this.discTitle = escape(discTitle);
    }

    public String getDiscArtist() {
        return discArtist;
    }

    public void setDiscArtist(final String discArtist) {
        this.discArtist = escape(discArtist);
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(final String trackTitle) {
        this.trackTitle = escape(trackTitle);
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public void setTrackArtist(final String trackArtist) {
        this.trackArtist = escape(trackArtist);
    }

    private String escape(String str) {
        return str.replaceAll("'", "\\'");// escapeHtml4(str);
    }

    public int getTrackProgress() {
        return trackProgress;
    }

    public void setTrackProgress(int trackProgress) {
        this.trackProgress = trackProgress;
    }

    public String asJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error("Impossible de transformer le status en JSON", e);
        }
        return "error";
    }
}
