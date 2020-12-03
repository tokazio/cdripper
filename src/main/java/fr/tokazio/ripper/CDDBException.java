package fr.tokazio.ripper;

/**
 * @link https://github.com/samskivert/samskivert/blob/master/src/main/java/com/samskivert/net/cddb/CDDBTest.java
 */
public class CDDBException extends Exception {
    protected int _code;

    public CDDBException(int code, String message) {
        super(message);
        _code = code;
    }

    public int getCode() {
        return _code;
    }
}
