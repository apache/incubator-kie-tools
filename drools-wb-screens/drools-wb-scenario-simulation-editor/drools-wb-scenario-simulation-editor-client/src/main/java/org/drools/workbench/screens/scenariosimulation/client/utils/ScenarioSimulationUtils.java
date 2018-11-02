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

import org.drools.workbench.screens.scenariosimulation.client.factories.FactoryProvider;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;

public class ScenarioSimulationUtils {

    /**
     * Returns a <code>ScenarioGridColumn</code> with the following default values:
     * <p>
     * width: 150
     * </p>
     * <p>
     * isMovable: <code>false</code>;
     * </p>
     * <p>
     * isPropertyAssigned: <code>false</code>;
     * </p>
     * <p>
     * placeHolder: <code>ScenarioSimulationEditorConstants.INSTANCE.insertValue()</code>;
     * </p>
     * <p>
     * columnRenderer: new ScenarioGridColumnRenderer()
     * </p>
     * @param instanceTitle
     * @param propertyTitle
     * @param columnId
     * @param columnGroup
     * @param factMappingType
     * @param scenarioGridPanel
     * @param gridLayer
     * @return
     */
    public static ScenarioGridColumn getScenarioGridColumn(String instanceTitle,
                                                           String propertyTitle,
                                                           String columnId,
                                                           String columnGroup,
                                                           FactMappingType factMappingType,
                                                           ScenarioGridPanel scenarioGridPanel,
                                                           ScenarioGridLayer gridLayer) {
        ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader = FactoryProvider.getHeaderTextBoxFactory(scenarioGridPanel, gridLayer);
        ScenarioSimulationBuilders.HeaderBuilder headerBuilder = getHeaderBuilder(instanceTitle, propertyTitle, columnId, columnGroup, factMappingType, factoryHeader);
        return getScenarioGridColumn(headerBuilder, scenarioGridPanel, gridLayer);
    }

    /**
     * Returns a <code>ScenarioGridColumn</code> with the following default values:
     * <p>
     * width: 150
     * </p>
     * <p>
     * isMovable: <code>false</code>;
     * </p>
     * <p>
     * isPropertyAssigned: <code>false</code>;
     * </p>
     * <p>
     * columnRenderer: new ScenarioGridColumnRenderer()
     * </p>
     * @param instanceTitle
     * @param propertyTitle
     * @param columnId
     * @param columnGroup
     * @param factMappingType
     * @param scenarioGridPanel
     * @param gridLayer
     * @return
     */
    public static ScenarioGridColumn getScenarioGridColumn(String instanceTitle,
                                                           String propertyTitle,
                                                           String columnId,
                                                           String columnGroup,
                                                           FactMappingType factMappingType,
                                                           ScenarioGridPanel scenarioGridPanel,
                                                           ScenarioGridLayer gridLayer,
                                                           String placeHolder) {
        ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader = FactoryProvider.getHeaderTextBoxFactory(scenarioGridPanel, gridLayer);
        ScenarioSimulationBuilders.HeaderBuilder headerBuilder = getHeaderBuilder(instanceTitle, propertyTitle, columnId, columnGroup, factMappingType, factoryHeader);
        return getScenarioGridColumn(headerBuilder, scenarioGridPanel, gridLayer, false, placeHolder);
    }

    /**
     * Returns a <code>ScenarioGridColumn</code> with the following default values:
     * <p>
     * width: 150
     * </p>
     * <p>
     * isMovable: <code>false</code>;
     * </p>
     * <p>
     * isPropertyAssigned: <code>true</code>;
     * </p>
     * <p>
     * placeHolder: <code>ScenarioSimulationEditorConstants.INSTANCE.insertValue()</code>;
     * </p>
     * <p>
     * columnRenderer: new ScenarioGridColumnRenderer()
     * </p>
     * @param headerBuilder
     * @param scenarioGridPanel
     * @param gridLayer
     * @return
     */
    public static ScenarioGridColumn getScenarioGridColumn(ScenarioSimulationBuilders.HeaderBuilder headerBuilder,
                                                           ScenarioGridPanel scenarioGridPanel,
                                                           ScenarioGridLayer gridLayer) {
        ScenarioCellTextAreaSingletonDOMElementFactory factoryCell = FactoryProvider.getCellTextBoxFactory(scenarioGridPanel, gridLayer);
        ScenarioSimulationBuilders.ScenarioGridColumnBuilder scenarioGridColumnBuilder = getScenarioGridColumnBuilder(factoryCell,
                                                                                                                      headerBuilder,
                                                                                                                      ScenarioSimulationEditorConstants.INSTANCE.insertValue());
        return scenarioGridColumnBuilder.build();
    }

    /**
     * Returns a <code>ScenarioGridColumn</code> with the following default values:
     * <p>
     * width: 150
     * </p>
     * <p>
     * isMovable: <code>false</code>;
     * </p>
     * <p>
     * columnRenderer: new ScenarioGridColumnRenderer()
     * </p>
     * @param headerBuilder
     * @param scenarioGridPanel
     * @param gridLayer
     * @param readOnly
     * @param placeHolder
     * @return
     */
    public static ScenarioGridColumn getScenarioGridColumn(ScenarioSimulationBuilders.HeaderBuilder headerBuilder,
                                                           ScenarioGridPanel scenarioGridPanel,
                                                           ScenarioGridLayer gridLayer,
                                                           boolean readOnly,
                                                           String placeHolder) {
        ScenarioCellTextAreaSingletonDOMElementFactory factoryCell = FactoryProvider.getCellTextBoxFactory(scenarioGridPanel, gridLayer);
        ScenarioSimulationBuilders.ScenarioGridColumnBuilder scenarioGridColumnBuilder = getScenarioGridColumnBuilder(factoryCell,
                                                                                                                      headerBuilder,
                                                                                                                      placeHolder);
        scenarioGridColumnBuilder.setReadOnly(readOnly);
        return scenarioGridColumnBuilder.build();
    }

    /**
     * Returns a <code>ScenarioSimulationBuilders.ScenarioGridColumnBuilder</code> with the following default values:
     * <p>
     * width: 150
     * </p>
     * <p>
     * isMovable: <code>false</code>;
     * </p>
     * <p>
     * isPropertyAssigned: <code>false</code>;
     * </p>
     * <p>
     * columnRenderer: new ScenarioGridColumnRenderer()
     * </p>
     * @param factoryCell
     * @param headerBuilder
     * @param placeHolder
     * @return
     */
    public static ScenarioSimulationBuilders.ScenarioGridColumnBuilder getScenarioGridColumnBuilder(ScenarioCellTextAreaSingletonDOMElementFactory factoryCell,
                                                                                                    ScenarioSimulationBuilders.HeaderBuilder headerBuilder,
                                                                                                    String placeHolder) {
        ScenarioSimulationBuilders.ScenarioGridColumnBuilder toReturn = ScenarioSimulationBuilders.ScenarioGridColumnBuilder.get(factoryCell, headerBuilder);
        toReturn.setPlaceHolder(placeHolder);
        toReturn.setWidth(getColumnWidth(headerBuilder.getColumnId()));
        return toReturn;
    }

    /**
     * Retrieve a <b>single</b> or <b>triple</b> level Header metadata, i.e. a <code>List&lt;GridColumn.HeaderMetaData&gt;</code> with one  or three elements,
     * depending on the column <b>Group</b>:
     * <p>
     * OTHER: single level
     * </p>
     * <p>
     * EXPECTED/GIVEN: triple level
     * </p>
     * @param instanceTitle
     * @param propertyTitle
     * @param columnId
     * @param columnGroup
     * @param factMappingType
     * @param factoryHeader
     * @return
     */
    public static ScenarioSimulationBuilders.HeaderBuilder getHeaderBuilder(String instanceTitle,
                                                                            String propertyTitle,
                                                                            String columnId,
                                                                            String columnGroup,
                                                                            FactMappingType factMappingType,
                                                                            ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader) {

        ScenarioSimulationBuilders.HeaderBuilder headerBuilder = ScenarioSimulationBuilders.HeaderBuilder.get(factoryHeader);

        headerBuilder.setColumnId(columnId);

        headerBuilder.setColumnTitle(columnGroup);
        headerBuilder.setReadOnly(true);

        if (isOther(factMappingType)) {
            headerBuilder.setColumnTitle(instanceTitle);
            headerBuilder.setColumnGroup(columnGroup);
            headerBuilder.setInstanceHeader(true);
            return headerBuilder;
        }

        // The "instance" header
        final ScenarioSimulationBuilders.HeaderBuilder instanceHeader = headerBuilder.newLevel()
                .setColumnTitle(instanceTitle)
                .setColumnGroup(columnGroup)
                .setReadOnly(false)
                .setInstanceHeader(true)
                .setPropertyHeader(false);

        // The "property" header
        instanceHeader.newLevel()
                .setColumnTitle(propertyTitle)
                .setColumnGroup(columnGroup)
                .setReadOnly(false)
                .setInstanceHeader(false)
                .setPropertyHeader(true);

        return headerBuilder;
    }

    protected static double getColumnWidth(String columnId) {
        ExpressionIdentifier.NAME expressionName = ExpressionIdentifier.NAME.Other;
        try {
            expressionName = ExpressionIdentifier.NAME.valueOf(columnId);
        } catch (IllegalArgumentException e) {
            // ColumnId not recognized
        }
        switch (expressionName) {
            case Index:
                return 70;
            case Description:
                return 300;
            default:
                return 200;
        }
    }

    private static boolean isOther(FactMappingType factMappingType) {
        return FactMappingType.OTHER.equals(factMappingType);
    }

    private static boolean isExpected(FactMappingType factMappingType) {
        return FactMappingType.EXPECTED.equals(factMappingType);
    }

    private static boolean isGiven(FactMappingType factMappingType) {
        return FactMappingType.GIVEN.equals(factMappingType);
    }
}
