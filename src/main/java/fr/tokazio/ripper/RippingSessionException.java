package fr.tokazio.ripper;

public class RippingSessionException extends Throwable {

    public RippingSessionException(String message) {
        super(message);
    }

    public RippingSessionException(Exception ex) {
        super(ex);
    }
}
