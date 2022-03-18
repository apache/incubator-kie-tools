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
package org.drools.workbench.screens.scenariosimulation.client.events;

import com.google.gwt.event.shared.GwtEvent;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.handlers.DuplicateInstanceEventHandler;

/**
 * <code>GwtEvent</code> to <b>duplicate</b> an instance.
 */
public class DuplicateInstanceEvent extends GwtEvent<DuplicateInstanceEventHandler> {

    public static final Type<DuplicateInstanceEventHandler> TYPE = new Type<>();

    private final GridWidget gridWidget;
    private final int columnIndex;

    /**
     *
     * @param gridWidget
     * @param columnIndex
     */
    public DuplicateInstanceEvent(GridWidget gridWidget, int columnIndex) {
        this.gridWidget = gridWidget;
        this.columnIndex = columnIndex;
    }

    @Override
    public Type<DuplicateInstanceEventHandler> getAssociatedType() {
        return TYPE;
    }

    public GridWidget getGridWidget() {
        return gridWidget;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    @Override
    protected void dispatch(DuplicateInstanceEventHandler handler) {
        handler.onEvent(this);
    }
}
