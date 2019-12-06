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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

@Dependent
public class CoverageScenarioListPresenter implements CoverageScenarioListView.Presenter {

    @Inject
    protected ViewsProvider viewsProvider;

    private HTMLUListElement scenarioList;

    @Override
    public void initScenarioList(HTMLUListElement scenarioList) {
        this.scenarioList = scenarioList;
    }

    @Override
    public void addScesimDataGroup(ScenarioWithIndex scenarioWithIndex, Map<String, Integer> resultCounter, Type type) {
        CoverageScenarioListView coverageScenarioListView = viewsProvider.getCoverageScenarioListView();
        coverageScenarioListView.setPresenter(this);
        coverageScenarioListView.setVisible(false);

        HTMLLIElement scenarioElement = coverageScenarioListView.getScenarioElement();

        String customText = Type.DMN.equals(type) ?
                ScenarioSimulationEditorConstants.INSTANCE.decisionsEvaluated() :
                ScenarioSimulationEditorConstants.INSTANCE.rulesFired();
        String itemLabel = customText + " " + scenarioWithIndex.getIndex() + ": " +
                scenarioWithIndex.getScesimData().getDescription();

        coverageScenarioListView.setItemLabel(itemLabel);

        scenarioElement.appendChild(createInternalList(resultCounter, coverageScenarioListView.getScenarioContentList()));

        this.scenarioList.appendChild(scenarioElement);
    }

    @Override
    public void clear() {
        while (scenarioList.firstChild != null) {
            scenarioList.removeChild(scenarioList.firstChild);
        }
    }

    protected HTMLUListElement createInternalList(Map<String, Integer> elements, HTMLUListElement listGroup) {
        List<String> keys = new ArrayList<>(elements.keySet());
        keys.sort(Comparator.naturalOrder());
        for (String key : keys) {
            Integer counter = elements.get(key);
            HTMLLIElement listElement = createElementLi();
            listElement.textContent = createContent(key, counter);
            listGroup.appendChild(listElement);
        }
        return listGroup;
    }

    protected HTMLLIElement createElementLi() {
        HTMLLIElement li = (HTMLLIElement) DomGlobal.document.createElement("li");
        li.classList.add("list-group-item");
        return li;
    }

    protected String createContent(String value, Integer counter) {
        return value + (counter != null && counter > 1 ? " (" + counter + ")" : "");
    }

    @Override
    public void onElementClick(CoverageScenarioListView coverageScenarioListView) {
        coverageScenarioListView.setVisible(!coverageScenarioListView.isVisible());
    }
}
