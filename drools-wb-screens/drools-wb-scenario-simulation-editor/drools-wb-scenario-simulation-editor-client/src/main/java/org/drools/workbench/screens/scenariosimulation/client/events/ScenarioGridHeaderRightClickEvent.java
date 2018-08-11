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
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioGridHeaderRightClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;

public class ScenarioGridHeaderRightClickEvent extends GwtEvent<ScenarioGridHeaderRightClickHandler> {

    public static Type<ScenarioGridHeaderRightClickHandler> TYPE = new Type<>();

    private final ScenarioHeaderMetaData scenarioHeaderMetaData;
    private final ScenarioGrid scenarioGrid;
    private final int left;
    private final int top;


    public ScenarioGridHeaderRightClickEvent(ScenarioHeaderMetaData scenarioHeaderMetaData, ScenarioGrid scenarioGrid, int left, int top) {
        this.scenarioHeaderMetaData = scenarioHeaderMetaData;
        this.scenarioGrid = scenarioGrid;
        this.left = left;
        this.top = top;
    }

    @Override
    public Type<ScenarioGridHeaderRightClickHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ScenarioGridHeaderRightClickHandler handler) {
        handler.onRightClick(this);
    }

    public ScenarioHeaderMetaData getScenarioHeaderMetaData() {
        return scenarioHeaderMetaData;
    }

    public ScenarioGrid getScenarioGrid() {
        return scenarioGrid;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }
}
