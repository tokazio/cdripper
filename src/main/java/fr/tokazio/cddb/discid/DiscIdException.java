package fr.tokazio.cddb.discid;

public class DiscIdException extends Exception {

    public DiscIdException(String message) {
        super(message);
    }

    public DiscIdException(Exception ex) {
        super(ex);
    }
}
