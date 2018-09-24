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
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetColumnValueEventHandler;

/**
 * <code>GwtEvent</code> to set a specific value for a given column
 */
public class SetColumnValueEvent extends GwtEvent<SetColumnValueEventHandler> {

    public static Type<SetColumnValueEventHandler> TYPE = new Type<>();

    private final int columnIndex;
    private String fullPackage;
    private final String value;
    private String valueClassName;

    public SetColumnValueEvent(int columnIndex, String fullPackage, String value, String valueClassName) {
        this.columnIndex = columnIndex;
        this.fullPackage = fullPackage;
        this.value = value;
        this.valueClassName = valueClassName;
    }

    @Override
    public Type<SetColumnValueEventHandler> getAssociatedType() {
        return TYPE;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getFullPackage() {
        return fullPackage;
    }

    public String getValue() {
        return value;
    }

    public String getValueClassName() {
        return valueClassName;
    }

    @Override
    protected void dispatch(SetColumnValueEventHandler handler) {
        handler.onEvent(this);
    }
}
