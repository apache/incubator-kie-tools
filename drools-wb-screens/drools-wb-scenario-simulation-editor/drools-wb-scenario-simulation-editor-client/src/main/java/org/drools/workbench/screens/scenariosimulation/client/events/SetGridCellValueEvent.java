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
package org.drools.workbench.screens.scenariosimulation.client.events;

import com.google.gwt.event.shared.GwtEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetGridCellValueEventHandler;

/**
 * <code>GwtEvent</code> to set the <code>GridCellValue</code> at a given rox/column position
 */
public class SetGridCellValueEvent extends GwtEvent<SetGridCellValueEventHandler> {

    public static Type<SetGridCellValueEventHandler> TYPE = new Type<>();

    private int rowIndex;
    private int columnIndex;
    private String cellValue;

    /**
     * 
     * @param rowIndex
     * @param columnIndex
     * @param cellValue
     */
    public SetGridCellValueEvent(int rowIndex, int columnIndex, String cellValue) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.cellValue = cellValue;
    }

    @Override
    public Type<SetGridCellValueEventHandler> getAssociatedType() {
        return TYPE;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getCellValue() {
        return cellValue;
    }

    @Override
    protected void dispatch(SetGridCellValueEventHandler handler) {
        handler.onEvent(this);
    }
}
