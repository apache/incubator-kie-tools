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

import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;

public class ScenarioGridCell extends BaseGridCell<String> {

    private boolean isEditingMode = false;
    private boolean isErrorMode = false;
    private boolean isList = false;
    private boolean isMap = false;

    public ScenarioGridCell(ScenarioGridCellValue value) {
        super(value);
    }

    public boolean isEditingMode() {
        return isEditingMode;
    }

    public void setEditingMode(boolean editingMode) {
        isEditingMode = editingMode;
    }

    public boolean isErrorMode() {
        return isErrorMode;
    }

    public void setErrorMode(boolean errorMode) {
        isErrorMode = errorMode;
    }

    public boolean isList() {
        return isList;
    }

    /**
     * Method to set the <b>isList/isMap</b> flags. Invoke this <b>only</b> for <i>collection</i> cells.
     * By default, both <b>isList/isMap</b> are false.
     * @param list
     */
    public void setListMap(boolean list) {
        isList = list;
        isMap = !list;
    }

    public boolean isMap() {
        return isMap;
    }

}