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

package org.trypticon.hex.scripting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jtechdev.macwidgets.SourceList;
import com.jtechdev.macwidgets.SourceListCategory;
import com.jtechdev.macwidgets.SourceListItem;
import com.jtechdev.macwidgets.SourceListModel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Simple editor for managing scripts.
 *
 * @author trejkaz
 */
public class ScriptEditor extends JFrame {
    public ScriptEditor() {
        SourceListModel sourceListModel = new SourceListModel();
        new SystemScriptProvider().populate(sourceListModel);
        SourceList sourceList = new SourceList(sourceListModel);
        for (SourceListCategory category : sourceList.getModel().getCategories()) {
            for (SourceListItem item : category.getItems()) {
                sourceList.setExpanded(item, false);
            }
        }
        sourceList.getComponent().setPreferredSize(new Dimension(200, 600));
        //HACK: SourceList offers no good way to get at this.
        JTree sourceListTree = (JTree)
            ((JScrollPane) sourceList.getComponent().getComponent(0)).getViewport().getView();
        sourceListTree.setUI(new FixedSourceListTreeUI());

        RSyntaxTextArea editor = new RSyntaxTextArea(30, 80);
        editor.setUseSelectedTextColor(true);
        editor.setSelectedTextColor(UIManager.getColor("TextArea.selectionForeground"));
        editor.setSelectionColor(UIManager.getColor("TextArea.selectionBackground"));
        editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
        if (PLAFUtils.isAqua()) { //TODO: Strictly incorrect if LAF changes at runtime.
            // Workaround for keyboard shortcuts: https://github.com/bobbylight/RSyntaxTextArea/issues/56
            editor.setActionMap(new RTAMacActionMap(editor.getActionMap()));
            editor.setInputMap(JComponent.WHEN_FOCUSED, new RTAMacInputMap());
        }

        RTextScrollPane editorScrollPane = new RTextScrollPane(editor);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setContinuousLayout(true);
        splitPane.setLeftComponent(sourceList.getComponent());
        splitPane.setRightComponent(editorScrollPane);
        splitPane.setDividerSize(1);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);

        // Has to occur after adding the scroll pane around the editor pane.
//        editorPane.setContentType("text/x-ruby");
        editor.setText("puts \"thingies\"\n");

        pack();
        splitPane.setDividerLocation(0.25);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//            } catch (Exception e) {}
            ScriptEditor editor = new ScriptEditor();
            editor.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            editor.setVisible(true);
        });
    }
}
