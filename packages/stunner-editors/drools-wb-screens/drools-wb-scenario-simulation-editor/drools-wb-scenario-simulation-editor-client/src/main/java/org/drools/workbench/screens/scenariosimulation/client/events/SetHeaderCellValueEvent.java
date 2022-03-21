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
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetHeaderCellValueEventHandler;

/**
 * <code>GwtEvent</code> to set the <code>GridCellValue</code> at a given rox/column position
 */
public class SetHeaderCellValueEvent extends GwtEvent<SetHeaderCellValueEventHandler> {

    public static final Type<SetHeaderCellValueEventHandler> TYPE = new Type<>();

    private final GridWidget gridWidget;
    private final int rowIndex;
    private final int columnIndex;
    private final String headerCellValue;
    private final boolean isInstanceHeader;
    private final boolean isPropertyHeader;

    /**
     * @param gridWidget
     * @param rowIndex
     * @param columnIndex
     * @param headerCellValue
     * @param isPropertyHeader set to <code>true</code> if the edited cell is inside the header
     */
    public SetHeaderCellValueEvent(GridWidget gridWidget, int rowIndex, int columnIndex, String headerCellValue, boolean isInstanceHeader, boolean isPropertyHeader) {
        this.gridWidget = gridWidget;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.headerCellValue = headerCellValue;
        this.isInstanceHeader = isInstanceHeader;
        this.isPropertyHeader = isPropertyHeader;
    }

    @Override
    public Type<SetHeaderCellValueEventHandler> getAssociatedType() {
        return TYPE;
    }

    public GridWidget getGridWidget() {
        return gridWidget;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getHeaderCellValue() {
        return headerCellValue;
    }

    public boolean isInstanceHeader() {
        return isInstanceHeader;
    }

    public boolean isPropertyHeader() {
        return isPropertyHeader;
    }

    @Override
    protected void dispatch(SetHeaderCellValueEventHandler handler) {
        handler.onEvent(this);
    }
}
