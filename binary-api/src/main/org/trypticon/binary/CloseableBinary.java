package org.trypticon.binary;

import java.io.Closeable;

/**
 * Extension on top of the normal {@link Binary} interface indicating that the binary is holding onto some resource
 * which needs to be closed.
 *
 * @author trejkaz
 */
public interface CloseableBinary extends Binary, Closeable {
}
