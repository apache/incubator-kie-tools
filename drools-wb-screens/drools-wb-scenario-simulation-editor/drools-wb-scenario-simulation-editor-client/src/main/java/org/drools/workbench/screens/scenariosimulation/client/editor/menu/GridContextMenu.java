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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.LIElement;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RunSingleScenarioEvent;

/**
 * The contextual menu of a a <i>ROW</i> cell whose <b>GROUP</b> does <b>allow</b> column modification (insert/delete). It has the same items has {@link AbstractColumnMenuPresenter} and specific ones (?)
 */
@Dependent
public class GridContextMenu extends AbstractColumnMenuPresenter {

    // This strings are used to give unique id in the final dom
    private final String GRIDCONTEXTMENU_GRID = "gridcontextmenu-grid";
    private final String GRIDCONTEXTMENU_INSERT_COLUMN_LEFT = "gridcontextmenu-insert-column-left";
    private final String GRIDCONTEXTMENU_INSERT_COLUMN_RIGHT = "gridcontextmenu-insert-column-right";
    private final String GRIDCONTEXTMENU_DELETE_COLUMN = "gridcontextmenu-delete-column";
    private final String GRIDCONTEXTMENU_INSERT_ROW_ABOVE = "gridcontextmenu-insert-row-above";
    private final String GRIDCONTEXTMENU_INSERT_ROW_BELOW = "gridcontextmenu-insert-row-below";
    private final String GRIDCONTEXTMENU_DELETE_ROW = "gridcontextmenu-delete-row";
    private final String GRIDCONTEXTMENU_DUPLICATE_ROW = "gridcontextmenu-duplicate-row";
    private final String GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO = "gridcontextmenu-run-single-scenario";

    private LIElement insertRowAboveLIElement;
    private LIElement insertRowBelowLIElement;
    private LIElement duplicateRowLIElement;
    private LIElement deleteRowLIElement;
    private LIElement runSingleScenarioElement;

    @PostConstruct
    @Override
    public void initMenu() {
        // GRID MENU
        COLUMNCONTEXTMENU_COLUMN = GRIDCONTEXTMENU_GRID;
        COLUMNCONTEXTMENU_INSERT_COLUMN_LEFT = GRIDCONTEXTMENU_INSERT_COLUMN_LEFT;
        COLUMNCONTEXTMENU_INSERT_COLUMN_RIGHT = GRIDCONTEXTMENU_INSERT_COLUMN_RIGHT;
        COLUMNCONTEXTMENU_DELETE_COLUMN = GRIDCONTEXTMENU_DELETE_COLUMN;
        COLUMNCONTEXTMENU_LABEL = constants.expect().toUpperCase();
        COLUMNCONTEXTMENU_I18N = "grid";
        // SCENARIO MENU
        super.initMenu();
        removeMenuItem(insertRowBelowElement);
        insertRowAboveLIElement = addExecutableMenuItem(GRIDCONTEXTMENU_INSERT_ROW_ABOVE, constants.insertRowAbove(), "insertRowAbove");
        insertRowBelowLIElement = addExecutableMenuItem(GRIDCONTEXTMENU_INSERT_ROW_BELOW, constants.insertRowBelow(), "insertRowBelow");
        duplicateRowLIElement = addExecutableMenuItem(GRIDCONTEXTMENU_DUPLICATE_ROW, constants.duplicateRow(), "duplicateRow");
        deleteRowLIElement = addExecutableMenuItem(GRIDCONTEXTMENU_DELETE_ROW, constants.deleteRow(), "deleteRow");
        runSingleScenarioElement = addExecutableMenuItem(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO, constants.runSingleScenario(), "runSingleScenario");
    }

    public void show(final int mx, final int my, int columnIndex, int rowIndex, String group, boolean asProperty, boolean showDuplicate) {
        super.show(mx, my, columnIndex, group, asProperty, showDuplicate);
        columnContextLIElement
                .getChild(1) //  a  element
                .getChild(3) // span element
                .getFirstChild() // b element
                .getChild(0) // text
                .setNodeValue(group);
        mapEvent(insertRowAboveLIElement, new InsertRowEvent(rowIndex));
        mapEvent(insertRowBelowLIElement, new InsertRowEvent(rowIndex + 1));
        mapEvent(duplicateRowLIElement, new DuplicateRowEvent(rowIndex));
        mapEvent(deleteRowLIElement, new DeleteRowEvent(rowIndex));
        mapEvent(runSingleScenarioElement, new RunSingleScenarioEvent(rowIndex));
    }
}
