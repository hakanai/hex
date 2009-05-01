package org.trypticon.hex.util.swingsupport;

import javax.swing.UIManager;

/**
 * Look and Feel utilities.
 */
public class PLAFUtils {
    /**
     * Tests if the look and feel is Quaqua, which we use on Mac to get better look and feel for Mac.
     *
     * @return {@code true} if using Quaqua, otherwise {@code false}.
     */
    public static boolean isQuaqua() {
        return "ch.randelshofer.quaqua.QuaquaLookAndFeel".equals(UIManager.getLookAndFeel().getClass().getName());
    }
}
