package fr.tokazio.cddb;

import java.util.LinkedList;
import java.util.List;

public class CddbData {

    private final String cdid;
    private final String artist;
    private final String album;
    private String year = "";
    private final List<Track> trackNames = new LinkedList<>();

    public CddbData(int trackCount) {
        this.cdid = "";
        this.artist = "Unknown";
        this.album = System.currentTimeMillis() + "";
        for (int i = 0; i < trackCount; i++) {
            trackNames.add(new Track(i, "Unknown - no title " + i));
        }
    }

    public CddbData(final String cdid, final String title) {
        if (cdid == null) {
            throw new IllegalArgumentException("CddbData need a discId");
        }
        this.cdid = cdid;
        if (title == null) {
            throw new IllegalArgumentException("CddbData need a title");
        }
        if (!title.isEmpty()) {
            String[] str = title.split("/");
            this.artist = str[0];
            this.album = str[1];
        } else {
            this.artist = "";
            this.album = "";
        }
    }

    public boolean isEmpty() {
        return cdid.isEmpty();
    }

    public void addTrack(final String trackName) {
        if (trackName == null) {
            throw new IllegalArgumentException("You can't provide a null track");
        }
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

    public int getTrackCount() {
        return trackNames.size();
    }

    public static class Track {

        private final String index;
        private final String artist;
        private final String title;

        public Track(final int index, final String trackName) {
            this.index = (index < 10 ? "0" : "") + index;
            if (trackName == null) {
                throw new IllegalArgumentException("You can't provide a null track name");
            }
            if (!trackName.isEmpty()) {
                String[] str = trackName.split("/");
                this.artist = str[0];
                this.title = str[1];
            } else {
                this.artist = "";
                this.title = "";
            }
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
            return "Track{" + "index='" + index + '\'' +
                    ", artist='" + artist + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }
}
