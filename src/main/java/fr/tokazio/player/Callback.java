package fr.tokazio.player;

/**
 * @author Romain PETIT <tokazio@esyo.net>
 */
@FunctionalInterface
public interface Callback {

    void call() throws Throwable;
}
