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

package org.drools.workbench.screens.scenariosimulation.webapp.client.dropdown;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.screens.scenariosimulation.client.dropdown.ScenarioSimulationDropdownView;
import org.kie.workbench.common.widgets.client.assets.dropdown.AbstractKieAssetsDropdown;

@Dependent
public class ScenarioSimulationKogitoLoadingScesimDropdown extends AbstractKieAssetsDropdown {

    @Inject
    public ScenarioSimulationKogitoLoadingScesimDropdown(@Named(ScenarioSimulationDropdownView.BEAN_NAME) ScenarioSimulationDropdownView view, ScenarioSimulationKogitoLoadingScesimAssetsDropdownProviderImpl dataProvider) {
        super(view, dataProvider);
    }
}