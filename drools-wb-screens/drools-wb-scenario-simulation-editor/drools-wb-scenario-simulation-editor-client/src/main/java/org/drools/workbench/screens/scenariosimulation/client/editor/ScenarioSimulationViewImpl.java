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

package org.drools.workbench.screens.scenariosimulation.client.editor;

import java.util.Map;

import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;


/**
 * Implementation of the main view for the ScenarioSimulation editor.
 *
 * This view contains a <code>ScenarioGridPanel</code>.
 *
 */
public class ScenarioSimulationViewImpl
        extends KieEditorViewImpl
        implements ScenarioSimulationView {

    private ScenarioGridPanel scenarioGridPanel;

    public ScenarioSimulationViewImpl(ScenarioGridPanel scenarioGridPanel) {
        this.scenarioGridPanel = scenarioGridPanel;
        initWidget(scenarioGridPanel);
    }

    @Override
    public void setContent(Map<Integer, String> headersMap, Map<Integer, Map<Integer, String>> rowsMap) {
        scenarioGridPanel.getScenarioGrid().setContent(headersMap, rowsMap);
    }

    @Override
    public ScenarioGridPanel getScenarioGridPanel() {
        return scenarioGridPanel;
    }
}