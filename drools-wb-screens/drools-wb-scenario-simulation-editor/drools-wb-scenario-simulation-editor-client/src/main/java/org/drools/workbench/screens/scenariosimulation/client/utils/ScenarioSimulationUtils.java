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

import java.util.Arrays;
import java.util.List;

import org.drools.workbench.screens.scenariosimulation.client.factories.FactoryProvider;
import org.drools.workbench.screens.scenariosimulation.client.interfaces.TetraFunction;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridColumnRenderer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.TextBoxSingletonDOMElementFactory;

public class ScenarioSimulationUtils {

    private static final TetraFunction<String, String, String, TextBoxSingletonDOMElementFactory, ScenarioHeaderMetaData> SCENARIOHEADERMETADATA_FUNCTION =
            ScenarioHeaderMetaData::new;
    private static final TetraFunction<String, String, String, TextBoxSingletonDOMElementFactory, List<GridColumn.HeaderMetaData>> SCENARIOHEADERMETADATALIST_FUNCTION =
            (columnId, columnTitle, columnGroup, factory) ->
                    Arrays.asList(SCENARIOHEADERMETADATA_FUNCTION.apply(columnId, columnGroup, "", factory),
                                  SCENARIOHEADERMETADATA_FUNCTION.apply(columnId, columnTitle, columnGroup, factory));

    public static ScenarioGridColumn getScenarioGridColumn(String columnId, String columnTitle, String columnGroup, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer gridLayer) {
        TextBoxSingletonDOMElementFactory factory = FactoryProvider.getHeaderHasNameTextBoxFactory(scenarioGridPanel, gridLayer);
        return new ScenarioGridColumn(SCENARIOHEADERMETADATALIST_FUNCTION.apply(columnId, columnTitle, columnGroup, factory), new ScenarioGridColumnRenderer(), 100, false);
    }

    public static ScenarioGridColumn getScenarioGridColumn(String columnId, String columnTitle, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer gridLayer) {
        TextBoxSingletonDOMElementFactory factory = FactoryProvider.getHeaderHasNameTextBoxFactory(scenarioGridPanel, gridLayer);
        return new ScenarioGridColumn(SCENARIOHEADERMETADATA_FUNCTION.apply(columnId, columnTitle, "", factory), new ScenarioGridColumnRenderer(), 100, false);
    }
}
