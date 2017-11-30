/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid;

import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

public class BaseExpressionGridTheme implements GridRendererTheme {

    public static final String BACKGROUND_FILL_COLOUR = "#e0e0e0";

    public static final String LABEL_BACKGROUND_FILL_COLOUR = "#fae5bb";

    public static final String ROW_NUMBER_BACKGROUND_FILL_COLOUR = "#ff7f00";

    public static final String INPUT_CLAUSE_BACKGROUND_FULL_COLOUR = "#238acc";

    public static final String OUTPUT_CLAUSE_BACKGROUND_FULL_COLOUR = "#70b3de";

    public static final String FONT_FAMILY_HEADER = "Times New Roman";

    public static final String FONT_FAMILY_LABEL = "Times New Roman";

    public static final String FONT_FAMILY_EXPRESSION = "Courier New";

    @Override
    public String getName() {
        return "DMN Editor";
    }

    @Override
    public MultiPath getSelector() {
        return new MultiPath().setFillColor(ColorName.GREEN).setAlpha(0.20);
    }

    @Override
    public Rectangle getCellSelector() {
        return new Rectangle(0, 0).setVisible(false);
    }

    @Override
    public Rectangle getHeaderBackground(final GridColumn<?> column) {
        return new Rectangle(0,
                             0)
                .setFillColor(BACKGROUND_FILL_COLOUR);
    }

    @Override
    public Rectangle getHeaderLinkBackground(final GridColumn<?> column) {
        return new Rectangle(0,
                             0)
                .setFillColor(ColorName.LIGHTGRAY);
    }

    @Override
    public MultiPath getHeaderGridLine() {
        return new MultiPath()
                .setStrokeColor(ColorName.WHITE)
                .setStrokeWidth(2.0)
                .setVisible(true);
    }

    @Override
    public Text getHeaderText() {
        return new Text("")
                .setFillColor(ColorName.BLACK)
                .setFontSize(12)
                .setFontFamily(FONT_FAMILY_HEADER)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER);
    }

    @Override
    public Rectangle getBodyBackground(final GridColumn<?> column) {
        final Rectangle background = new Rectangle(0, 0)
                .setStrokeColor(ColorName.WHITE)
                .setStrokeWidth(0.0);
        if (column instanceof NameColumn) {
            background.setFillColor(LABEL_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof LiteralExpressionColumn) {
            background.setFillColor(BACKGROUND_FILL_COLOUR);
        } else if (column instanceof RowNumberColumn) {
            background.setFillColor(ROW_NUMBER_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof InputClauseColumn) {
            background.setFillColor(INPUT_CLAUSE_BACKGROUND_FULL_COLOUR);
        } else if (column instanceof OutputClauseColumn) {
            background.setFillColor(OUTPUT_CLAUSE_BACKGROUND_FULL_COLOUR);
        }
        return background;
    }

    @Override
    public MultiPath getBodyGridLine() {
        return new MultiPath()
                .setStrokeColor(ColorName.WHITE)
                .setStrokeWidth(2.0)
                .setVisible(true);
    }

    @Override
    public Text getBodyText() {
        return new Text("")
                .setFillColor(ColorName.BLACK)
                .setFontSize(12)
                .setFontFamily(FONT_FAMILY_LABEL)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER);
    }

    @Override
    public Rectangle getGridBoundary() {
        return new Rectangle(0,
                             0)
                .setStrokeColor(ColorName.WHITE)
                .setStrokeWidth(2.0)
                .setVisible(false);
    }

    @Override
    public Line getGridHeaderBodyDivider() {
        return new Line()
                .setStrokeColor(ColorName.WHITE)
                .setStrokeWidth(2.0)
                .setVisible(true);
    }
}
