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
import org.drools.workbench.screens.scenariosimulation.client.handlers.InsertColumnEventHandler;

/**
 * <code>GwtEvent</code> to <b>insert</b> a column.
 */
public class InsertColumnEvent extends GwtEvent<InsertColumnEventHandler> {

    public static Type<InsertColumnEventHandler> TYPE = new Type<>();

    private int columnIndex;
    private boolean isRight;

    /**
     *
     * @param columnIndex
     * @param isRight when <code>true</code>, column will be inserted to the right of the given index (i.e. at position columnIndex +1), otherwise to the left (i.e. at position columnIndex)
     */
    public InsertColumnEvent(int columnIndex, boolean isRight) {
        this.columnIndex = columnIndex;
        this.isRight = isRight;
    }

    @Override
    public Type<InsertColumnEventHandler> getAssociatedType() {
        return TYPE;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public boolean isRight() {
        return isRight;
    }

    @Override
    protected void dispatch(InsertColumnEventHandler handler) {
        handler.onEvent(this);
    }
}
