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

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor.ScenarioSimulationBusinessCentralViewsProvider;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CoverageScenarioListPresenterTest {

    @Mock
    private ScenarioSimulationBusinessCentralViewsProvider viewsProviderMock;

    @Mock
    private CoverageScenarioListView coverageScenarioListViewMock;

    @Mock
    private HTMLLIElement scenarioElementMock;

    @Mock
    private HTMLUListElement scenarioContentListMock;

    @Mock
    private HTMLUListElement scenarioList;

    private CoverageScenarioListPresenter coverageScenarioListPresenterSpy;

    @Before
    public void setup() {
        coverageScenarioListPresenterSpy = spy(CoverageScenarioListPresenter.class);
        coverageScenarioListPresenterSpy.viewsProvider = viewsProviderMock;
        coverageScenarioListPresenterSpy.initScenarioList(scenarioList);
        when(viewsProviderMock.getCoverageScenarioListView()).thenReturn(coverageScenarioListViewMock);
        when(coverageScenarioListViewMock.getScenarioElement()).thenReturn(scenarioElementMock);
        when(coverageScenarioListViewMock.getScenarioElement()).thenReturn(scenarioElementMock);
        when(coverageScenarioListViewMock.getScenarioContentList()).thenReturn(scenarioContentListMock);
    }

    @Test
    public void addScesimDataGroup_DMN() {
        int scenarioIndex = 1;
        String scenarioDescription = "description";
        String expectedLabel = ScenarioSimulationEditorConstants.INSTANCE.decisionsEvaluated() + " " + scenarioIndex + ": " + scenarioDescription;
        commonAddScesimDataGroup(ScenarioSimulationModel.Type.DMN, scenarioIndex, scenarioDescription, expectedLabel);
    }

    @Test
    public void addScesimDataGroup_RULE() {
        int scenarioIndex = 1;
        String scenarioDescription = "description";
        String expectedLabel = ScenarioSimulationEditorConstants.INSTANCE.rulesFired() + " " + scenarioIndex + ": " + scenarioDescription;
        commonAddScesimDataGroup(ScenarioSimulationModel.Type.RULE, scenarioIndex, scenarioDescription, expectedLabel);
    }

    private void commonAddScesimDataGroup(ScenarioSimulationModel.Type type, int scenarioIndex, String scenarioDescription, String expectedLabel) {
        Scenario scenario = new Scenario();
        scenario.setDescription(scenarioDescription);
        ScenarioWithIndex scenarioWithIndex = new ScenarioWithIndex(scenarioIndex, scenario);
        Map<String, Integer> resultCounter = new HashMap<>();
        coverageScenarioListPresenterSpy.addScesimDataGroup(scenarioWithIndex, resultCounter, type);
        verify(viewsProviderMock, times(1)).getCoverageScenarioListView();
        verify(coverageScenarioListViewMock, times(1)).setPresenter(eq(coverageScenarioListPresenterSpy));
        verify(coverageScenarioListViewMock, times(1)).setVisible(eq(false));
        verify(coverageScenarioListViewMock, times(1)).getScenarioElement();
        verify(coverageScenarioListViewMock, times(1)).setItemLabel(eq(expectedLabel));
        verify(scenarioElementMock, times(1)).appendChild(isA(HTMLUListElement.class));
    }
}
