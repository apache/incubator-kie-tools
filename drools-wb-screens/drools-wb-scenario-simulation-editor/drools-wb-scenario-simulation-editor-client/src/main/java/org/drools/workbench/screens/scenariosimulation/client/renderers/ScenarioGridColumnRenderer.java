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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Text;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.StringColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

public class ScenarioGridColumnRenderer extends StringColumnRenderer {

    @Override
    public Group renderCell(final GridCell<String> cell,
                            final GridBodyCellRenderContext context) {
        if (cell == null) { // nothing to render
            return null;
        }
        // Show placeholder only if the following conditions are met
        if ((cell instanceof ScenarioGridCell) && cell.getValue() != null && cell.getValue().getValue() == null && ((ScenarioGridCellValue) cell.getValue()).getPlaceHolder() != null) {
            // Render as placeholder
            return renderPlaceholderCell((ScenarioGridCell) cell, context);
        } else {
            // Otherwise delegate to default implementation
            return super.renderCell(cell, context);
        }
    }

    Group renderPlaceholderCell(final ScenarioGridCell cell,
                                        final GridBodyCellRenderContext context) {
        final GridRenderer renderer = context.getRenderer();
        final ScenarioGridRendererTheme theme = (ScenarioGridRendererTheme) renderer.getTheme();
        final Group toReturn = new Group();
        final Text t = theme.getPlaceholderText();
        t.setText(((ScenarioGridCellValue) cell.getValue()).getPlaceHolder());
        t.setListening(false);
        t.setX(context.getCellWidth() / 2);
        t.setY(context.getCellHeight() / 2);
        toReturn.add(t);
        return toReturn;
    }
}