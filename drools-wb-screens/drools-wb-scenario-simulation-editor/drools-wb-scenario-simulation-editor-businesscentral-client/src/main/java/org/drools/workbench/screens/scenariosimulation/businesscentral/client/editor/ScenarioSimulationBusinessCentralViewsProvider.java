/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage.CoverageElementView;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage.CoverageScenarioListView;
import org.jboss.errai.ioc.client.api.ManagedInstance;

/**
 * Class used as Provider for specific <i>Views</i> on Business Central module only, that has to be dynamically created
 */
@ApplicationScoped
public class ScenarioSimulationBusinessCentralViewsProvider {

    @Inject
    private ManagedInstance<CoverageElementView> coverageElementView;

    @Inject
    private ManagedInstance<CoverageScenarioListView> coverageScenarioListView;

    public CoverageElementView getCoverageElementView() {
        return coverageElementView.get();
    }

    public CoverageScenarioListView getCoverageScenarioListView() {
        return coverageScenarioListView.get();
    }
}
