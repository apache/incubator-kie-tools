/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;

/**
 * A in-cell widget representing the state of merged cells; i.e. collapsed or expanded.
 */
public class GroupingToggle extends Group {

    private static final String TOGGLE_FONT_FAMILY = "Glyphicons Halflings";

    private static final double TOGGLE_FONT_SIZE = 12.0;

    private static final String TOGGLE_ICON_EXPAND = "\ue252";

    private static final String TOGGLE_ICON_COLLAPSE = "\ue253";

    private static final double PADDING = 8;

    private final Text toggle = new Text("",
                                         TOGGLE_FONT_FAMILY,
                                         TOGGLE_FONT_SIZE) {{
        setFillColor(ColorName.GRAY);
    }};

    /**
     * Constructor for the widget.
     * @param cellWidth Width of the containing cell.
     * @param cellHeight Height of the containing cell.
     * @param isGrouped true is the cell is collapsed.
     */
    @SuppressWarnings("unused")
    public GroupingToggle(final double cellWidth,
                          final double cellHeight,
                          final boolean isGrouped) {
        toggle.setText(isGrouped ? TOGGLE_ICON_EXPAND : TOGGLE_ICON_COLLAPSE);
        toggle.setX(cellWidth - TOGGLE_FONT_SIZE - PADDING);
        toggle.setY(TOGGLE_FONT_SIZE + PADDING);
        add(toggle);
    }

    /**
     * Check whether a cell-relative coordinate is "on" the hot-spot to toggle the collapsed/expanded state.
     * @param cellX The MouseEvent relative to the cell's x-coordinate.
     * @param cellY The MouseEvent relative to the cell's y-coordinate.
     * @param cellWidth Width of the containing cell.
     * @param cellHeight Height of the containing cell.
     * @return true if the cell coordinate is on the hot-spot.
     */
    @SuppressWarnings("unused")
    public static boolean onHotSpot(final double cellX,
                                    final double cellY,
                                    final double cellWidth,
                                    final double cellHeight) {
        final double offsetX = cellWidth - TOGGLE_FONT_SIZE - PADDING;
        if (cellX - offsetX > 0 && cellX - offsetX < TOGGLE_FONT_SIZE) {
            if (cellY > PADDING && cellY < PADDING + TOGGLE_FONT_SIZE) {
                return true;
            }
        }
        return false;
    }
}
