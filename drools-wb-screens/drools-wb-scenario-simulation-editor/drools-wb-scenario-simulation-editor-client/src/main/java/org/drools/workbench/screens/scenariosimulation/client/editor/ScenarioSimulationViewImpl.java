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
package org.drools.workbench.screens.scenariosimulation.client.editor;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

/**
 * Implementation of the main tab view for the ScenarioSimulation editor.
 * <p>
 * This class acts as a wrapper class which holds the main <code>ScenarioGridWidget</code>
 */
@Dependent
public class ScenarioSimulationViewImpl extends KieEditorViewImpl implements ScenarioSimulationView {

    protected ScenarioGridWidget scenarioGridWidget;

    @Override
    public void init() {
        initWidget(scenarioGridWidget);
    }

    @Override
    public ScenarioGridWidget getScenarioGridWidget() {
        return scenarioGridWidget;
    }

    @Override
    public void setScenarioGridWidget(ScenarioGridWidget scenarioGridWidget) {
        this.scenarioGridWidget = scenarioGridWidget;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if (parent != null) {
            final double w = parent.getOffsetWidth();
            final double h = parent.getOffsetHeight();
            setPixelSize((int) w, (int) h);
        }
        scenarioGridWidget.onResize();
    }
}