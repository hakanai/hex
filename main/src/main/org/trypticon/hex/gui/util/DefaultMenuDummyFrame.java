package org.trypticon.hex.gui.util;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.DefaultFocusManager;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

/**
 * A utility frame which can be used to attach the default menu to display when no other frames are open.
 * This is just a big, hacky workaround for {@code setDefaultMenuBar} itself not working.
 *
 * @author trejkaz
 */
public class DefaultMenuDummyFrame extends JFrame {

    /**
     * Tracks the windows which we know about.
     */
    private final Set<Window> knownWindows = new HashSet<>();

    /**
     * <p>Constructs the dummy frame.</p>
     * <p/>
     * <p>The caller should set it visible themselves, because it's odd to have the construction of a window
     * result in the window becoming visible automatically. Even though the window will be set "visible",
     * we do everything we can to prevent it actually becoming visible.</p>
     *
     * @param menuBar the menu bar to add.
     */
    public DefaultMenuDummyFrame(JMenuBar menuBar) {
        setJMenuBar(menuBar);
        setUndecorated(true);
        setOpacity(0.0f);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        DefaultFocusManager.getCurrentManager().addPropertyChangeListener("activeWindow", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                Window activeWindow = (Window) event.getNewValue();
                if (activeWindow == null ||
                    !(activeWindow instanceof Frame) ||
                    activeWindow instanceof DefaultMenuDummyFrame) {
                    return; // not a real frame
                }

                if (knownWindows.add(activeWindow)) {
                    updateState();

                    // A new top-level frame has appeared.
                    activeWindow.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent event) {
                            Window closedWindow = event.getWindow();
                            if (knownWindows.remove(closedWindow)) {
                                updateState();
                                closedWindow.removeWindowListener(this);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Because the menu only appears while the window is focused, we do some acrobatics in here to set it
     * focusable only when no other windows exist.
     */
    private void updateState() {
        // Are there any *real* frames left?
        boolean realWindowFound = false;
        for (Frame frame : Frame.getFrames()) {
            if (!(frame instanceof DefaultMenuDummyFrame) && frame.isDisplayable()) {
                realWindowFound = true;
                break;
            }
        }

        if (realWindowFound) {
            // Setting it non-focusable stops the user getting to it using the keyboard.
            setFocusableWindowState(false);
        } else {
            setFocusableWindowState(true);

            // If you don't call this, the previous window's menu remains as the current menu but in a kind of
            // zombie state where the actions don't fire.
            toFront();
        }
    }
}
