package fr.tokazio.events;

public class RippingError {

    private final Exception error;

    public RippingError(Exception e) {
        this.error = e;
    }

    public Exception getError() {
        return error;
    }
}
