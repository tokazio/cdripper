package fr.tokazio;

public class RippingStatus {

    private String serviceState;

    private int trackId;
    private String discTitle;
    private String discArtist;
    private String trackTitle;
    private String trackArtist;
    private int trackProgress;

    public String getServiceState() {
        return serviceState;
    }

    public RippingStatus setServiceState(String serviceState) {
        this.serviceState = serviceState;
        return this;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getDiscTitle() {
        return discTitle;
    }

    public void setDiscTitle(String discTitle) {
        this.discTitle = discTitle;
    }

    public String getDiscArtist() {
        return discArtist;
    }

    public void setDiscArtist(String discArtist) {
        this.discArtist = discArtist;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }

    public int getTrackProgress() {
        return trackProgress;
    }

    public void setTrackProgress(int trackProgress) {
        this.trackProgress = trackProgress;
    }
}
