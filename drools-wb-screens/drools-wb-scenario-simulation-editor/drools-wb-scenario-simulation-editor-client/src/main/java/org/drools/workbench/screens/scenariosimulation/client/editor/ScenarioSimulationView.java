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

import java.util.function.Supplier;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.model.menu.MenuItem;

public interface ScenarioSimulationView extends KieEditorView,
                                                IsWidget,
                                                RequiresResize {

    void init(final ScenarioSimulationEditorPresenter presenter);

    void setContent(final Simulation simulation);

    void refreshContent(final Simulation simulation);

    MenuItem getRunScenarioMenuItem();

    MenuItem getUndoMenuItem();

    MenuItem getRedoMenuItem();

    MenuItem getDownloadMenuItem(final Supplier<Path> pathSupplier);

    MenuItem getExportToCsvMenuItem();

    MenuItem getImportMenuItem();

    void setScenarioGridPanel(ScenarioGridPanel scenarioGridPanel);

    ScenarioGridPanel getScenarioGridPanel();

    ScenarioGridLayer getScenarioGridLayer();
}
