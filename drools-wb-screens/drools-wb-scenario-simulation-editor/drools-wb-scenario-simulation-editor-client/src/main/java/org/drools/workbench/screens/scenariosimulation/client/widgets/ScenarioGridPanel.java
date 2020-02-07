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

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.event.NodeMouseOutEvent;
import com.ait.lienzo.client.core.event.NodeMouseOutHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelMouseMoveHandler;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * ScenarioGridPanel implementation of <code>GridLienzoPanel</code>.
 * <p>
 * This panel contains a <code>ScenarioGridLayer</code> and it is instantiated only once.
 * The Clicks are managed by the injected <code>ScenarioSimulationMainGridPanelClickHandler</code>
 */
@Dependent
public class ScenarioGridPanel extends GridLienzoPanel implements NodeMouseOutHandler,
                                                                  ScrollHandler {

    private ScenarioSimulationGridPanelClickHandler clickHandler;
    private ScenarioSimulationGridPanelMouseMoveHandler mouseMoveHandler;

    Set<HandlerRegistration> handlerRegistrations = new HashSet<>();

    public void addHandlers(final ScenarioSimulationGridPanelClickHandler clickHandler,
                            final ScenarioSimulationGridPanelMouseMoveHandler mouseMoveHandler) {
        this.clickHandler = clickHandler;
        this.mouseMoveHandler = mouseMoveHandler;
        unregister();
        handlerRegistrations.add(getDomElementContainer().addDomHandler(clickHandler,
                                                                        ContextMenuEvent.getType()));
        handlerRegistrations.add(getDomElementContainer().addDomHandler(clickHandler,
                                                                        ClickEvent.getType()));
        handlerRegistrations.add(getScenarioGridLayer().addNodeMouseOutHandler(this));
        handlerRegistrations.add(getScenarioGridLayer().addNodeMouseMoveHandler(mouseMoveHandler));
        handlerRegistrations.add(getScrollPanel().addDomHandler(this, ScrollEvent.getType()));
    }

    public ScenarioGridLayer getScenarioGridLayer() {
        return (ScenarioGridLayer) getDefaultGridLayer();
    }

    public ScenarioGrid getScenarioGrid() {
        return ((ScenarioGridLayer) getDefaultGridLayer()).getScenarioGrid();
    }

    public void setEventBus(EventBus eventBus) {
        getScenarioGrid().setEventBus(eventBus);
    }

    public void select() {
        getDefaultGridLayer().select(getScenarioGrid()); // This is to have "floatable" header, ie. not moving
    }

    @Override
    public void onNodeMouseOut(NodeMouseOutEvent event) {
        final int height = getScenarioGridLayer().getHeight();
        final int width = getScenarioGridLayer().getWidth();
        final int x = event.getX();
        final int y = event.getY();
        if (x < 0 || x > width || y < 0 || y > height) {
            clickHandler.hideMenus();
            mouseMoveHandler.hidePopover();
        }
    }

    @Override
    public void onScroll(ScrollEvent scrollEvent) {
        clickHandler.hideMenus();
        mouseMoveHandler.hidePopover();
    }

    @Override
    public void onResize() {
        super.onResize();
        clickHandler.hideMenus();
        mouseMoveHandler.hidePopover();
    }

    public void unregister() {
        handlerRegistrations.forEach(HandlerRegistration::removeHandler);
        handlerRegistrations.clear();
    }

    public void synchronizeFactMappingsWidths() {
        getScenarioGrid().getModel().synchronizeFactMappingsWidths();
    }

    public void ensureCellIsSelected() {
        getScenarioGrid().ensureCellIsSelected();
    }
}