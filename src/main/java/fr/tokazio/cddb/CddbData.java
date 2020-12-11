package fr.tokazio.cddb;

import java.util.LinkedList;
import java.util.List;

public class CddbData {

    private final String cdid;
    private final String artist;
    private final String album;
    private String year = "";
    private final List<Track> trackNames = new LinkedList<>();

    public CddbData(String cdid, String title) {
        this.cdid = cdid;
        String[] str = title.split("/");
        this.artist = str[0];
        this.album = str[1];
    }

    public void addTrack(final String trackName) {
        int index = trackNames.size();
        trackNames.add(new Track(index, trackName));
    }

    public String getCdid() {
        return cdid;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public List<Track> getTracks() {
        return trackNames;
    }

    public String getYear() {
        return year;
    }

    public void setYear(final String year) {
        this.year = year != null ? year : "";
    }

    public static class Track {

        private final String index;
        private final String artist;
        private final String title;

        public Track(int index, String trackName) {
            String[] str = trackName.split("/");
            this.index = ((index < 10) ? "0" : "") + index;
            this.artist = str[0];
            this.title = str[1];
        }

        public String getArtist() {
            return artist;
        }

        public String getTitle() {
            return title;
        }

        public String getIndex() {
            return index;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Track{");
            sb.append("index='").append(index).append('\'');
            sb.append(", artist='").append(artist).append('\'');
            sb.append(", title='").append(title).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
