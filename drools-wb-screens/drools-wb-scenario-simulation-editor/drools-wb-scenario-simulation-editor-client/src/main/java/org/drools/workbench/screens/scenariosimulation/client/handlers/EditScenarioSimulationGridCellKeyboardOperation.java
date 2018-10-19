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

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationEditCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.isEditableHeader;
import static org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities.findUiColumnIndex;
import static org.uberfire.ext.wires.core.grids.client.util.RenderContextUtilities.makeRenderContext;

public class EditScenarioSimulationGridCellKeyboardOperation extends KeyboardOperationEditCell {

    public EditScenarioSimulationGridCellKeyboardOperation(final GridLayer gridLayer) {
        super(gridLayer);
    }

    @Override
    protected void editCell(GridWidget gridWidget) {
        super.editCell(gridWidget);
        final GridData model = gridWidget.getModel();
        if (model.getSelectedHeaderCells().size() > 0) {
            final GridData.SelectedCell selectedCell = model.getSelectedHeaderCells().get(0);
            final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
            final int uiHeaderRowIndex = selectedCell.getRowIndex();
            final int uiColumnIndex = findUiColumnIndex(model.getColumns(),
                                                        selectedCell.getColumnIndex());

            final GridColumn<?> column = model.getColumns().get(uiColumnIndex);

            final double columnXCoordinate = rendererHelper.getColumnOffset(column) + column.getWidth() / 2;
            final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(columnXCoordinate);

            if (column instanceof ScenarioGridColumn) {
                if (!isEditableHeader(column,
                                      uiHeaderRowIndex)) {
                    return;
                }

                final GridBodyCellEditContext cellEditContext = makeRenderContext(gridWidget,
                                                                                  rendererHelper.getRenderingInformation(),
                                                                                  ci,
                                                                                  null,
                                                                                  uiHeaderRowIndex);

                final ScenarioHeaderMetaData metaData =
                        ScenarioSimulationGridHeaderUtilities.getColumnScenarioHeaderMetaData((ScenarioGridColumn) column,
                                                                                              uiHeaderRowIndex);
                if (metaData != null) {
                    metaData.edit(cellEditContext);
                }
            }
        }
    }
}
