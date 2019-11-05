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
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;
import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.endsWith;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CoverageReportPresenterTest {

    @Mock
    protected CoverageReportDonutPresenter coverageReportDonutPresenterMock;

    @Mock
    protected CoverageElementPresenter coverageElementPresenterMock;

    @Mock
    protected CoverageScenarioListPresenter coverageScenarioListPresenterMock;

    @Mock
    protected CoverageReportView coverageReportViewMock;

    @Mock
    private HTMLDivElement donutChartMock;

    @Mock
    private HTMLElement list;

    @Mock
    private HTMLUListElement scenarioList;

    @Mock
    private HTMLButtonElement downloadReportButtonMock;

    @Mock
    private Command downloadReportCommandMock;

    @InjectMocks
    protected CoverageReportPresenter presenter;

    @Captor
    protected ArgumentCaptor<ScenarioWithIndex> scenarioWithIndexCaptor;

    @Captor
    protected ArgumentCaptor<Map<String, Integer>> resultCounterCaptor;

    @Captor
    protected ArgumentCaptor<String> descriptionCaptor;

    @Captor
    protected ArgumentCaptor<String> valueCaptor;

    protected CoverageReportPresenter presenterSpy;

    protected SimulationRunMetadata simulationRunMetadataLocal;

    private Map<String, Integer> outputCounterLocal;
    private Map<ScenarioWithIndex, Map<String, Integer>> scenarioCounterLocal;
    private AuditLog auditLog;
    private int availableLocal;
    private int executedLocal;
    private double coverageLocal;

    @Before
    public void setup() {
        presenterSpy = spy(presenter);
        presenterSpy.downloadReportCommand = downloadReportCommandMock;
        when(coverageReportViewMock.getDonutChart()).thenReturn(donutChartMock);
        when(coverageReportViewMock.getList()).thenReturn(list);
        when(coverageReportViewMock.getScenarioList()).thenReturn(scenarioList);
        when(coverageReportViewMock.getDownloadReportButton()).thenReturn(downloadReportButtonMock);

        availableLocal = 10;
        executedLocal = 6;
        coverageLocal = (double) executedLocal / availableLocal * 100;
        outputCounterLocal = new HashMap<>();
        outputCounterLocal.put("d1", 1);
        outputCounterLocal.put("d2", 2);
        scenarioCounterLocal = new HashMap<>();
        Map<String, Integer> scenario1Data = new HashMap<>();
        scenario1Data.put("d1", 1);
        scenario1Data.put("d2", 1);
        Map<String, Integer> scenario2Data = new HashMap<>();
        scenario2Data.put("d2", 1);
        scenarioCounterLocal.put(new ScenarioWithIndex(1, new Scenario()), scenario1Data);
        scenarioCounterLocal.put(new ScenarioWithIndex(2, new Scenario()), scenario2Data);
        simulationRunMetadataLocal = new SimulationRunMetadata(availableLocal, executedLocal, outputCounterLocal, scenarioCounterLocal, auditLog);
    }

    @Test
    public void init() {
        presenterSpy.init();
        verify(coverageReportDonutPresenterMock, times(1)).init(eq(donutChartMock));
        verify(coverageElementPresenterMock, times(1)).initElementList(eq(list));
        verify(coverageScenarioListPresenterMock, times(1)).initScenarioList(eq(scenarioList));
        verify(presenterSpy, times(1)).resetDownload();
    }

    @Test
    public void populateCoverageReport() {
        presenterSpy.populateCoverageReport(Type.DMN, simulationRunMetadataLocal);
        verify(presenterSpy, times(1)).setSimulationRunMetadata(eq(simulationRunMetadataLocal), eq(Type.DMN));

        reset(presenterSpy);

        presenterSpy.populateCoverageReport(Type.DMN, null);
        verify(presenterSpy, times(1)).showEmptyStateMessage();

        reset(presenterSpy);

        presenterSpy.populateCoverageReport(Type.RULE, simulationRunMetadataLocal);
        verify(presenterSpy, times(1)).setSimulationRunMetadata(eq(simulationRunMetadataLocal), eq(Type.RULE));
    }

    @Test
    public void setSimulationRunMetadata() {
        presenterSpy.setSimulationRunMetadata(simulationRunMetadataLocal, Type.DMN);
        verify(presenterSpy, times(1)).populateSummary(eq(availableLocal), eq(executedLocal), eq(coverageLocal));
        verify(presenterSpy, times(1)).populateList(eq(outputCounterLocal));
        verify(presenterSpy, times(1)).populateScenarioList(eq(scenarioCounterLocal), eq(Type.DMN));
        verify(coverageReportViewMock, times(1)).show();
    }

    @Test
    public void populateSummary() {
        presenterSpy.populateSummary(availableLocal, executedLocal, coverageLocal);
        verify(coverageReportViewMock, times(1)).setReportAvailable(eq(availableLocal + ""));
        verify(coverageReportViewMock, times(1)).setReportExecuted(eq(executedLocal + ""));
        // cannot test actual value because it uses GWT NumberFormat that is not available on server side
        verify(coverageReportViewMock, times(1)).setReportCoverage(endsWith("%"));
        int delta = availableLocal - executedLocal;
        verify(coverageReportDonutPresenterMock, times(1)).showCoverageReport(eq(executedLocal),
                                                                              eq(delta));
    }

    @Test
    public void populateList() {
        presenterSpy.populateList(outputCounterLocal);
        verify(coverageElementPresenterMock, times(1)).clear();
        verify(coverageElementPresenterMock, times(outputCounterLocal.size()))
                .addElementView(descriptionCaptor.capture(),
                                valueCaptor.capture());

        List<String> descriptions = descriptionCaptor.getAllValues();
        assertEquals(2, descriptions.size());
        assertEquals("d1", descriptions.get(0));
        assertEquals("d2", descriptions.get(1));

        List<String> values = valueCaptor.getAllValues();
        assertEquals(2, values.size());
        assertEquals("1", values.get(0));
        assertEquals("2", values.get(1));
    }

    @Test
    public void populateScenarioList() {
        presenterSpy.populateScenarioList(scenarioCounterLocal, Type.DMN);
        verify(coverageScenarioListPresenterMock, times(1)).clear();
        verify(coverageScenarioListPresenterMock, times(scenarioCounterLocal.size()))
                .addScesimDataGroup(scenarioWithIndexCaptor.capture(),
                                  resultCounterCaptor.capture(),
                                  eq(Type.DMN));

        List<ScenarioWithIndex> scenarios = scenarioWithIndexCaptor.getAllValues();
        assertEquals(scenarioCounterLocal.size(), scenarios.size());
        assertEquals(1, scenarios.get(0).getIndex());
        assertEquals(2, scenarios.get(1).getIndex());

        List<Map<String, Integer>> resultCounters = resultCounterCaptor.getAllValues();
        assertEquals(scenarioCounterLocal.size(), resultCounters.size());
        assertTrue(resultCounters.get(0).keySet().contains("d1"));
        assertEquals(1, resultCounters.get(0).get("d1"));
        assertTrue(resultCounters.get(0).keySet().contains("d2"));
        assertEquals(1, resultCounters.get(0).get("d2"));
        assertTrue(resultCounters.get(1).keySet().contains("d2"));
        assertEquals(1, resultCounters.get(1).get("d2"));
    }

    @Test
    public void showEmptyStateMessage() {
        presenterSpy.showEmptyStateMessage();
        verify(coverageReportViewMock, times(1)).setEmptyStatusText(anyString());
        verify(coverageReportViewMock, times(1)).hide();
    }

    @Test
    public void resetTest() {
        presenterSpy.reset();
        verify(coverageReportViewMock, times(1)).reset();
        verify(presenterSpy, times(1)).resetDownload();
    }

    @Test
    public void onDownloadButtonClicked() {
        presenterSpy.onDownloadReportButtonClicked();
        verify(downloadReportCommandMock, times(1)).execute();
    }

    @Test
    public void onDownloadButtonClicked_NoCommandAssigned() {
        presenterSpy.downloadReportCommand = null;
        verify(downloadReportCommandMock, never()).execute();
    }

    @Test
    public void resetDownload() {
        presenterSpy.resetDownload();
        assertTrue(downloadReportButtonMock.disabled);
        assertNull(presenterSpy.downloadReportCommand);
    }
}