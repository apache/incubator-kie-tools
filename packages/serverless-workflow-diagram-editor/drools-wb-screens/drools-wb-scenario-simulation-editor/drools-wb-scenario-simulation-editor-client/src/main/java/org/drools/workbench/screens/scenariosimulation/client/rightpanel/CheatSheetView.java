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

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;

public interface CheatSheetView extends SubDockView<CheatSheetView.Presenter> {

    void setRuleCheatSheetContent();

    void setDMNCheatSheetContent();

    interface Presenter extends SubDockView.Presenter {

        /**
         * Method to initialize cheat sheet content based on <code>ScenarioSimulationModel.Type</code>
         * @param type
         */
        void initCheatSheet(ScenarioSimulationModel.Type type);
    }
}
