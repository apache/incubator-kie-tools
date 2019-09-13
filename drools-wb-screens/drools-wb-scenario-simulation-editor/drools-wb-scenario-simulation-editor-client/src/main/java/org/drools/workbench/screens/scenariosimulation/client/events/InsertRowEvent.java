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
import org.drools.workbench.screens.scenariosimulation.client.handlers.InsertRowEventHandler;

/**
 * <code>GwtEvent</code> to <b>append</b> (i.e. put in the last position) a row
 */
public class InsertRowEvent extends GwtEvent<InsertRowEventHandler> {

    public static final Type<InsertRowEventHandler> TYPE = new Type<>();

    private final int rowIndex;

    public InsertRowEvent(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public Type<InsertRowEventHandler> getAssociatedType() {
        return TYPE;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    @Override
    protected void dispatch(InsertRowEventHandler handler) {
        handler.onEvent(this);
    }
}
