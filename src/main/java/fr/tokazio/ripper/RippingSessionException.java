package fr.tokazio.ripper;

public class RippingSessionException extends Exception {

    public RippingSessionException(String message) {
        super(message);
    }

    public RippingSessionException(Exception ex) {
        super(ex);
    }
}
