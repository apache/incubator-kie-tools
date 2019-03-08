/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.CellContextUtilities;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationInvokeContextMenuForSelectedCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class ScenarioInvokeContextMenuForSelectedCell extends KeyboardOperationInvokeContextMenuForSelectedCell {

    private ScenarioContextMenuRegistry scenarioContextMenuRegistry;

    public ScenarioInvokeContextMenuForSelectedCell(final GridLayer gridLayer, final ScenarioContextMenuRegistry
            scenarioContextMenuRegistry) {
        super(gridLayer);
        this.scenarioContextMenuRegistry = scenarioContextMenuRegistry;
    }

    @Override
    public boolean isExecutable(final GridWidget gridWidget) {
        final GridData model = gridWidget.getModel();
        if (model.getSelectedHeaderCells().size() == 1 && model.getSelectedCells().size() == 0) {
            return true;
        }
        if (model.getSelectedHeaderCells().size() == 0 && model.getSelectedCells().size() == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean perform(final GridWidget gridWidget, final boolean isShiftKeyDown, final boolean isControlKeyDown) {
        final GridData model = gridWidget.getModel();
        GridData.SelectedCell origin = null;
        boolean isHeader = false;
        if (model.getSelectedHeaderCells().size() == 1) {
            origin = model.getSelectedHeaderCells().get(0);
            isHeader = true;
        } else if (model.getSelectedCells().size() == 1) {
            origin = model.getSelectedCellsOrigin();
            isHeader = false;
        }
        final int uiRowIndex = origin.getRowIndex();
        final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(model.getColumns(),
                                                                         origin.getColumnIndex());
        final GridColumn<?> column = model.getColumns().get(uiColumnIndex);
        if (column instanceof ScenarioGridColumn) {
            final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
            final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
            final double columnXCoordinate = rendererHelper.getColumnOffset(column) + column.getWidth() / 2;
            final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(columnXCoordinate);

            final GridBodyCellEditContext context = isHeader ?
                    CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                     ri,
                                                                     ci,
                                                                     uiRowIndex)
                    : CellContextUtilities.makeCellRenderContext(gridWidget,
                                                                 ri,
                                                                 ci,
                                                                 uiRowIndex);
            final int cellXMiddle = (int) (context.getAbsoluteCellX() +
                    context.getCellWidth() / 2 +
                    gridLayer.getDomElementContainer().getAbsoluteLeft());
            final int cellYMiddle = (int) (context.getAbsoluteCellY() +
                    context.getCellHeight() / 2 +
                    gridLayer.getDomElementContainer().getAbsoluteTop());

            return scenarioContextMenuRegistry.manageRightClick((ScenarioGrid) gridWidget,
                                                                cellXMiddle,
                                                                cellYMiddle,
                                                                uiRowIndex,
                                                                uiColumnIndex,
                                                                isHeader);
        }
        return false;
    }
}
