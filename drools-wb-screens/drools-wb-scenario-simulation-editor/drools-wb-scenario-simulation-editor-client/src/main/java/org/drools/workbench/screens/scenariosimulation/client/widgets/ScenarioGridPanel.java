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

import com.google.gwt.event.dom.client.ContextMenuEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelContextMenuHandler;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * ScenarioGridPanel implementation of <code>GridLienzoPanel</code>.
 *
 * This panel contains a <code>ScenarioGridLayer</code> and it is instantiated only once.
 * The Right click is managed by the injected <code>ScenarioSimulationGridPanelContextMenuHandler</code>
 *
 */
public class ScenarioGridPanel extends GridLienzoPanel {

    public static final int LIENZO_PANEL_WIDTH = 1000;

    public static final int LIENZO_PANEL_HEIGHT = 450;

    // Add for testing purpose
    private ScenarioSimulationGridPanelContextMenuHandler contextMenuHandler;

    public ScenarioGridPanel(ScenarioSimulationGridPanelContextMenuHandler contextMenuHandler) {
        super(LIENZO_PANEL_WIDTH, LIENZO_PANEL_HEIGHT);
        this.contextMenuHandler = contextMenuHandler;
        getDomElementContainer().addDomHandler(contextMenuHandler,
                                               ContextMenuEvent.getType());
    }

    // Add for testing purpose
    public ScenarioSimulationGridPanelContextMenuHandler getContextMenuHandler() {
        return contextMenuHandler;
    }

    public ScenarioGrid getScenarioGrid() {
        return ((ScenarioGridLayer) getDefaultGridLayer()).getScenarioGrid();
    }


}