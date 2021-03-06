/*
 *   Copyright 2005-2007 Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.opal.gui;

import java.awt.*;
import javax.swing.*;


/**
 * <i>LabelCellRenderer - A cell renderer for lists and combo boxes that sets up a {@link JLabel} for its cells.</i><br> <br>
 *
 * @author lhunath
 */
public class LabelCellRenderer extends DefaultListCellRenderer {

    /**
     * @inheritDoc
     */
    @Override
    public Component getListCellRendererComponent(JList list, final Object value, final int index, boolean isSelected,
                                                  final boolean cellHasFocus) {

        setHorizontalTextPosition( CENTER );
        if (value instanceof JLabel) {
            setBackground( ((JLabel) value).getBackground() );
            setForeground( ((JLabel) value).getForeground() );
            setText( ((JLabel) value).getText() );
        } else
            super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

        return this;
    }
}
