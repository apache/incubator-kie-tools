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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextLineBreakWrap;
import com.ait.lienzo.client.core.shape.TextNoWrap;
import com.ait.lienzo.shared.core.types.TextAlign;
import org.gwtbootstrap3.client.ui.TextArea;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.single.impl.BaseGridColumnSingletonDOMElementRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

public class FunctionColumnRenderer extends BaseGridColumnSingletonDOMElementRenderer<String, TextArea, TextAreaDOMElement> {

    private final GridWidget gridWidget;

    public FunctionColumnRenderer(final TextAreaSingletonDOMElementFactory factory,
                                  final GridWidget gridWidget) {
        super(factory);
        this.gridWidget = gridWidget;
    }

    @Override
    public Group renderCell(final GridCell<String> cell,
                            final GridBodyCellRenderContext context) {
        if (cell == null || cell.getValue() == null) {
            return null;
        }

        final GridCellValue gcv = cell.getValue();
        if (gcv instanceof FunctionParametersCellValue) {
            return renderFunctionParametersCellValue((FunctionParametersCellValue) gcv, context);
        }
        return renderLiteralExpression(cell, context);
    }

    private Group renderFunctionParametersCellValue(final FunctionParametersCellValue cell,
                                                    final GridBodyCellRenderContext context) {
        final FunctionParametersHolder parameters = cell.getValue();
        final GridRenderer renderer = context.getRenderer();
        final GridRendererTheme theme = renderer.getTheme();
        final Group g = new Group();

        final GridColumn<?> column = gridWidget.getModel().getColumns().get(context.getColumnIndex());
        final Rectangle r = theme.getHeaderBackground(column).setVisible(true);
        r.setWidth(context.getCellWidth()).setHeight(context.getCellHeight());
        g.add(r);

        final Text t = theme.getHeaderText()
                .setText(parameters.getExpressionLanguageTitle() + " : " + parameters.getFormalParametersTitle())
                .setListening(false)
                .setVisible(true)
                .setX(context.getCellWidth() / 2)
                .setY(context.getCellHeight() / 2)
                .setTextAlign(TextAlign.CENTER);
        t.setWrapper(new TextNoWrap(t));
        g.add(t);
        return g;
    }

    private Group renderLiteralExpression(final GridCell<String> cell,
                                          final GridBodyCellRenderContext context) {
        final GridRenderer renderer = context.getRenderer();
        final GridRendererTheme theme = renderer.getTheme();
        final Group g = new Group();
        final Text t = theme.getBodyText()
                .setText(cell.getValue().getValue())
                .setListening(false)
                .setX(5)
                .setY(5)
                .setFontFamily(BaseExpressionGridTheme.FONT_FAMILY_EXPRESSION)
                .setTextAlign(TextAlign.LEFT);
        t.setWrapper(new TextLineBreakWrap(t));
        g.add(t);
        return g;
    }
}
