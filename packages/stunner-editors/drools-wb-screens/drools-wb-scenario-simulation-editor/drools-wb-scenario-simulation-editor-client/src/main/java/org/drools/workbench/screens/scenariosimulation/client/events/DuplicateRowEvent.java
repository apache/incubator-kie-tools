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
import org.drools.workbench.screens.scenariosimulation.client.handlers.DuplicateRowEventHandler;

/**
 * <code>GwtEvent</code> to <b>duplicate</b> a row
 */
public class DuplicateRowEvent extends GwtEvent<DuplicateRowEventHandler> {

    public static final Type<DuplicateRowEventHandler> TYPE = new Type<>();

    private final GridWidget gridWidget;
    private final int rowIndex;

    /**
     *
     * @param gridWidget
     * @param rowIndex
     */
    public DuplicateRowEvent(GridWidget gridWidget, int rowIndex) {
        this.gridWidget = gridWidget;
        this.rowIndex = rowIndex;
    }

    @Override
    public Type<DuplicateRowEventHandler> getAssociatedType() {
        return TYPE;
    }

    public GridWidget getGridWidget() {
        return gridWidget;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    @Override
    protected void dispatch(DuplicateRowEventHandler handler) {
        handler.onEvent(this);
    }
}
