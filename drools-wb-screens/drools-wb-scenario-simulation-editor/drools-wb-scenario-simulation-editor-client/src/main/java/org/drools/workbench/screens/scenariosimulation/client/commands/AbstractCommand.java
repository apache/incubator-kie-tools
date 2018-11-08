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
package org.drools.workbench.screens.scenariosimulation.client.commands;

import java.util.Optional;

import org.drools.workbench.screens.scenariosimulation.client.factories.FactoryProvider;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.factories.FactoryProvider.getHeaderTextBoxFactory;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getHeaderBuilder;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getScenarioGridColumn;

/**
 * <b>Abstract</b> <code>Command</code> class to provide common methods used by actual implementations
 */
public abstract class AbstractCommand implements Command {

    protected ScenarioGridPanel scenarioGridPanel;
    protected ScenarioGridLayer scenarioGridLayer;

    public AbstractCommand() {
    }

    public AbstractCommand(ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer scenarioGridLayer) {
        this.scenarioGridPanel = scenarioGridPanel;
        this.scenarioGridLayer = scenarioGridLayer;
    }

    protected ScenarioHeaderTextBoxSingletonDOMElementFactory getHeaderTextBoxFactoryLocal() {
        // indirection add for test
        return getHeaderTextBoxFactory(scenarioGridPanel, scenarioGridLayer);
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
    protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle,
                                                            String propertyTitle,
                                                            String columnId,
                                                            String columnGroup,
                                                            FactMappingType factMappingType,
                                                            ScenarioGridPanel scenarioGridPanel,
                                                            ScenarioGridLayer gridLayer,
                                                            String placeHolder) {
        ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader = FactoryProvider.getHeaderTextBoxFactory(scenarioGridPanel, gridLayer);
        ScenarioSimulationBuilders.HeaderBuilder headerBuilder = getHeaderBuilder(instanceTitle, propertyTitle, columnId, columnGroup, factMappingType, factoryHeader);
        return getScenarioGridColumn(headerBuilder, scenarioGridPanel, gridLayer, placeHolder);
    }

    protected ScenarioGridColumn getScenarioGridColumnLocal(ScenarioSimulationBuilders.HeaderBuilder headerBuilder) {
        // indirection add for test
        return getScenarioGridColumn(headerBuilder, scenarioGridPanel, scenarioGridLayer, ScenarioSimulationEditorConstants.INSTANCE.insertValue());
    }

    protected Optional<FactIdentifier> getFactIdentifierByColumnTitle(String columnTitle) {
        return scenarioGridLayer.getScenarioGrid().getModel().getColumns().stream()
                .filter(column -> columnTitle.equals(((ScenarioGridColumn) column).getInformationHeaderMetaData().getTitle()))
                .findFirst()
                .map(column -> ((ScenarioGridColumn) column).getFactIdentifier());
    }
}
