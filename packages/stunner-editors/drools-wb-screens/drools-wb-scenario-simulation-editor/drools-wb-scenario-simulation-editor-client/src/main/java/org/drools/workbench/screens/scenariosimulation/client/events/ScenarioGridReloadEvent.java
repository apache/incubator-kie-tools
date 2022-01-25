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
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioGridReloadEventHandler;

/**
 * <code>GwtEvent</code> to <b>reload</b> a grid
 */
public class ScenarioGridReloadEvent extends GwtEvent<ScenarioGridReloadEventHandler> {

    public static final Type<ScenarioGridReloadEventHandler> TYPE = new Type<>();

    private final GridWidget gridWidget;

    public ScenarioGridReloadEvent(GridWidget gridWidget) {
        this.gridWidget = gridWidget;
    }

    public GridWidget getGridWidget() {
        return gridWidget;
    }

    @Override
    public Type<ScenarioGridReloadEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ScenarioGridReloadEventHandler handler) {
        handler.onEvent(this);
    }

}
