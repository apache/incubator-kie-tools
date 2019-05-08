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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class CoverageReportPresenterTest {

    @Mock
    protected CoverageReportDonutPresenter coverageReportDonutPresenterMock;

    @Mock
    protected CoverageDecisionElementPresenter coverageDecisionElementPresenterMock;

    @Mock
    protected CoverageScenarioListPresenter coverageScenarioListPresenterMock;

    @Mock
    protected CoverageReportView coverageReportViewMock;

    @InjectMocks
    protected CoverageReportPresenter presenter;

    protected CoverageReportPresenter presenterSpy;

    protected SimulationRunMetadata simulationRunMetadataLocal;

    private Map<String, Integer> outputCounterLocal;
    private Map<ScenarioWithIndex, List<String>> scenarioCounterLocal;
    private int availableLocal;
    private int executedLocal;
    private double coverageLocal;

    @Before
    public void setup() {
        presenterSpy = spy(presenter);

        availableLocal = 10;
        executedLocal = 5;
        coverageLocal = (double) executedLocal / availableLocal * 100;
        outputCounterLocal = new HashMap<>();
        outputCounterLocal.put("d1", 1);
        outputCounterLocal.put("d2", 2);
        scenarioCounterLocal = new HashMap<>();
        scenarioCounterLocal.put(new ScenarioWithIndex(1, new Scenario()), asList("d1", "d2"));
        scenarioCounterLocal.put(new ScenarioWithIndex(2, new Scenario()), singletonList("d2"));
        simulationRunMetadataLocal = new SimulationRunMetadata(availableLocal, executedLocal, outputCounterLocal, scenarioCounterLocal);
    }

    @Test
    public void setSimulationRunMetadata() {
        presenterSpy.setSimulationRunMetadata(simulationRunMetadataLocal, Type.RULE);
        verify(presenterSpy, never()).populateSummary(eq(availableLocal), eq(executedLocal), eq(coverageLocal));
        verify(presenterSpy, never()).populateDecisionList(eq(outputCounterLocal));
        verify(presenterSpy, never()).populateScenarioList(eq(scenarioCounterLocal));
        verify(coverageReportViewMock, never()).show();

        presenterSpy.setSimulationRunMetadata(simulationRunMetadataLocal, Type.DMN);
        verify(presenterSpy, times(1)).populateSummary(eq(availableLocal), eq(executedLocal), eq(coverageLocal));
        verify(presenterSpy, times(1)).populateDecisionList(eq(outputCounterLocal));
        verify(presenterSpy, times(1)).populateScenarioList(eq(scenarioCounterLocal));
        verify(coverageReportViewMock, times(1)).show();
    }

    @Test
    public void populateSummary() {
        presenterSpy.populateSummary(availableLocal, executedLocal, coverageLocal);
        verify(coverageReportViewMock, times(1)).setReportAvailable(eq(availableLocal + ""));
        verify(coverageReportViewMock, times(1)).setReportExecuted(eq(executedLocal + ""));
        verify(coverageReportViewMock, times(1)).setReportCoverage(eq(coverageLocal + "%"));
        int delta = availableLocal - executedLocal;
        verify(coverageReportDonutPresenterMock, times(1)).showCoverageReport(eq(executedLocal),
                                                                              eq(delta));
    }

    @Test
    public void populateDecisionList() {
        presenterSpy.populateDecisionList(outputCounterLocal);
        verify(coverageDecisionElementPresenterMock, times(1)).clear();
        verify(coverageDecisionElementPresenterMock, times(outputCounterLocal.size())).addDecisionElementView(anyString(), anyString());
    }

    @Test
    public void populateScenarioList() {
        presenterSpy.populateScenarioList(scenarioCounterLocal);
        verify(coverageScenarioListPresenterMock, times(1)).clear();
        verify(coverageScenarioListPresenterMock, times(scenarioCounterLocal.size())).addScenarioGroup(any(), any());
    }

    @Test
    public void showEmptyStateMessage() {
        presenterSpy.showEmptyStateMessage(null);
        verify(coverageReportViewMock, times(1)).setEmptyStatusText(anyString());
        verify(coverageReportViewMock, times(1)).hide();
    }

    @Test
    public void resetTest() {
        presenterSpy.reset();
        verify(coverageReportViewMock, times(1)).reset();
    }
}