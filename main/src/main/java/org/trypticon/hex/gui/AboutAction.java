/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2014  Trejkaz, Hex Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.trypticon.hex.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import org.trypticon.hex.gui.util.BaseAction;

/**
 * Action to show an About dialog.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class AboutAction extends BaseAction {
    public AboutAction() {
        Resources.localiseAction(this, "About");
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        JDialog dialog = new JDialog(null, Resources.getString("About.name"), Dialog.ModalityType.MODELESS);
        dialog.setContentPane(new AboutPanel());
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static class AboutPanel extends JPanel {
        public AboutPanel() {
            Properties properties = new Properties();
            try (InputStream stream = getClass().getResourceAsStream("/META-INF/MANIFEST.MF")) {
                properties.load(stream);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read jar manifest", e);
            }

            //TODO: A real icon
            JLabel iconLabel = new JLabel(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(Color.LIGHT_GRAY);
                    ((Graphics2D) g).draw(new Rectangle(x, y, 63, 63));
                }

                @Override
                public int getIconWidth() {
                    return 64;
                }

                @Override
                public int getIconHeight() {
                    return 64;
                }
            });

            JLabel nameLabel = new JLabel(properties.getProperty("Implementation-Title"));
            nameLabel.putClientProperty("JComponent.sizeVariant", "large");
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, nameLabel.getFont().getSize() + 2.0f));

            JLabel versionLabel = new JLabel(Resources.getString("About.versionFormat",
                                                                 properties.getProperty("Implementation-Version")));
            versionLabel.putClientProperty("JComponent.sizeVariant", "small");

            JLabel copyrightLabel = new JLabel(properties.getProperty("Copyright"));
            copyrightLabel.putClientProperty("JComponent.sizeVariant", "small");

            GroupLayout layout = new GroupLayout(this);
            layout.setAutoCreateGaps(true);
            setLayout(layout);

            layout.setHorizontalGroup(layout.createSequentialGroup()
                                          .addContainerGap(20, Short.MAX_VALUE)
                                          .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                        .addComponent(iconLabel)
                                                        .addComponent(nameLabel)
                                                        .addComponent(versionLabel)
                                                        .addComponent(copyrightLabel))
                                          .addContainerGap(20, Short.MAX_VALUE));

            layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap(8, 8)
                .addComponent(iconLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 16, 16)
                .addComponent(nameLabel)
                .addComponent(versionLabel)
                .addComponent(copyrightLabel)
                .addContainerGap(19, 19));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            size.width = Math.max(size.width, 284);
            return size;
        }
    }
}
