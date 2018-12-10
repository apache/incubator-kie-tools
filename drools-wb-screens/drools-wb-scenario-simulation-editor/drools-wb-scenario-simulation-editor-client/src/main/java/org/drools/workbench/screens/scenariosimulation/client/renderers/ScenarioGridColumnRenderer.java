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

import java.util.List;
import java.util.function.BiFunction;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.ColumnRenderingStrategyFlattened;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.StringColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class ScenarioGridColumnRenderer extends StringColumnRenderer {

    @Override
    public Group renderCell(final GridCell<String> cell,
                            final GridBodyCellRenderContext context) {
        if (cell == null || cell.getValue() == null || (cell.getValue().getValue() == null && cell.getValue().getPlaceHolder() == null)) {
            return null;
        }

        final ScenarioGridRendererTheme theme = (ScenarioGridRendererTheme) context.getRenderer().getTheme();

        Text text;
        String value;

        // Show placeholder only if the following conditions are met
        if ((cell instanceof ScenarioGridCell) && cell.getValue() != null && (cell.getValue().getValue() == null || cell.getValue().getValue().isEmpty() ) && cell.getValue().getPlaceHolder() != null) {
            // Render as placeholder
            text = theme.getPlaceholderText();
            value = cell.getValue().getPlaceHolder();
        } else {
            text = ((ScenarioGridCell) cell).isErrorMode() ? theme.getErrorText() : theme.getBodyText();
            value = cell.getValue() != null ? cell.getValue().getValue() : null;
        }

        return internalRenderCell(cell,
                                  context,
                                  text,
                                  value);
    }

    @Override
    protected Group internalRenderCell(final GridCell<String> cell,
                             final GridBodyCellRenderContext context,
                             final Text text,
                             final String value) {
        if (value == null) {
            return null;
        }

        final GridRenderer renderer = context.getRenderer();
        final ScenarioGridRendererTheme theme = (ScenarioGridRendererTheme) renderer.getTheme();

        final Group g = new Group();

        text.setText(value);
        text.setListening(false);
        text.setX(context.getCellWidth() / 2);
        text.setY(context.getCellHeight() / 2);

        applyBackgroundColor((ScenarioGridCell)cell, context, g, theme);

        g.add(text);
        return g;
    }

    void applyBackgroundColor(ScenarioGridCell cell,
                              GridBodyCellRenderContext context,
                              Group group,
                              ScenarioGridRendererTheme theme) {
        if (cell.isErrorMode()) {
            final Rectangle bodyErrorBackground = theme.getBodyErrorBackground(cell);
            bodyErrorBackground.setWidth(context.getCellWidth());
            bodyErrorBackground.setHeight(context.getCellHeight());
            group.add(bodyErrorBackground);
        }
    }

    @Override
    public List<GridRenderer.RendererCommand> renderColumn(GridColumn<?> column, GridBodyColumnRenderContext context, BaseGridRendererHelper rendererHelper, BaseGridRendererHelper.RenderingInformation renderingInformation, BiFunction<Boolean, GridColumn<?>, Boolean> columnRenderingConstraint) {
        return ColumnRenderingStrategyFlattened.render(column,
                                                       context,
                                                       rendererHelper,
                                                       renderingInformation,
                                                       columnRenderingConstraint);
    }
}