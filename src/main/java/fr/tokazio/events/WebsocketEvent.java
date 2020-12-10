package fr.tokazio.events;

public class WebsocketEvent {

    private final String message;

    public WebsocketEvent(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
