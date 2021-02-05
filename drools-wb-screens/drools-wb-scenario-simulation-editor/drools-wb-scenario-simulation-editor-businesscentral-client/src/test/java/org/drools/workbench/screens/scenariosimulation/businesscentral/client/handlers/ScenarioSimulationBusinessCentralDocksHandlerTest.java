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

package org.drools.workbench.screens.scenariosimulation.businesscentral.client.handlers;

import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage.CoverageReportPresenter;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.testrunner.TestRunnerReportingPanelWrapper;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

import static org.drools.workbench.screens.scenariosimulation.businesscentral.client.handlers.ScenarioSimulationBusinessCentralDocksHandler.TEST_RUNNER_REPORTING_PANEL;
import static org.drools.workbench.screens.scenariosimulation.client.handlers.AbstractScenarioSimulationDocksHandler.SCESIMEDITOR_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationBusinessCentralDocksHandlerTest {

    @Mock
    private AuthoringEditorDock authoringWorkbenchDocksMock;
    @Mock
    private TestRunnerReportingPanelWrapper testRunnerReportingPanelWrapperMock;
    @Mock
    private CheatSheetPresenter cheatSheetPresenterMock;
    @Mock
    private TestToolsPresenter testToolsPresenterMock;
    @Mock
    private SettingsPresenter settingsPresenterMock;
    @Mock
    private CoverageReportPresenter coverageReportPresenterMock;
    @Mock
    private PlaceManager placeManagerMock;
    @Mock
    private IsWidget testRunnerReportingPanelWidgetMock;

    private ScenarioSimulationBusinessCentralDocksHandler scenarioSimulationBusinessCentralDocksHandlerSpy;

    private enum MANAGED_DOCKS {
        SETTINGS,
        TOOLS,
        CHEATSHEET,
        REPORT,
        COVERAGE;
    }

    @Before
    public void setup() {
        scenarioSimulationBusinessCentralDocksHandlerSpy = spy(new ScenarioSimulationBusinessCentralDocksHandler() {

            {
                this.authoringWorkbenchDocks = authoringWorkbenchDocksMock;
                this.testRunnerReportingPanelWrapper = testRunnerReportingPanelWrapperMock;
                this.placeManager = placeManagerMock;
                this.coverageReportPresenter = coverageReportPresenterMock;
                this.settingsPresenter = settingsPresenterMock;
                this.cheatSheetPresenter = cheatSheetPresenterMock;
                this.testToolsPresenter = testToolsPresenterMock;
            }

        });
        when(testRunnerReportingPanelWrapperMock.asWidget()).thenReturn(testRunnerReportingPanelWidgetMock);
    }

    @Test
    public void provideDocks() {
        final Collection<UberfireDock> docks = scenarioSimulationBusinessCentralDocksHandlerSpy.provideDocks("id");
        assertEquals(MANAGED_DOCKS.values().length, docks.size());
        final UberfireDock testRunnerDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.REPORT.ordinal()];
        assertNotNull(testRunnerDock);
        assertEquals(TEST_RUNNER_REPORTING_PANEL, testRunnerDock.getPlaceRequest().getParameter("name", ""));
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.testReport(), testRunnerDock.getLabel());
        assertEquals(UberfireDockPosition.EAST, testRunnerDock.getDockPosition());
        assertEquals("PLAY_CIRCLE", testRunnerDock.getIconType());
        final UberfireDock coverageDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.COVERAGE.ordinal()];
        assertNotNull(coverageDock);
        assertEquals(CoverageReportPresenter.IDENTIFIER, coverageDock.getPlaceRequest().getIdentifier());
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.coverageReport(), coverageDock.getLabel());
        assertEquals(UberfireDockPosition.EAST, coverageDock.getDockPosition());
        assertEquals("BAR_CHART", coverageDock.getIconType());
        final UberfireDock cheatSheetDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.CHEATSHEET.ordinal()];
        assertNotNull(cheatSheetDock);
        assertEquals(CheatSheetPresenter.IDENTIFIER, cheatSheetDock.getPlaceRequest().getIdentifier());
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.scenarioCheatSheet(), cheatSheetDock.getLabel());
        assertEquals(UberfireDockPosition.EAST, cheatSheetDock.getDockPosition());
        assertEquals("FILE_TEXT", cheatSheetDock.getIconType());
        final UberfireDock settingsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.SETTINGS.ordinal()];
        assertNotNull(settingsDock);
        assertEquals(SettingsPresenter.IDENTIFIER, settingsDock.getPlaceRequest().getIdentifier());
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.settings(), settingsDock.getLabel());
        assertEquals(UberfireDockPosition.EAST, settingsDock.getDockPosition());
        assertEquals("SLIDERS", settingsDock.getIconType());
        final UberfireDock testToolsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.TOOLS.ordinal()];
        assertNotNull(testToolsDock);
        assertEquals(TestToolsPresenter.IDENTIFIER, testToolsDock.getPlaceRequest().getIdentifier());
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.testTools(), testToolsDock.getLabel());
        assertEquals(UberfireDockPosition.EAST, testToolsDock.getDockPosition());
        assertEquals("INFO_CIRCLE", testToolsDock.getIconType());
    }

    @Test
    public void expandTestResultsDock() {
        final Collection<UberfireDock> docks = scenarioSimulationBusinessCentralDocksHandlerSpy.provideDocks("id");
        final UberfireDock testRunnerDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.REPORT.ordinal()];

        scenarioSimulationBusinessCentralDocksHandlerSpy.expandTestResultsDock();
        verify(authoringWorkbenchDocksMock).expandAuthoringDock(eq(testRunnerDock));
    }

    @Test
    public void setScesimPath() {
        final Collection<UberfireDock> docks = scenarioSimulationBusinessCentralDocksHandlerSpy.provideDocks("id");
        final UberfireDock settingsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.SETTINGS.ordinal()];
        final UberfireDock toolsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.TOOLS.ordinal()];
        final UberfireDock cheatSheetDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.CHEATSHEET.ordinal()];
        final UberfireDock coverageDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.COVERAGE.ordinal()];
        String TEST_PATH = "TEST_PATH";
        scenarioSimulationBusinessCentralDocksHandlerSpy.setScesimEditorId(TEST_PATH);
        assertTrue(settingsDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, settingsDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
        assertTrue(toolsDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, toolsDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
        assertTrue(cheatSheetDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, cheatSheetDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
        assertTrue(coverageDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, coverageDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
    }

    @Test
    public void resetDocks() {
        scenarioSimulationBusinessCentralDocksHandlerSpy.resetDocks();
        verify(testRunnerReportingPanelWrapperMock, times(1)).reset();
        verify(coverageReportPresenterMock, times(1)).reset();
        verify(testToolsPresenterMock, times(1)).reset();
        verify(settingsPresenterMock, times(1)).reset();
        verify(cheatSheetPresenterMock, times(1)).reset();
    }

    @Test
    public void getTestRunnerReportingPanel() {
        IsWidget panel = scenarioSimulationBusinessCentralDocksHandlerSpy.getTestRunnerReportingPanelWidget();
        assertSame(testRunnerReportingPanelWidgetMock, panel);
    }

    @Test
    public void updateTestRunnerReportingPanelResult() {
        TestResultMessage testResultMessageMock = mock(TestResultMessage.class);
        scenarioSimulationBusinessCentralDocksHandlerSpy.updateTestRunnerReportingPanelResult(testResultMessageMock);
        verify(testRunnerReportingPanelWrapperMock, times(1)).onTestRun(eq(testResultMessageMock));
    }
}
