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

package org.drools.workbench.screens.scenariosimulation.client.menu;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.ExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.OtherContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.UnmodifiableColumnGridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;

@Dependent
public class ScenarioContextMenuRegistry {

    protected OtherContextMenu otherContextMenu;
    protected HeaderGivenContextMenu headerGivenContextMenu;
    protected HeaderExpectedContextMenu headerExpectedContextMenu;
    protected GivenContextMenu givenContextMenu;
    protected ExpectedContextMenu expectedContextMenu;
    protected GridContextMenu gridContextMenu;
    protected UnmodifiableColumnGridContextMenu unmodifiableColumnGridContextMenu;

    @Inject
    public ScenarioContextMenuRegistry(final OtherContextMenu otherContextMenu,
                                       final HeaderGivenContextMenu headerGivenContextMenu,
                                       final HeaderExpectedContextMenu headerExpectedContextMenu,
                                       final GivenContextMenu givenContextMenu,
                                       final ExpectedContextMenu expectedContextMenu,
                                       final GridContextMenu gridContextMenu,
                                       final UnmodifiableColumnGridContextMenu unmodifiableColumnGridContextMenu) {
        this.otherContextMenu = otherContextMenu;
        this.headerGivenContextMenu = headerGivenContextMenu;
        this.headerExpectedContextMenu = headerExpectedContextMenu;
        this.givenContextMenu = givenContextMenu;
        this.expectedContextMenu = expectedContextMenu;
        this.gridContextMenu = gridContextMenu;
        this.unmodifiableColumnGridContextMenu = unmodifiableColumnGridContextMenu;
    }

    public void setEventBus(final EventBus eventBus) {
        otherContextMenu.setEventBus(eventBus);
        headerGivenContextMenu.setEventBus(eventBus);
        headerExpectedContextMenu.setEventBus(eventBus);
        givenContextMenu.setEventBus(eventBus);
        expectedContextMenu.setEventBus(eventBus);
        gridContextMenu.setEventBus(eventBus);
        unmodifiableColumnGridContextMenu.setEventBus(eventBus);
    }

    public void hideMenus() {
        otherContextMenu.hide();
        headerGivenContextMenu.hide();
        headerExpectedContextMenu.hide();
        givenContextMenu.hide();
        expectedContextMenu.hide();
        gridContextMenu.hide();
        unmodifiableColumnGridContextMenu.hide();
    }

    public boolean manageRightClick(final ScenarioGrid scenarioGrid,
                                    final ContextMenuEvent event) {

        final int canvasX = CoordinateUtilities.getRelativeXOfEvent(event);
        final int canvasY = CoordinateUtilities.getRelativeYOfEvent(event);

        final Point2D gridClickPoint = CoordinateUtilities.convertDOMToGridCoordinate(scenarioGrid,
                                                                                      new Point2D(canvasX, canvasY));
        boolean isHeader = true;
        Integer uiRowIndex = CoordinateUtilities.getUiHeaderRowIndex(scenarioGrid, gridClickPoint);

        if (uiRowIndex == null) {
            uiRowIndex = CoordinateUtilities.getUiRowIndex(scenarioGrid, gridClickPoint.getY());
            isHeader = false;
        }
        if (uiRowIndex == null) {
            return false;
        }

        final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(scenarioGrid, gridClickPoint.getX());
        if (uiColumnIndex == null) {
            return false;
        }

        return manageRightClick(scenarioGrid,
                                event.getNativeEvent().getClientX(),
                                event.getNativeEvent().getClientY(),
                                uiRowIndex,
                                uiColumnIndex,
                                isHeader);
    }

    public boolean manageRightClick(final ScenarioGrid scenarioGrid,
                                    final int clientXPosition,
                                    final int clientYPosition,
                                    final Integer uiRowIndex,
                                    final Integer uiColumnIndex,
                                    final boolean isHeader) {

        ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) scenarioGrid.getModel().getColumns().get(uiColumnIndex);
        if (scenarioGridColumn == null) {
            return false;
        }
        if (isHeader) {
            return manageHeaderRightClick(scenarioGrid,
                                          clientXPosition,
                                          clientYPosition,
                                          uiRowIndex,
                                          uiColumnIndex);
        } else {
            return manageBodyRightClickLocal(scenarioGrid,
                                             clientXPosition,
                                             clientYPosition,
                                             uiRowIndex,
                                             uiColumnIndex);
        }
    }

    /**
     * This method check if the click happened on an <b>body</b> cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param left
     * @param top
     * @param uiRowIndex
     * @param uiColumnIndex
     * @return
     */
    private boolean manageBodyRightClickLocal(final ScenarioGrid scenarioGrid,
                                              final int left,
                                              final int top,
                                              final Integer uiRowIndex,
                                              final Integer uiColumnIndex) {

        if (uiRowIndex == null) {
            return false;
        }
        return manageScenarioBodyRightClick(scenarioGrid, left, top, uiRowIndex, uiColumnIndex);
    }

    /**
     * This method check if the click happened on an <b>body</b> cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param left
     * @param top
     * @param uiRowIndex
     * @param uiColumnIndex
     * @return
     */
    private boolean manageScenarioBodyRightClick(final ScenarioGrid scenarioGrid,
                                                 final int left,
                                                 final int top,
                                                 final int uiRowIndex,
                                                 final int uiColumnIndex) {
        scenarioGrid.deselect();
        ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) scenarioGrid.getModel().getColumns().get(uiColumnIndex);
        if (scenarioGridColumn == null) {
            return false;
        }
        boolean showDuplicateInstance = scenarioGrid.getModel().getSimulation().get().getSimulationDescriptor().getType().equals(ScenarioSimulationModel.Type.RULE);
        String group = scenarioGridColumn.getInformationHeaderMetaData().getColumnGroup();
        switch (group) {
            case "GIVEN":
            case "EXPECT":
                gridContextMenu.show(left, top, uiColumnIndex, uiRowIndex, group, true, showDuplicateInstance);
                break;
            default:
                unmodifiableColumnGridContextMenu.show(left, top, uiRowIndex);
        }
        return true;
    }

    /**
     * This method check if the click happened on an <b>header</b> cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param left
     * @param top
     * @param uiHeaderRowIndex - coordinates relative to the grid top left corner
     * @param uiColumnIndex
     * @return
     */
    private boolean manageHeaderRightClick(final ScenarioGrid scenarioGrid,
                                           final int left,
                                           final int top,
                                           final Integer uiHeaderRowIndex,
                                           final Integer uiColumnIndex) {
        final ScenarioGridColumn column = (ScenarioGridColumn) scenarioGrid.getModel().getColumns().get(uiColumnIndex);
        if (column == null) {
            return false;
        }
        ScenarioHeaderMetaData columnMetadata = ScenarioSimulationGridHeaderUtilities.getColumnScenarioHeaderMetaData(column, uiHeaderRowIndex);
        if (columnMetadata == null) {
            return false;
        }
        if (uiHeaderRowIndex == null) {
            return false;
        }
        boolean showDuplicateInstance = scenarioGrid.getModel().getSimulation().get().getSimulationDescriptor().getType().equals(ScenarioSimulationModel.Type.RULE);
        String group = columnMetadata.getColumnGroup();
        if (group.contains("-")) {
            group = group.substring(0, group.indexOf("-"));
        }
        switch (group) {
            case "":
                switch (columnMetadata.getTitle()) {
                    case "GIVEN":
                        headerGivenContextMenu.show(left, top);
                        break;
                    case "EXPECT":
                        headerExpectedContextMenu.show(left, top);
                        break;
                    default:
                        otherContextMenu.show(left, top);
                }
                break;
            case "GIVEN":
                givenContextMenu.show(left, top, uiColumnIndex, group, Objects.equals(columnMetadata.getMetadataType(), ScenarioHeaderMetaData.MetadataType.PROPERTY), showDuplicateInstance);
                break;
            case "EXPECT":
                expectedContextMenu.show(left, top, uiColumnIndex, group, Objects.equals(columnMetadata.getMetadataType(), ScenarioHeaderMetaData.MetadataType.PROPERTY), showDuplicateInstance);
                break;
            default:
                otherContextMenu.show(left, top);
        }
        scenarioGrid.setSelectedColumnAndHeader(uiHeaderRowIndex, uiColumnIndex);
        return true;
    }
}
