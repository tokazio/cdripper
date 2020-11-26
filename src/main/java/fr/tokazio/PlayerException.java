package fr.tokazio;

public class PlayerException extends Exception {


    private final int code;

    public PlayerException(final int code) {
        this.code = code;
    }

    public PlayerException(final int code, final Exception ex) {
        super(ex);
        this.code = code;
    }

    public int getResponseCode() {
        return code;
    }
}
