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
import org.drools.workbench.screens.scenariosimulation.client.handlers.DeleteColumnEventHandler;

/**
 * <code>GwtEvent</code> to <b>delete</b> a column
 */
public class DeleteColumnEvent extends GwtEvent<DeleteColumnEventHandler> {

    public static final Type<DeleteColumnEventHandler> TYPE = new Type<>();

    private final GridWidget gridWidget;
    private final int columnIndex;
    private final String columnGroup;
    private final boolean asProperty;

    /**
     *
     * @param gridWidget
     * @param columnIndex
     * @param columnGroup
     * @param asProperty asProperty when <code>true</code> it delete only the column, when <code>false</code> it deletes the whole instance
     */
    public DeleteColumnEvent(GridWidget gridWidget, int columnIndex, String columnGroup, boolean asProperty) {
        this.gridWidget = gridWidget;
        this.columnIndex = columnIndex;
        this.columnGroup = columnGroup;
        this.asProperty = asProperty;
    }

    @Override
    public Type<DeleteColumnEventHandler> getAssociatedType() {
        return TYPE;
    }

    public GridWidget getGridWidget() {
        return gridWidget;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getColumnGroup() {
        return columnGroup;
    }

    public boolean isAsProperty() {
        return asProperty;
    }

    @Override
    protected void dispatch(DeleteColumnEventHandler handler) {
        handler.onEvent(this);
    }
}
