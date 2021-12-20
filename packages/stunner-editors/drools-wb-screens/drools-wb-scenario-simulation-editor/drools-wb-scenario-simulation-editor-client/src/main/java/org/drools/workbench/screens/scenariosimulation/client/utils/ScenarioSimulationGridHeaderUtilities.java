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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.types.Point2D;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.AbstractScesimGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static java.util.Collections.singletonList;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

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
     * Retrieve the <code>ScenarioHeaderMetaData</code> from the <code>GridColumn</code> corresponding to given row.
     * It returns <code>null</code> row with given index doesn't exist.
     * @param scenarioGridColumn
     * @param uiRowIndex
     * @return
     */
    public static ScenarioHeaderMetaData getColumnScenarioHeaderMetaData(final ScenarioGridColumn scenarioGridColumn,
                                                                         final int uiRowIndex) {
        if (scenarioGridColumn.getHeaderMetaData().size() > uiRowIndex) {
            return (ScenarioHeaderMetaData) scenarioGridColumn.getHeaderMetaData().get(uiRowIndex);
        }

        return null;
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

    /**
     * Checks whether the edit mode can be invoked on header cell from given column on given row.
     * @param column
     * @param uiHeaderRowIndex
     * @return true if conditions are met, false otherwise
     */
    public static boolean isEditableHeader(final ScenarioGridColumn column,
                                           final Integer uiHeaderRowIndex) {
        final GridColumn.HeaderMetaData headerMetaData = column.getHeaderMetaData().get(uiHeaderRowIndex);
        if (!(headerMetaData instanceof ScenarioHeaderMetaData)) {
            throw new IllegalStateException("Header metadata has to be an instance of ScenarioHeaderMetaData");
        }

        final ScenarioHeaderMetaData scenarioHeaderMetaData = (ScenarioHeaderMetaData) headerMetaData;
        if (scenarioHeaderMetaData.isEditingMode() || scenarioHeaderMetaData.isReadOnly()) {
            return false;
        }

        if (!column.isInstanceAssigned() || !column.isEditableHeaders()) {
            return false;
        }

        return scenarioHeaderMetaData.isInstanceHeader() || (scenarioHeaderMetaData.isPropertyHeader() && column.isPropertyAssigned());
    }

    public static EnableTestToolsEvent getEnableTestToolsEvent(final ScenarioGrid scenarioGrid,
                                                               final ScenarioGridColumn scenarioGridColumn,
                                                               final ScenarioHeaderMetaData clickedScenarioHeaderMetadata,
                                                               final Integer uiColumnIndex,
                                                               final String columnGroup) {
        if (!scenarioGridColumn.isInstanceAssigned()) {
            String complexSearch = getExistingInstances(columnGroup,
                                                        scenarioGrid.getType(),
                                                        scenarioGrid.getModel().getColumns());
            return new EnableTestToolsEvent(complexSearch, true);
        } else if (Objects.equals(clickedScenarioHeaderMetadata.getMetadataType(), ScenarioHeaderMetaData.MetadataType.PROPERTY)) {
            List<String> propertyNameElements = null;
            if (scenarioGridColumn.isPropertyAssigned()) {
                propertyNameElements = getPropertyNameElements(scenarioGrid.getModel(), uiColumnIndex);
            }
            return propertyNameElements != null ? new EnableTestToolsEvent(scenarioGridColumn.getInformationHeaderMetaData()
                                                                                   .getTitle(), propertyNameElements) : new EnableTestToolsEvent(scenarioGridColumn.getInformationHeaderMetaData().getTitle());
        } else {
            return new EnableTestToolsEvent(scenarioGridColumn.getInformationHeaderMetaData().getTitle(), false);
        }
    }

    public static String getExistingInstances(final String group,
                                              final ScenarioSimulationModel.Type scenarioType,
                                              final List<GridColumn<?>> columns) {
        final boolean isDMN = ScenarioSimulationModel.Type.DMN.equals(scenarioType);
        return String.join(";", columns
                .stream()
                .map(column -> (ScenarioGridColumn) column)
                .filter(scenarioGridColumn -> {
                    GridColumn.HeaderMetaData m = scenarioGridColumn.getInformationHeaderMetaData();
                    String columnGroup = ScenarioSimulationUtils.getOriginalColumnGroup(group);
                    return isDMN || columnGroup.equals(m.getColumnGroup());
                })
                .map(scenarioGridColumn -> scenarioGridColumn.getInformationHeaderMetaData().getTitle())
                .collect(Collectors.toSet()));
    }

    public static List<String> getPropertyNameElements(final AbstractScesimGridModel abstractScesimGridModel, final int columnIndex) {
        final Optional<AbstractScesimModel> optionalScesimModel = abstractScesimGridModel.getAbstractScesimModel();
        return optionalScesimModel.map(abstractScesimModel -> {
            final FactMapping factMapping = abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
            if (FactMappingValueType.EXPRESSION.equals(factMapping.getFactMappingValueType())) {
                return singletonList(ConstantHolder.EXPRESSION);
            }
            if (abstractScesimGridModel.isSimpleType(factMapping.getFactAlias()) &&
                    FactMappingValueType.NOT_EXPRESSION.equals(factMapping.getFactMappingValueType())) {
                return singletonList(VALUE);
            }
            return Collections.unmodifiableList(abstractScesimModel.getScesimModelDescriptor().getFactMappingByIndex(columnIndex).getExpressionElementsWithoutClass()
                                                        .stream()
                                                        .map(ExpressionElement::getStep)
                                                        .collect(Collectors.toList()));
        }).orElse(null);
    }
}
