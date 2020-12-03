package fr.tokazio.cddb;


/**
 * @link https://github.com/samskivert/samskivert/blob/master/src/main/java/com/samskivert/net/cddb/CDDBTest.java
 */
public class CDDBException extends Exception {
    protected int _code;

    public CDDBException(int code, String message) {
        super(message);
        _code = code;
    }

    public CDDBException(Exception ex) {
        super(ex);
    }

    public CDDBException(String message) {
        super(message);
    }

    public int getCode() {
        return _code;
    }
}
