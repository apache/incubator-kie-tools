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

import javax.enterprise.context.Dependent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * ScenarioGridPanel implementation of <code>GridLienzoPanel</code>.
 * <p>
 * This panel contains a <code>ScenarioGridLayer</code> and it is instantiated only once.
 * The Clicks are managed by the injected <code>ScenarioSimulationGridPanelClickHandler</code>
 */
@Dependent
public class ScenarioGridPanel extends GridLienzoPanel {

    public static final int LIENZO_PANEL_WIDTH = 1000;

    public static final int LIENZO_PANEL_HEIGHT = 800;

    private EventBus eventBus;

    public ScenarioGridPanel() {
        super(LIENZO_PANEL_WIDTH, LIENZO_PANEL_HEIGHT);
    }

    public HandlerRegistration addClickHandler(final ScenarioSimulationGridPanelClickHandler clickHandler) {
        getDomElementContainer().addDomHandler(clickHandler,
                                               ContextMenuEvent.getType());
        return RootPanel.get().addDomHandler(clickHandler, ClickEvent.getType());
    }

    public ScenarioGridLayer getScenarioGridLayer() {
        return (ScenarioGridLayer) getDefaultGridLayer();
    }

    public ScenarioGrid getScenarioGrid() {
        return ((ScenarioGridLayer) getDefaultGridLayer()).getScenarioGrid();
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        getScenarioGrid().getModel().setEventBus(eventBus);
    }
}