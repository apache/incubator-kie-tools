/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid;

import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.RuleAnnotationClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionKindRowColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationParameterColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.IsRowDragHandle;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.KIEColours;

public class BaseExpressionGridTheme implements GridRendererTheme {

    public static final String FONT_FAMILY_HEADER = "Open Sans, Helvetica, Arial, sans-serif";

    public static final String FONT_FAMILY_LABEL = "Open Sans, Helvetica, Arial, sans-serif";

    public static final String FONT_FAMILY_EXPRESSION = "Courier New";

    public static final double SELECTOR_STROKE_WIDTH = 2.0;

    public static final double STROKE_WIDTH = 1.0;

    public static final int FONT_SIZE = 10;

    public static final double DEFAULT_STROKE_WIDTH = 0;

    @Override
    public String getName() {
        return "DMN Editor";
    }

    @Override
    public MultiPath getSelector() {
        return new MultiPath().setVisible(false);
    }

    @Override
    public Rectangle getCellSelectorBorder() {
        return new Rectangle(0, 0)
                .setStrokeColor(KIEColours.CELL_FOCUS)
                .setStrokeWidth(SELECTOR_STROKE_WIDTH);
    }

    @Override
    public Rectangle getCellSelectorBackground() {
        return new Rectangle(0, 0).setVisible(false);
    }

    @Override
    public Rectangle getHeaderBackground(final GridColumn<?> column) {
        final Rectangle background = new Rectangle(0, 0);
        if (column instanceof NameColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_BLUE);
        } else if (column instanceof InvocationParameterColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_BLUE);
        } else if (column instanceof org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.NameColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_BLUE);
        } else if (column instanceof RelationColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_BLUE);
        } else if (column instanceof LiteralExpressionColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_BLUE);
        } else if (column instanceof IsRowDragHandle) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_WHITE);
        } else if (column instanceof InputClauseColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_BLUE);
        } else if (column instanceof OutputClauseColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_DARK_BLUE);
        } else if (column instanceof RuleAnnotationClauseColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_WHITE);
        } else if (column instanceof ExpressionEditorColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_BLUE);
        } else if (column instanceof UndefinedExpressionColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_LIGHT_BLUE);
        } else if (column instanceof FunctionKindRowColumn) {
            background.setFillColor(KIEColours.HEADER_BACKGROUND_WHITE);
        }
        background.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        return background;
    }

    @Override
    public MultiPath getHeaderGridLine() {
        return new MultiPath()
                .setStrokeColor(KIEColours.TABLE_GRID)
                .setStrokeWidth(STROKE_WIDTH)
                .setVisible(true);
    }

    @Override
    public Text getHeaderText() {
        return new Text("")
                .setFillColor(ColorName.BLACK)
                .setFontSize(FONT_SIZE)
                .setFontFamily(FONT_FAMILY_HEADER)
                .setTextUnit(TextUnit.PT)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER)
                .setStrokeWidth(DEFAULT_STROKE_WIDTH);
    }

    @Override
    public Rectangle getBodyBackground(final GridColumn<?> column) {
        return new Rectangle(0, 0)
                .setFillColor(KIEColours.CELL_CONTENT)
                .setStrokeWidth(DEFAULT_STROKE_WIDTH);
    }

    @Override
    public MultiPath getBodyGridLine() {
        return new MultiPath()
                .setStrokeColor(KIEColours.TABLE_GRID)
                .setStrokeWidth(STROKE_WIDTH)
                .setVisible(true);
    }

    @Override
    public Text getBodyText() {
        return new Text("")
                .setFillColor(ColorName.BLACK)
                .setFontSize(FONT_SIZE)
                .setFontFamily(FONT_FAMILY_LABEL)
                .setTextUnit(TextUnit.PT)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER)
                .setStrokeWidth(DEFAULT_STROKE_WIDTH);
    }

    @Override
    public Rectangle getGridBoundary() {
        return new Rectangle(0, 0)
                .setStrokeColor(KIEColours.TABLE_GRID)
                .setStrokeWidth(STROKE_WIDTH)
                .setVisible(true);
    }

    @Override
    public Line getGridHeaderBodyDivider() {
        return new Line()
                .setStrokeColor(KIEColours.TABLE_GRID)
                .setStrokeWidth(STROKE_WIDTH)
                .setVisible(true);
    }
}
