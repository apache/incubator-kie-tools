/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.client.renderers;

import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEStyles;

import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours.CELL_ERROR_BACKGROUND;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours.CELL_ERROR_FOCUS;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours.CELL_FOCUS;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours.HEADER_BACKGROUND_DARK_BLUE;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours.HEADER_BACKGROUND_LIGHT_BLUE;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours.HEADER_BACKGROUND_WHITE;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours.TABLE_GRID;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours.TABLE_TEXT;

public class BaseExpressionGridTheme implements ScenarioGridRendererTheme {

    public static final String FONT_FAMILY_HEADER = "Open Sans, Helvetica, Arial, sans-serif";

    public static final double SELECTOR_STROKE_WIDTH = 2.0;

    public static final double STROKE_WIDTH = 1.0;


    @Override
    public String getName() {
        return "Test Scenarios (Preview)";
    }

    @Override
    public MultiPath getSelector() {
        return new MultiPath().setVisible(false);
    }

    @Override
    public Rectangle getCellSelectorBorder() {
        return new Rectangle(0, 0)
                .setStrokeColor(CELL_FOCUS)
                .setStrokeWidth(SELECTOR_STROKE_WIDTH);
    }

    @Override
    public Rectangle getCellSelectorBackground() {
        return new Rectangle(0, 0).setVisible(false);
    }

    @Override
    public Rectangle getHeaderBackground(final GridColumn<?> column) {
        String colorToApply;
        // GIVEN
        if(FactMappingType.GIVEN.name().equalsIgnoreCase(column.getHeaderMetaData().get(0).getTitle())) {
            colorToApply = HEADER_BACKGROUND_LIGHT_BLUE;
        }
        // EXPECT
        else if (FactMappingType.EXPECT.name().equalsIgnoreCase(column.getHeaderMetaData().get(0).getTitle())) {
            colorToApply = HEADER_BACKGROUND_DARK_BLUE;
        }
        // DEFAULT
        else {
            colorToApply = HEADER_BACKGROUND_WHITE;
        }

        return new Rectangle(0, 0)
                .setFillColor(colorToApply);
    }

    @Override
    public Rectangle getHeaderLinkBackground(final GridColumn<?> column) {
        return new Rectangle(0, 0)
                .setFillColor(ColorName.LIGHTGRAY);
    }

    @Override
    public MultiPath getHeaderGridLine() {
        return new MultiPath()
                .setStrokeColor(TABLE_GRID)
                .setStrokeWidth(STROKE_WIDTH)
                .setVisible(true);
    }

    @Override
    public Text getHeaderText() {
        return new Text("")
                .setFillColor(TABLE_TEXT)
                .setFontSize(KIEStyles.FONT_SIZE)
                .setFontFamily(FONT_FAMILY_HEADER)
                .setTextUnit(TextUnit.PT)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER);
    }

    @Override
    public Rectangle getBodyBackground(final GridColumn<?> column) {
        final Rectangle background = new Rectangle(0, 0);
        background.setFillColor(ColorName.TRANSPARENT);
        // to customize background on column-type base, set the fill color (e.g. background.setFillColor(LABEL_BACKGROUND_FILL_COLOUR); ) based on the column type
        return background;
    }

    @Override
    public MultiPath getBodyGridLine() {
        return new MultiPath()
                .setStrokeColor(TABLE_GRID)
                .setStrokeWidth(STROKE_WIDTH)
                .setVisible(true);
    }

    @Override
    public Text getBodyText() {
        return new Text("")
                .setFillColor(TABLE_TEXT)
                .setFontSize(KIEStyles.FONT_SIZE)
                .setFontFamily(KIEStyles.FONT_FAMILY_LABEL)
                .setTextUnit(TextUnit.PT)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER);
    }

    @Override
    public Rectangle getGridBoundary() {
        return new Rectangle(0, 0)
                .setStrokeColor(TABLE_GRID)
                .setStrokeWidth(STROKE_WIDTH)
                .setVisible(true);
    }

    @Override
    public Line getGridHeaderBodyDivider() {
        return new Line()
                .setStrokeColor(TABLE_GRID)
                .setStrokeWidth(STROKE_WIDTH)
                .setVisible(true);
    }

    @Override
    public Rectangle getBodyErrorBackground(GridCell<?> cell) {
        final Rectangle header = new Rectangle(0,
                                               0)
                .setFillColor(CELL_ERROR_BACKGROUND);
        return header;
    }

    @Override
    public Text getErrorText() {
        return new Text("")
                .setFillColor(CELL_ERROR_FOCUS)
                .setFontSize(KIEStyles.FONT_SIZE)
                .setFontFamily(KIEStyles.FONT_FAMILY_LABEL)
                .setTextUnit(TextUnit.PT)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER);
    }
}
