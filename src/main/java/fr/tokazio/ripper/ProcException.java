package fr.tokazio.ripper;

public class ProcException extends Exception {

    private final int code;
    private final String out;

    public ProcException(int code, String out) {
        this.code = code;
        this.out = out;
    }

    public int getCode() {
        return code;
    }

    public String getOut() {
        return out;
    }
}
