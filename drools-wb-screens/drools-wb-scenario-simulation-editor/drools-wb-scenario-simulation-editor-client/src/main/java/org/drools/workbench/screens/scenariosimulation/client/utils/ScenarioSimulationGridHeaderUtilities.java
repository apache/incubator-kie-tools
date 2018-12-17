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

package org.drools.workbench.screens.scenariosimulation.client.utils;

import java.util.Optional;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.types.Point2D;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionElement;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class ScenarioSimulationGridHeaderUtilities {

    /**
     * Retrieve the  <code>ScenarioHeaderMetaData</code> from the <code>GridColumn</code> of a <code>GridWidget</code> at a given point x.
     * It returns <code>null</code> if none is present at that position.
     * @param gridWidget
     * @param relativePoint within the gridWidget
     * @return
     */
    public static ScenarioHeaderMetaData getColumnScenarioHeaderMetaData(GridWidget gridWidget, Point2D relativePoint) {
        final GridColumn<?> column = getGridColumn(gridWidget, relativePoint.getX());
        if (column == null) {
            return null;
        }
        //Get row index
        final Integer uiHeaderRowIndex = CoordinateUtilities.getUiHeaderRowIndex(gridWidget,
                                                                                 relativePoint);
        if (uiHeaderRowIndex == null) {
            return null;
        }
        return (ScenarioHeaderMetaData) column.getHeaderMetaData().get(uiHeaderRowIndex);
    }

    /**
     * Retrieve the <code>GridColumn</code> of a <code>GridWidget</code> at a given point x.
     * It returns <code>null</code> if none is present at that position.
     * @param gridWidget
     * @param cx
     * @return
     */
    public static GridColumn<?> getGridColumn(GridWidget gridWidget, double cx) {
        //Get column information
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
        if (ri == null) {
            return null;
        }
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(cx);
        return ci.getColumn();
    }

    public static boolean hasEditableHeader(final GridColumn<?> column) {
        return column.getHeaderMetaData().stream().anyMatch(md -> md instanceof ScenarioHeaderMetaData);
    }

    public static boolean isEditableHeader(final GridColumn<?> column,
                                           final Integer uiHeaderRowIndex) {
        GridColumn.HeaderMetaData headerMetaData = column.getHeaderMetaData().get(uiHeaderRowIndex);
        return headerMetaData instanceof ScenarioHeaderMetaData && !((ScenarioHeaderMetaData) headerMetaData).isReadOnly();
    }

    public static EnableRightPanelEvent getEnableRightPanelEvent(final ScenarioGrid scenarioGrid,
                                                                 final ScenarioGridColumn scenarioGridColumn,
                                                                 final ScenarioHeaderMetaData clickedScenarioHeaderMetadata,
                                                                 final Integer uiColumnIndex,
                                                                 final String columnGroup) {
        if (!scenarioGridColumn.isInstanceAssigned()) {
            String complexSearch = getExistingInstances(columnGroup, scenarioGrid.getModel());
            return new EnableRightPanelEvent(complexSearch, true);
        } else if (clickedScenarioHeaderMetadata.isPropertyHeader()) {
            String propertyName = null;
            if (scenarioGridColumn.isPropertyAssigned()) {
                final Optional<Simulation> optionalSimulation = scenarioGrid.getModel().getSimulation();
                propertyName = optionalSimulation.map(simulation -> getPropertyName(simulation, uiColumnIndex)).orElse(null);
            }
            return propertyName != null ? new EnableRightPanelEvent(scenarioGridColumn.getInformationHeaderMetaData()
                                                                            .getTitle(), propertyName) : new EnableRightPanelEvent(scenarioGridColumn.getInformationHeaderMetaData().getTitle());
        } else {
            String complexSearch = getExistingInstances(columnGroup, scenarioGrid.getModel());
            return new EnableRightPanelEvent(complexSearch, true);
        }
    }

    public static String getExistingInstances(final String group, final ScenarioGridModel scenarioGridModel) {
        return String.join(";", scenarioGridModel.getColumns()
                .stream()
                .filter(gridColumn -> {

                    GridColumn.HeaderMetaData m = ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData();
                    return group.equals(m.getColumnGroup());
                })
                .map(gridColumn -> ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getTitle())
                .collect(Collectors.toSet()));
    }

    public static String getPropertyName(final Simulation simulation, final int columnIndex) {
        return String.join(".", simulation.getSimulationDescriptor().getFactMappingByIndex(columnIndex).getExpressionElements()
                .stream()
                .map(ExpressionElement::getStep)
                .collect(Collectors.toSet()));
    }
}
