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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import com.ait.lienzo.client.core.shape.Layer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

/**
 * ScenarioSimulation implementation of <code>DefaultGridLayer</code>.
 * <p>
 * This layer contains a <code>ScenarioGrid</code>.
 * It also has a reference to the containing <code>ScenarioGridPanel</code> to avoid circular references by CDI
 *
 */
public class ScenarioGridLayer extends DefaultGridLayer {

    private ScenarioGridPanel scenarioGridPanel;
    private ScenarioGrid scenarioGrid;

    public ScenarioGridPanel getScenarioGridPanel() {
        return scenarioGridPanel;
    }

    public void setScenarioGridPanel(ScenarioGridPanel scenarioGridPanel) {
        this.scenarioGridPanel = scenarioGridPanel;
    }

    public ScenarioGrid getScenarioGrid() {
        return scenarioGrid;
    }

    /**
     * Add a scenarioGrid to this Layer. If the child is a GridWidget then also add
     * a Connector between the Grid Widget and any "linked" GridWidgets.
     * @param scenarioGrid ScenarioGrid to add to the Layer
     * @return The Layer
     */
    public Layer addScenarioGrid(final ScenarioGrid scenarioGrid) {
        this.scenarioGrid = scenarioGrid;
        return super.add(scenarioGrid);
    }
}