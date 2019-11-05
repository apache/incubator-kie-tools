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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.models.AbstractScesimGridModel;

public class ScenarioGridWidget extends ResizeComposite {

    protected ScenarioGridPanel scenarioGridPanel;

    protected boolean selected;

    public void setScenarioGridPanel(ScenarioGridPanel scenarioGridPanel) {
        this.scenarioGridPanel = scenarioGridPanel;
        initWidget(scenarioGridPanel);
    }

    public void setContent(AbstractScesimModel abstractScesimModel, ScenarioSimulationModel.Type type) {
        scenarioGridPanel.getScenarioGrid().setContent(abstractScesimModel, type);
    }

    public ScenarioGridPanel getScenarioGridPanel() {
        return scenarioGridPanel;
    }

    public ScenarioSimulationContext getScenarioSimulationContext() {
        return scenarioGridPanel.getScenarioGrid().getScenarioSimulationContext();
    }

    public void refreshContent(AbstractScesimModel abstractScesimModel) {
        scenarioGridPanel.getScenarioGrid().getModel().bindContent(abstractScesimModel);
        scenarioGridPanel.getScenarioGrid().getModel().refreshErrors();
        onResize();
    }

    public void unregister() {
        scenarioGridPanel.unregister();
    }

    public void clearSelections() {
        scenarioGridPanel.getScenarioGrid().getModel().clearSelections();
    }

    public void resetErrors() {
        scenarioGridPanel.getScenarioGrid().getModel().resetErrors();
    }

    public AbstractScesimGridModel getModel() {
        return scenarioGridPanel.getScenarioGrid().getModel();
    }

    public void select() {
        scenarioGridPanel.getScenarioGrid().select();
        selected = true;
    }

    public void deselect() {
        scenarioGridPanel.getScenarioGrid().deselect();
        selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if (parent != null) {
            final double w = parent.getOffsetWidth();
            final double h = parent.getOffsetHeight();
            setPixelSize((int) w, (int) h);
        }
        scenarioGridPanel.onResize();
    }
}
