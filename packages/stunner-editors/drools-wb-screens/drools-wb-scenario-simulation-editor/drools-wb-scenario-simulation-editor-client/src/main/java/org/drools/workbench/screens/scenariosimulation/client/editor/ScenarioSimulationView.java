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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface ScenarioSimulationView extends KieEditorView,
                                                IsWidget,
                                                RequiresResize {

    void init();

    ScenarioGridWidget getScenarioGridWidget();

    void setScenarioGridWidget(ScenarioGridWidget scenarioMainGridWidget);

    void setContentWidget(Widget widget);
}
