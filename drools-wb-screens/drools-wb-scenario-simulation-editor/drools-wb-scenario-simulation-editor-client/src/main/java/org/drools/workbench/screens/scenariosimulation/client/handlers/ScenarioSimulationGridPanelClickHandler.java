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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.AbstractHeaderMenuPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.BaseMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.ExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.OtherContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.UnmodifiableColumnGridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionElement;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.getColumnScenarioHeaderMetaData;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationGridHeaderUtilities.getUiHeaderRowIndex;

@Dependent
public class ScenarioSimulationGridPanelClickHandler implements ClickHandler,
                                                                ContextMenuHandler {

    ScenarioGrid scenarioGrid;
    OtherContextMenu otherContextMenu;
    HeaderGivenContextMenu headerGivenContextMenu;
    HeaderExpectedContextMenu headerExpectedContextMenu;
    GivenContextMenu givenContextMenu;
    ExpectedContextMenu expectedContextMenu;
    GridContextMenu gridContextMenu;
    UnmodifiableColumnGridContextMenu unmodifiableColumnGridContextMenu;
    Set<AbstractHeaderMenuPresenter> managedMenus = new HashSet<>();
    EventBus eventBus;
    AtomicInteger clickReceived = new AtomicInteger(0);
    BaseGridRendererHelper rendererHelper;

    public ScenarioSimulationGridPanelClickHandler() {
    }

    public void setScenarioGrid(ScenarioGrid scenarioGrid) {
        this.scenarioGrid = scenarioGrid;
        this.rendererHelper = scenarioGrid.getRendererHelper();
    }

    public void setOtherContextMenu(OtherContextMenu otherContextMenu) {
        this.otherContextMenu = otherContextMenu;
        managedMenus.add(otherContextMenu);
    }

    public void setHeaderGivenContextMenu(HeaderGivenContextMenu headerGivenContextMenu) {
        this.headerGivenContextMenu = headerGivenContextMenu;
        managedMenus.add(headerGivenContextMenu);
    }

    public void setHeaderExpectedContextMenu(HeaderExpectedContextMenu headerExpectedContextMenu) {
        this.headerExpectedContextMenu = headerExpectedContextMenu;
        managedMenus.add(headerExpectedContextMenu);
    }

    public void setGivenContextMenu(GivenContextMenu givenContextMenu) {
        this.givenContextMenu = givenContextMenu;
        managedMenus.add(givenContextMenu);
    }

    public void setExpectedContextMenu(ExpectedContextMenu expectedContextMenu) {
        this.expectedContextMenu = expectedContextMenu;
        managedMenus.add(expectedContextMenu);
    }

    public void setGridContextMenu(GridContextMenu gridContextMenu) {
        this.gridContextMenu = gridContextMenu;
        managedMenus.add(gridContextMenu);
    }

    public void setUnmodifiableColumnGridContextMenu(UnmodifiableColumnGridContextMenu unmodifiableColumnGridContextMenu) {
        this.unmodifiableColumnGridContextMenu = unmodifiableColumnGridContextMenu;
        managedMenus.add(unmodifiableColumnGridContextMenu);
    }

    /**
     * This method must be called <b>after</b> all the <i>ContextMenu</i> setters
     * @param eventBus
     */
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        managedMenus.forEach(menu -> menu.setEventBus(eventBus));
    }

    @Override
    public void onClick(ClickEvent event) {
        clickReceived.getAndIncrement();
        final int canvasX = getRelativeX(event);
        final int canvasY = getRelativeY(event);
        final boolean isShiftKeyDown = event.getNativeEvent().getShiftKey();
        final boolean isControlKeyDown = event.getNativeEvent().getCtrlKey();
        hideMenus();
        scenarioGrid.clearSelections();
        if (!manageLeftClick(canvasX, canvasY, isShiftKeyDown, isControlKeyDown)) { // It was not a grid click
            eventBus.fireEvent(new ReloadRightPanelEvent(true));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onContextMenu(final ContextMenuEvent event) {
        hideMenus();
        if (manageRightClick(event)) {
            event.preventDefault();
            event.stopPropagation();
        }
    }

    protected boolean manageRightClick(final ContextMenuEvent event) {
        final int canvasX = getRelativeX(event);
        final int canvasY = getRelativeY(event);
        final boolean isShiftKeyDown = event.getNativeEvent().getShiftKey();
        final boolean isControlKeyDown = event.getNativeEvent().getCtrlKey();
        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(scenarioGrid,
                                                                          new Point2D(canvasX,
                                                                                      canvasY));
        final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(scenarioGrid,
                                                                           ap.getX());
        if (uiColumnIndex == null) {
            return false;
        }
        if (!manageHeaderRightClick(scenarioGrid, event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY(), ap.getY(), uiColumnIndex)) {
            return manageBodyRightClick(scenarioGrid, event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY(), ap.getY(), uiColumnIndex, isShiftKeyDown, isControlKeyDown);
        } else {
            return true;
        }
    }

    protected int getRelativeX(final ContextMenuEvent event) {
        final NativeEvent e = event.getNativeEvent();
        final Element target = event.getRelativeElement();
        return e.getClientX() - target.getAbsoluteLeft() + target.getScrollLeft() + target.getOwnerDocument().getScrollLeft();
    }

    protected int getRelativeY(final ContextMenuEvent event) {
        final NativeEvent e = event.getNativeEvent();
        final Element target = event.getRelativeElement();
        return e.getClientY() - target.getAbsoluteTop() + target.getScrollTop() + target.getOwnerDocument().getScrollTop();
    }

    protected int getRelativeX(final ClickEvent event) {
        final NativeEvent e = event.getNativeEvent();
        final Element target = event.getRelativeElement();
        return e.getClientX() - target.getAbsoluteLeft() + target.getScrollLeft() + target.getOwnerDocument().getScrollLeft();
    }

    protected int getRelativeY(final ClickEvent event) {
        final NativeEvent e = event.getNativeEvent();
        final Element target = event.getRelativeElement();
        return e.getClientY() - target.getAbsoluteTop() + target.getScrollTop() + target.getOwnerDocument().getScrollTop();
    }

    public void hideMenus() {
        managedMenus.forEach(BaseMenu::hide);
    }

    /**
     * This method check if the click happened on an <b>header</b> cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param left
     * @param top
     * @param gridY
     * @param uiColumnIndex
     * @return
     */
    protected boolean manageHeaderRightClick(ScenarioGrid scenarioGrid, int left, int top, double gridY, Integer uiColumnIndex) {
        final GridColumn<?> scenarioGridColumn = scenarioGrid.getModel().getColumns().get(uiColumnIndex);
        ScenarioHeaderMetaData columnMetadata = getColumnScenarioHeaderMetaDataLocal(scenarioGrid, scenarioGridColumn, gridY);
        if (columnMetadata == null) {
            return false;
        }
        //Get row index
        final Integer uiHeaderRowIndex = getUiHeaderRowIndexLocal(scenarioGrid, scenarioGridColumn, gridY);
        if (uiHeaderRowIndex == null) {
            return false;
        }
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
                givenContextMenu.show(left, top, uiColumnIndex, group, columnMetadata.isPropertyHeader());
                break;
            case "EXPECT":
                expectedContextMenu.show(left, top, uiColumnIndex, group, columnMetadata.isPropertyHeader());
                break;
            default:
                otherContextMenu.show(left, top);
        }
        scenarioGrid.setSelectedColumnAndHeader(uiHeaderRowIndex, uiColumnIndex);
        return true;
    }

    /**
     * This method check if the click happened on an <b>body</b> cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param left
     * @param top
     * @param gridY
     * @param uiColumnIndex
     * @param isShiftKeyDown
     * @param isControlKeyDown
     * @return
     */
    protected boolean manageBodyRightClick(ScenarioGrid scenarioGrid, int left, int top, double gridY, Integer uiColumnIndex, boolean isShiftKeyDown, boolean isControlKeyDown) {
        scenarioGrid.deselect();
        final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(scenarioGrid, gridY);
        if (uiRowIndex == null) {
            return false;
        }
        ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) scenarioGrid.getModel().getColumns().get(uiColumnIndex);
        if (scenarioGridColumn == null) {
            return false;
        }
        String group = scenarioGridColumn.getInformationHeaderMetaData().getColumnGroup();
        switch (group) {
            case "GIVEN":
            case "EXPECT":
                gridContextMenu.show(left, top, uiColumnIndex, uiRowIndex, group, true);
                break;
            default:
                unmodifiableColumnGridContextMenu.show(left, top, uiRowIndex);
        }
        return true;
    }

    /**
     * @param canvasX
     * @param canvasY
     * @param isShiftKeyDown
     * @param isControlKeyDown
     * @return
     */
    protected boolean manageLeftClick(final int canvasX, final int canvasY, final boolean isShiftKeyDown, final boolean isControlKeyDown) {
        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(scenarioGrid,
                                                                          new Point2D(canvasX,
                                                                                      canvasY));
        final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex(scenarioGrid,
                                                                           ap.getX());
        if (uiColumnIndex == null) {
            return false;
        }
        ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) scenarioGrid.getModel().getColumns().get(uiColumnIndex);
        if (scenarioGridColumn == null) {
            return false;
        }

        if (!manageHeaderLeftClick(scenarioGrid, uiColumnIndex, scenarioGridColumn, ap)) {
            final Integer uiRowIndex = CoordinateUtilities.getUiRowIndex(scenarioGrid, ap.getY());
            if (uiRowIndex == null) {
                return false;
            } else {
                return manageGridLeftClick(scenarioGrid, uiRowIndex, uiColumnIndex, scenarioGridColumn);
            }
        } else {
            return true;
        }
    }

    /**
     * This method check if the click happened on an <b>second level header</b> (i.e. the header of a specific column) cell. If it is so, manage it and returns <code>true</code>,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param uiColumnIndex
     * @param scenarioGridColumn
     * @param rp
     * @return
     */
    protected boolean manageHeaderLeftClick(ScenarioGrid scenarioGrid, Integer uiColumnIndex, ScenarioGridColumn scenarioGridColumn, Point2D rp) {
        double gridY = rp.getY();
        if (!hasEditableHeaderLocal(scenarioGridColumn)) {
            return false;
        }
        //Get row index
        final Integer uiHeaderRowIndex = getUiHeaderRowIndexLocal(scenarioGrid, scenarioGridColumn, gridY);
        if (uiHeaderRowIndex == null) {
            return false;
        }
        if (!isEditableHeaderLocal(scenarioGridColumn, uiHeaderRowIndex)) {
            return false;
        }
        ScenarioHeaderMetaData clickedScenarioHeaderMetadata = getColumnScenarioHeaderMetaDataLocal(scenarioGrid, scenarioGridColumn, gridY);
        if (clickedScenarioHeaderMetadata == null) {
            return false;
        }
        String group = clickedScenarioHeaderMetadata.getColumnGroup();
        if (group.contains("-")) {
            group = group.substring(0, group.indexOf("-"));
        }
        switch (group) {
            case "GIVEN":
            case "EXPECT":
                return manageGivenExpectHeaderLeftClick(clickedScenarioHeaderMetadata, scenarioGridColumn, group, uiColumnIndex, uiHeaderRowIndex, rp);
            default:
                return false;
        }
    }

    /**
     * This method manage the click happened on an <i>GIVEN</i> or <i>EXPECT</i> header, starting editing it if not already did.
     *
     * @param clickedScenarioHeaderMetadata
     * @param scenarioGridColumn
     * @param group
     * @param uiColumnIndex
     * @param uiHeaderRowIndex
     * @param rp
     * @return
     */
    protected boolean manageGivenExpectHeaderLeftClick(ScenarioHeaderMetaData clickedScenarioHeaderMetadata, ScenarioGridColumn scenarioGridColumn, String group, Integer uiColumnIndex, Integer uiHeaderRowIndex, Point2D rp) {
        if (rendererHelper != null && !clickedScenarioHeaderMetadata.isEditingMode()) {
            final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
            final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(rp.getX());
            final GridBodyCellEditContext context = ScenarioSimulationGridHeaderUtilities.makeRenderContext(scenarioGrid,
                                                                                                            ri,
                                                                                                            ci,
                                                                                                            rp, uiHeaderRowIndex);
            clickedScenarioHeaderMetadata.edit(context);
        }
        if (scenarioGridColumn.isInstanceAssigned() && clickedScenarioHeaderMetadata.isInstanceHeader()) {
            eventBus.fireEvent(new ReloadRightPanelEvent(true, true));
            return true;
        }
        scenarioGrid.setSelectedColumnAndHeader(scenarioGridColumn.getHeaderMetaData().indexOf(clickedScenarioHeaderMetadata), uiColumnIndex);
        EnableRightPanelEvent toFire;
        if (!scenarioGridColumn.isInstanceAssigned()) {
            String complexSearch = getExistingInstances(group, scenarioGrid.getModel());
            toFire = new EnableRightPanelEvent(complexSearch, true);
        } else if (clickedScenarioHeaderMetadata.isPropertyHeader()) {
            String propertyName = null;
            if (scenarioGridColumn.isPropertyAssigned()) {
                final Optional<Simulation> optionalSimulation = scenarioGrid.getModel().getSimulation();
                propertyName = optionalSimulation.map(simulation -> getPropertyName(simulation, uiColumnIndex)).orElse(null);
            }
            toFire = propertyName != null ? new EnableRightPanelEvent(scenarioGridColumn.getInformationHeaderMetaData().getTitle(), propertyName) : new EnableRightPanelEvent(scenarioGridColumn.getInformationHeaderMetaData().getTitle());
        } else {
            String complexSearch = getExistingInstances(group, scenarioGrid.getModel());
            toFire = new EnableRightPanelEvent(complexSearch, true);
        }
        eventBus.fireEvent(toFire);
        return true;
    }

    /**
     * This method check if the click happened on an <i>writable</i> column of a <b>grid row</b>. If it is so, start editing the cell,
     * otherwise returns <code>false</code>
     * @param scenarioGrid
     * @param uiRowIndex
     * @param uiColumnIndex
     * @param scenarioGridColumn
     * @return
     */
    protected boolean manageGridLeftClick(ScenarioGrid scenarioGrid, Integer uiRowIndex, Integer uiColumnIndex, ScenarioGridColumn scenarioGridColumn) {
        final GridCell<?> cell = scenarioGrid.getModel().getCell(uiRowIndex, uiColumnIndex);
        if (cell == null) {
            return false;
        }
        if (((ScenarioGridCell) cell).isEditingMode()) {
            return true;
        }
        ((ScenarioGridCell) cell).setEditingMode((!scenarioGridColumn.isReadOnly()) && scenarioGrid.startEditingCell(uiRowIndex, uiColumnIndex));
        return ((ScenarioGridCell) cell).isEditingMode();
    }

    protected String getExistingInstances(String group, ScenarioGridModel scenarioGridModel) {
        return String.join(";", scenarioGridModel.getColumns()
                .stream()
                .filter(gridColumn -> group.equals(((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getColumnGroup()))
                .map(gridColumn -> ((ScenarioGridColumn) gridColumn).getInformationHeaderMetaData().getTitle())
                .collect(Collectors.toSet()));
    }

    protected String getPropertyName(Simulation simulation, int columnIndex) {
        return String.join(".", simulation.getSimulationDescriptor().getFactMappingByIndex(columnIndex).getExpressionElements()
                .stream()
                .map(ExpressionElement::getStep)
                .collect(Collectors.toSet()));
    }

    // Indirection add for test
    protected ScenarioHeaderMetaData getColumnScenarioHeaderMetaDataLocal(ScenarioGrid scenarioGrid, GridColumn<?> scenarioGridColumn, double gridY) {
        return getColumnScenarioHeaderMetaData(scenarioGrid, scenarioGridColumn, gridY);
    }

    // Indirection add for test
    protected Integer getUiHeaderRowIndexLocal(ScenarioGrid scenarioGrid, GridColumn<?> scenarioGridColumn, double gridY) {
        return getUiHeaderRowIndex(scenarioGrid, scenarioGridColumn, gridY);
    }

    // Indirection add for test
    protected boolean hasEditableHeaderLocal(GridColumn<?> scenarioGridColumn) {
        return ScenarioSimulationGridHeaderUtilities.hasEditableHeader(scenarioGridColumn);
    }

    // Indirection add for test
    protected boolean isEditableHeaderLocal(GridColumn<?> scenarioGridColumn, Integer uiHeaderRowIndex) {
        return ScenarioSimulationGridHeaderUtilities.isEditableHeader(scenarioGridColumn, uiHeaderRowIndex);
    }
}
