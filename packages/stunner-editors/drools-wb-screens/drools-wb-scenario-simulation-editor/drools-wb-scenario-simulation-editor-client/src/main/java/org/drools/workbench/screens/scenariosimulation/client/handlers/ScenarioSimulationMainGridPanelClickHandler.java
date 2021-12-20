/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.Dependent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.events.DisableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.getEnableTestToolsEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeXOfEvent;
import static org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities.getRelativeYOfEvent;

@Dependent
public class ScenarioSimulationMainGridPanelClickHandler extends AbstractScenarioSimulationGridPanelHandler
        implements ScenarioSimulationGridPanelClickHandler {

    protected ScenarioContextMenuRegistry scenarioContextMenuRegistry;
    protected EventBus eventBus;
    protected AtomicInteger clickReceived = new AtomicInteger(0);

    public void setScenarioContextMenuRegistry(ScenarioContextMenuRegistry scenarioContextMenuRegistry) {
        this.scenarioContextMenuRegistry = scenarioContextMenuRegistry;
    }

    /**
     * This method must be called <b>after</b> all the <i>ContextMenu</i> setters
     * @param eventBus
     */
    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onClick(ClickEvent event) {
        clickReceived.getAndIncrement();
        final int canvasX = getRelativeXOfEvent(event);
        final int canvasY = getRelativeYOfEvent(event);
        scenarioContextMenuRegistry.hideMenus();
        scenarioContextMenuRegistry.hideErrorReportPopover();
        if (!manageCoordinates(canvasX, canvasY)) { // It was not a grid click
            eventBus.fireEvent(new DisableTestToolsEvent());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onContextMenu(final ContextMenuEvent event) {
        scenarioContextMenuRegistry.hideMenus();
        if (scenarioContextMenuRegistry.manageRightClick(scenarioGrid, event)) {
            scenarioContextMenuRegistry.hideErrorReportPopover();
            event.preventDefault();
            event.stopPropagation();
        }
    }

    @Override
    public void hideMenus() {
        scenarioContextMenuRegistry.hideMenus();
    }

    /**
     * This method manage the click happened on an <i>GIVEN</i> or <i>EXPECT</i> header, starting editing it if not already did.
     * @param clickedScenarioHeaderMetadata
     * @param scenarioGridColumn
     * @param group
     * @param uiColumnIndex
     * @return
     */
    @Override
    protected boolean manageGivenExpectHeaderCoordinates(ScenarioHeaderMetaData clickedScenarioHeaderMetadata,
                                                         ScenarioGridColumn scenarioGridColumn,
                                                         String group,
                                                         Integer uiColumnIndex) {
        scenarioGrid.setSelectedColumn(uiColumnIndex);
        if (scenarioGridColumn.isInstanceAssigned() && clickedScenarioHeaderMetadata.getMetadataType().equals(ScenarioHeaderMetaData.MetadataType.INSTANCE)) {
            eventBus.fireEvent(new ReloadTestToolsEvent(true, true));
        } else {
            EnableTestToolsEvent toFire = getEnableTestToolsEvent(scenarioGrid,
                                                                  scenarioGridColumn,
                                                                  clickedScenarioHeaderMetadata,
                                                                  uiColumnIndex,
                                                                  group);
            eventBus.fireEvent(toFire);
        }
        return true;
    }

    /**
     * This method check if the click happened on an column of a <b>grid row</b>. If it is so, select the cell,
     * otherwise returns <code>false</code>
     * @param uiRowIndex
     * @param uiColumnIndex
     * @return
     */
    @Override
    protected boolean manageBodyCoordinates(Integer uiRowIndex, Integer uiColumnIndex) {
        final GridCell<?> cell = scenarioGrid.getModel().getCell(uiRowIndex, uiColumnIndex);
        if (cell == null) {
            return false;
        } else {
            scenarioGrid.getModel().selectCell(uiRowIndex, uiColumnIndex);
            eventBus.fireEvent(new DisableTestToolsEvent());
            return true;
        }
    }

}
