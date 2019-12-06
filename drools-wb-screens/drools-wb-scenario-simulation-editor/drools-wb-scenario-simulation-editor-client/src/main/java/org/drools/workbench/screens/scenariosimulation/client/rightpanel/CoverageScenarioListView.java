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

import java.util.Map;

import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;

public interface CoverageScenarioListView {

    void setPresenter(Presenter presenter);

    HTMLLIElement getScenarioElement();

    HTMLUListElement getScenarioContentList();

    boolean isVisible();

    void setItemLabel(String itemLabel);

    void setVisible(boolean visible);

    interface Presenter {

        void initScenarioList(HTMLUListElement scenarioList);

        void clear();

        void addScesimDataGroup(ScenarioWithIndex key, Map<String, Integer> value, Type type);

        void onElementClick(CoverageScenarioListView coverageScenarioListView);
    }
}
