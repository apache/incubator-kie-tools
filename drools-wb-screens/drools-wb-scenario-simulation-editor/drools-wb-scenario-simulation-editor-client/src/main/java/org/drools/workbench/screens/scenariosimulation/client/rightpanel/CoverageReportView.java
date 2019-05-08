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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;

public interface CoverageReportView
        extends SubDockView<CoverageReportView.Presenter> {

    void hide();

    void show();

    void setReportAvailable(String value);

    void setReportExecuted(String value);

    void setReportCoverage(String value);

    void setEmptyStatusText(String value);

    HTMLElement getDecisionList();

    HTMLDivElement getDonutChart();

    HTMLUListElement getScenarioList();

    interface Presenter extends SubDockView.Presenter {

        void setSimulationRunMetadata(SimulationRunMetadata simulationRunMetadata, ScenarioSimulationModel.Type type);

        void showEmptyStateMessage(ScenarioSimulationModel.Type type);
    }
}
