/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.drools.guvnor.client.common;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * A simple property grid that displays name-value pairs.
 * <br>
 * Used styles:
 * <ul>
 * <li>bpm-prop-grid
 * <li>bpm-prop-grid-label
 * <li>bpm-prop-grid-even
 * <li>bpm-prop-grid-odd
 * </ul>
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class PropertyGrid extends ScrollPanel {

    private String[] fieldNames;

    private Grid grid;

    public PropertyGrid(String[] fieldDesc) {
        this.grid = new Grid(fieldDesc.length, 2);
        grid.setStyleName("bpm-prop-grid");
        this.fieldNames = fieldDesc;

        this.add(grid);

        initReset();
    }

    private void initReset() {
        for (int i = 0; i < fieldNames.length; i++) {
            Label label = new Label(fieldNames[i]);
            label.setStyleName("bpm-prop-grid-label");
            grid.setWidget(i, 0, label);
            grid.setWidget(i, 1, new HTML(""));

            String style = (i % 2 == 0) ? "bpm-prop-grid-even" : "bpm-prop-grid-odd";
            grid.getRowFormatter().setStyleName(i, style);
            grid.getColumnFormatter().setWidth(0, "20%");
            grid.getColumnFormatter().setWidth(1, "80%");
        }
    }

    public void clear() {
        initReset();
    }

    public void update(String[] fieldValues) {
        if (fieldValues.length != fieldNames.length) {
            throw new IllegalArgumentException("fieldValues.length doesn't match fieldName.length: " + fieldNames);
        }

        for (int i = 0; i < fieldNames.length; i++) {
            Label label = new Label(fieldNames[i]);
            label.setStyleName("bpm-prop-grid-label");
            grid.setWidget(i, 0, label);
            grid.setWidget(i, 1, new HTML(fieldValues[i]));
        }
    }

}
