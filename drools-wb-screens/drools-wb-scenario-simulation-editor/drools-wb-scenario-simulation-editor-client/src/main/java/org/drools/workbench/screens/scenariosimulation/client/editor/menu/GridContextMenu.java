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

package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.LIElement;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RunSingleScenarioEvent;

/**
 * The contextual menu of a a <i>ROW</i> cell whose <b>GROUP</b> does <b>allow</b> column modification (insert/delete). It has the same items has {@link AbstractColumnMenuPresenter} and specific ones (?)
 */
@Dependent
public class GridContextMenu extends AbstractHeaderMenuPresenter {

    // This strings are used to give unique id in the final dom
    protected static final String GRIDCONTEXTMENU_GRID_TITLE = "gridcontextmenu-grid-title";
    protected static final String GRIDCONTEXTMENU_INSERT_ROW_ABOVE = "gridcontextmenu-insert-row-above";
    protected static final String GRIDCONTEXTMENU_INSERT_ROW_BELOW = "gridcontextmenu-insert-row-below";
    protected static final String GRIDCONTEXTMENU_DELETE_ROW = "gridcontextmenu-delete-row";
    protected static final String GRIDCONTEXTMENU_DUPLICATE_ROW = "gridcontextmenu-duplicate-row";
    protected static final String GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO = "gridcontextmenu-run-single-scenario";

    protected LIElement insertRowAboveLIElement;
    protected LIElement insertRowBelowLIElement;
    protected LIElement duplicateRowLIElement;
    protected LIElement deleteRowLIElement;
    protected LIElement runSingleScenarioElement;

    @PostConstruct
    @Override
    public void initMenu() {
        super.initMenu();
        removeMenuItem(insertRowBelowElement);
        insertRowAboveLIElement = addExecutableMenuItem(GRIDCONTEXTMENU_INSERT_ROW_ABOVE, constants.insertRowAbove(), "insertRowAbove");
        insertRowBelowLIElement = addExecutableMenuItem(GRIDCONTEXTMENU_INSERT_ROW_BELOW, constants.insertRowBelow(), "insertRowBelow");
        duplicateRowLIElement = addExecutableMenuItem(GRIDCONTEXTMENU_DUPLICATE_ROW, constants.duplicateRow(), "duplicateRow");
        deleteRowLIElement = addExecutableMenuItem(GRIDCONTEXTMENU_DELETE_ROW, constants.deleteRow(), "deleteRow");
        runSingleScenarioElement = addExecutableMenuItem(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO, constants.runSingleScenario(), "runSingleScenario");
    }

    public void show(final GridWidget gridWidget, final int mx, final int my, int rowIndex) {
        show(gridWidget, mx, my);
        mapEvent(insertRowAboveLIElement, new InsertRowEvent(gridWidget, rowIndex));
        mapEvent(insertRowBelowLIElement, new InsertRowEvent(gridWidget, rowIndex + 1));
        mapEvent(duplicateRowLIElement, new DuplicateRowEvent(gridWidget, rowIndex));
        mapEvent(deleteRowLIElement, new DeleteRowEvent(gridWidget, rowIndex));
        if (Objects.equals(GridWidget.BACKGROUND, gridWidget) && runSingleScenarioElement != null) {
            updateMenuItemAttributes(gridTitleElement , GRIDCONTEXTMENU_GRID_TITLE, constants.background(), "background");
            removeMenuItem(runSingleScenarioElement);
            runSingleScenarioElement = null;
        } else if (Objects.equals(GridWidget.SIMULATION, gridWidget)) {
            updateMenuItemAttributes(gridTitleElement , GRIDCONTEXTMENU_GRID_TITLE, constants.scenario(), "scenario");
            if (runSingleScenarioElement == null) {
                runSingleScenarioElement = addExecutableMenuItem(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO, constants.runSingleScenario(), "runSingleScenario");
            }
            mapEvent(runSingleScenarioElement, new RunSingleScenarioEvent(rowIndex));
        }
    }

}
