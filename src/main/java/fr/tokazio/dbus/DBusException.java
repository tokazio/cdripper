package fr.tokazio.dbus;

public class DBusException extends Exception {
    public DBusException(String message) {
        super(message);
    }

    public DBusException(Exception ex) {
        super(ex);
    }
}
