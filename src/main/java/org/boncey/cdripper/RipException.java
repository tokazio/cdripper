package org.boncey.cdripper;

public class RipException extends Exception {

    public RipException(Exception e) {
        super(e);
    }

    public RipException(String message) {
        super(message);
    }
}
