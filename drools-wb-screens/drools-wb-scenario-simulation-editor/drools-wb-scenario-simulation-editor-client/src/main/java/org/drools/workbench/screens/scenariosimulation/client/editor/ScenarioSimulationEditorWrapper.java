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
package org.drools.workbench.screens.scenariosimulation.client.editor;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.workbench.screens.scenariosimulation.client.handlers.AbstractScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

public interface ScenarioSimulationEditorWrapper {

    void onRunScenario(RemoteCallback<SimulationRunResult> refreshModelCallback, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, ScesimModelDescriptor simulationDescriptor, Settings settings, List<ScenarioWithIndex> toRun, Background background);

    Integer getOriginalHash();

    void wrappedRegisterDock(final String id, final IsWidget widget);

    void onImport(String fileContents, RemoteCallback<AbstractScesimModel> importCallBack, ErrorCallback<Object> importErrorCallback, AbstractScesimModel<? extends AbstractScesimData> scesimModel);

    void onExportToCsv(RemoteCallback<String> exportCallBack, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, AbstractScesimModel<? extends AbstractScesimData> scesimModel);

    void onDownloadReportToCsv(RemoteCallback<String> exportCallBack, ScenarioSimulationHasBusyIndicatorDefaultErrorCallback scenarioSimulationHasBusyIndicatorDefaultErrorCallback, SimulationRunMetadata simulationRunMetadata, ScenarioSimulationModel.Type modelType);

    void validate(Simulation simulation, Settings settings, RemoteCallback<?> callback);

    void onRefreshedModelContent(SimulationRunResult testResult);

    void addBackgroundPage(ScenarioGridWidget scenarioGridWidget);

    void selectSimulationTab();

    void selectBackgroundTab();

    AbstractScenarioSimulationDocksHandler getScenarioSimulationDocksHandler();

    ScenarioSimulationEditorPresenter getScenarioSimulationEditorPresenter();

    default void synchronizeColumnsDimension(ScenarioGridPanel simulationPanel, ScenarioGridPanel backgroundPanel) {
        simulationPanel.synchronizeFactMappingsWidths();
        backgroundPanel.synchronizeFactMappingsWidths();
    }

    default void populateDocks(String identifier) {
        switch (identifier) {
            case SettingsPresenter.IDENTIFIER:
                getScenarioSimulationDocksHandler().getSettingsPresenter().ifPresent(presenter -> {
                    getScenarioSimulationEditorPresenter().setSettings(presenter);
                    presenter.setCurrentPath(getScenarioSimulationEditorPresenter().getPath());
                });
                break;
            case TestToolsPresenter.IDENTIFIER:
                getScenarioSimulationDocksHandler().getTestToolsPresenter().ifPresent(
                        presenter -> getScenarioSimulationEditorPresenter().setTestTools(presenter));
                break;
            case CheatSheetPresenter.IDENTIFIER:
                getScenarioSimulationDocksHandler().getCheatSheetPresenter().ifPresent(
                        presenter -> {
                            if (!presenter.isCurrentlyShow(getScenarioSimulationEditorPresenter().getPath())) {
                                getScenarioSimulationEditorPresenter().setCheatSheet(presenter);
                                presenter.setCurrentPath(getScenarioSimulationEditorPresenter().getPath());
                            }
                        });
                break;
            default:
                throw new IllegalArgumentException("Invalid identifier");
        }
    }
}
