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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes;

import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEStyles;

import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours.HIGHLIGHTED_CELL_BACKGROUND;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours.HIGHLIGHTED_CELL_STROKE;

/**
 * Definition of themes used by a render for the pluggable rendering mechanism.
 */
public interface GridRendererTheme {

    /**
     * Returns a display name for the theme.
     * @return A name for the theme
     */
    String getName();

    /**
     * Delegates construction of the "selector" to sub-classes. All implementations
     * are to provide a MultiPath to draw around the GridWidget.
     * @return A {@link MultiPath} for the "selector"
     */
    MultiPath getSelector();

    /**
     * Delegates construction of the cell "selector" border to sub-classes.
     * @return A {@link Rectangle} for the cell "selector"
     */
    Rectangle getCellSelectorBorder();

    /**
     * Delegates construction of the cell "selector" background to sub-classes.
     * @return A {@link Rectangle} for the cell "selector"
     */
    Rectangle getCellSelectorBackground();

    /**
     * Delegates the Header's background Rectangle to sub-classes.
     * @param column The column being rendered.
     * @return A {@link Rectangle} for the header's background.
     */
    Rectangle getHeaderBackground(final GridColumn<?> column);

    /**
     * Delegates the Header's background Rectangle, used for "linked" columns to sub-classes.
     * @param column The column being rendered.
     * @return A {@link Rectangle} for a header's background for a "linked" column.
     */
    Rectangle getHeaderLinkBackground(final GridColumn<?> column);

    /**
     * Delegates the Header's grid lines to sub-classes.
     * @return A {@link MultiPath} to be used to render the header's grid lines.
     */
    MultiPath getHeaderGridLine();

    /**
     * Delegates the Header's Text to sub-classes.
     * @return A {@link Text} used to render all text in the header.
     */
    Text getHeaderText();

    /**
     * Delegates the Body's background Rectangle to sub-classes.
     * @param column The column being rendered.
     * @return A {@link Rectangle} for the body's background.
     */
    Rectangle getBodyBackground(final GridColumn<?> column);

    /**
     * Delegates the Body's grid lines to sub-classes.
     * @return A {@link MultiPath} to be used to render the body's grid lines.
     */
    MultiPath getBodyGridLine();

    /**
     * Delegates the Body's Text to sub-classes.
     * @return A {@link Text} used to render all text in the body.
     */
    Text getBodyText();

    /**
     * Delegates construction of the Grids boundary to sub-classes. All implementations
     * are to provide a Rectangle surrounding the whole GridWidget.
     * @return A {@link Rectangle} for the Grid's boundary.
     */
    Rectangle getGridBoundary();

    /**
     * Delegates construction of a divider between the Grids header and body to sub-classes.
     * @return A {@link Line} for the divider.
     */
    Line getGridHeaderBodyDivider();

    /**
     * Delegates the Body's Text to sub-classes.
     * @return A {@link Text} used to render the placeholder in the body.
     */
    default Text getPlaceholderText() {
        return new Text("")
                .setFillColor(KIEColours.PLACEHOLDER_COLOR)
                .setFontSize(KIEStyles.FONT_SIZE)
                .setFontFamily(KIEStyles.FONT_FAMILY_LABEL)
                .setFontStyle(KIEStyles.FONT_STYLE_ITALIC)
                .setTextUnit(TextUnit.PT)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER);
    }

    /**
     * Delegates the highlighted cell background Rectangle to sub-classes.
     * @return A {@link Rectangle} for the cell's highlight background.
     */
    default Rectangle getHighlightedCellBackground() {
        final Rectangle r = new Rectangle(0, 0);
        r.setFillColor(HIGHLIGHTED_CELL_BACKGROUND);
        r.setStrokeWidth(1.0);
        // We need some alpha because the highlight is draw over the cell content.
        r.setAlpha(0.3);
        r.setStrokeColor(HIGHLIGHTED_CELL_STROKE);
        return r;
    }
}
