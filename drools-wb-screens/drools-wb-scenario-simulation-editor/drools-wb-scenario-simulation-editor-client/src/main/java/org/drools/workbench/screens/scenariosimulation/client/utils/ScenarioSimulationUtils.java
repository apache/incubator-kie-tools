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

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.screens.scenariosimulation.client.factories.FactoryProvider;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridColumnRenderer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

public class ScenarioSimulationUtils {

    public static ScenarioGridColumn getScenarioGridColumn(String title,
                                                           String columnId,
                                                           String columnGroup,
                                                           FactMappingType factMappingType,
                                                           ScenarioGridPanel scenarioGridPanel,
                                                           ScenarioGridLayer gridLayer) {
        ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader = FactoryProvider.getHeaderTextBoxFactory(scenarioGridPanel, gridLayer);
        ScenarioCellTextBoxSingletonDOMElementFactory factoryCell = FactoryProvider.getCellTextBoxFactory(scenarioGridPanel, gridLayer);
        return new ScenarioGridColumn(getTwoLevelHeaderBuilder(title, columnId, columnGroup, factMappingType).build(factoryHeader), new ScenarioGridColumnRenderer(), 150, false, factoryCell);
    }

    public static ColumnBuilder getTwoLevelHeaderBuilder(String title,
                                                         String columnId,
                                                         String columnGroup,
                                                         FactMappingType factMappingType) {

        ColumnBuilder columnBuilder = ColumnBuilder.get();

        columnBuilder.setColumnId(columnId);

        columnBuilder.setColumnTitle(columnGroup);
        columnBuilder.setReadOnly(true);

        boolean informationHeader = isOther(factMappingType) || isExpected(factMappingType) || isGiven(factMappingType);
        if (isOther(factMappingType)) {
            columnBuilder.setColumnTitle(title);
            columnBuilder.setColumnGroup(columnGroup);
            columnBuilder.setInformationHeader(informationHeader);
            return columnBuilder;
        }

        columnBuilder.newLevel()
                .setColumnTitle(title)
                .setColumnGroup(columnGroup)
                .setReadOnly(false)
                .setInformationHeader(informationHeader);

        return columnBuilder;
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

    public static class ColumnBuilder {

        String columnId;
        String columnTitle;
        String columnGroup = "";
        boolean readOnly = false;
        boolean informationHeader = false;
        ColumnBuilder nestedLevel;

        public static ColumnBuilder get() {
            return new ColumnBuilder();
        }

        public ColumnBuilder setColumnId(String columnId) {
            this.columnId = columnId;
            return this;
        }

        public ColumnBuilder setColumnTitle(String columnTitle) {
            this.columnTitle = columnTitle;
            return this;
        }

        public ColumnBuilder setColumnGroup(String columnGroup) {
            this.columnGroup = columnGroup;
            return this;
        }

        public ColumnBuilder setReadOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public ColumnBuilder setInformationHeader(boolean informationHeader) {
            this.informationHeader = informationHeader;
            return this;
        }

        public ColumnBuilder newLevel() {
            this.nestedLevel = ColumnBuilder.get()
                    .setColumnId(columnId)
                    .setColumnTitle(columnTitle)
                    .setColumnGroup(columnGroup)
                    .setReadOnly(readOnly);
            return this.nestedLevel;
        }

        public List<GridColumn.HeaderMetaData> build(ScenarioHeaderTextBoxSingletonDOMElementFactory factory) {
            List<GridColumn.HeaderMetaData> toReturn = new ArrayList<>();
            ColumnBuilder current = this;
            do {
                toReturn.add(current.internalBuild(factory));
                current = current.nestedLevel;
            } while (current != null);
            return toReturn;
        }

        private GridColumn.HeaderMetaData internalBuild(ScenarioHeaderTextBoxSingletonDOMElementFactory factory) {
            return new ScenarioHeaderMetaData(columnId, columnTitle, columnGroup, factory, readOnly, informationHeader);
        }
    }
}
