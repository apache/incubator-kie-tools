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

package org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor.ScenarioSimulationBusinessCentralViewsProvider;

@Dependent
public class CoverageElementPresenter implements CoverageElementView.Presenter {

    @Inject
    protected ScenarioSimulationBusinessCentralViewsProvider viewsProvider;

    private HTMLElement elementList;

    @Override
    public void initElementList(HTMLElement elementList) {
        this.elementList = elementList;
    }

    @Override
    public void addElementView(String description, String value) {
        CoverageElementView coverageElementView = viewsProvider.getCoverageElementView();
        coverageElementView.setDescriptionValue(description);
        coverageElementView.setElementValue(value);

        this.elementList.appendChild(coverageElementView.getDescription());
        this.elementList.appendChild(coverageElementView.getNumberOfTime());
    }

    public void clear() {
        while (elementList.firstChild != null) {
            elementList.removeChild(elementList.firstChild);
        }
    }
}
