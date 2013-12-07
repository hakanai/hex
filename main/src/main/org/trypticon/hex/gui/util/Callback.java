package org.trypticon.hex.gui.util;

/**
 * An interface for callback-style APIs. Receives the result of the operation.
 *
 * @author trejkaz
 */
public interface Callback<T> {

    /**
     * Executed when the operation completes.
     *
     * @param result the result of the operation.
     */
    void execute(T result);
}
