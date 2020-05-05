/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.Collection;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.mockito.Mock;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.PlaceRequest;

import static org.drools.workbench.screens.scenariosimulation.client.handlers.AbstractScenarioSimulationDocksHandler.SCESIMEDITOR_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractScenarioSimulationDocksHandlerTest {

    @Mock
    private AuthoringEditorDock authoringWorkbenchDocksMock;
    @Mock
    private CheatSheetPresenter cheatSheetPresenterMock;
    @Mock
    private TestToolsPresenter testToolsPresenterMock;
    @Mock
    private SettingsPresenter settingsPresenterMock;
    @Mock
    private PlaceManager placeManagerMock;

    private AbstractScenarioSimulationDocksHandler abstractScenarioSimulationDocksHandlerSpy;

    private enum MANAGED_DOCKS {
        SETTINGS,
        TOOLS,
        CHEATSHEET
    }

    @Before
    public void setup() {
        abstractScenarioSimulationDocksHandlerSpy = spy(new AbstractScenarioSimulationDocksHandler() {

            {
                this.authoringWorkbenchDocks = authoringWorkbenchDocksMock;
                this.placeManager = placeManagerMock;
            }

            @Override
            public void expandTestResultsDock() {
                //Do nothing
            }
        });

        doReturn(Optional.of(cheatSheetPresenterMock)).when(abstractScenarioSimulationDocksHandlerSpy).getCheatSheetPresenter();
        doReturn(Optional.of(testToolsPresenterMock)).when(abstractScenarioSimulationDocksHandlerSpy).getTestToolsPresenter();
        doReturn(Optional.of(settingsPresenterMock)).when(abstractScenarioSimulationDocksHandlerSpy).getSettingsPresenter();
    }

    @Test
    public void provideDocks() {
        final Collection<UberfireDock> docks = abstractScenarioSimulationDocksHandlerSpy.provideDocks("id");
        assertEquals(MANAGED_DOCKS.values().length, docks.size());
        final UberfireDock cheetSheetDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.CHEATSHEET.ordinal()];
        assertNotNull(cheetSheetDock);
        assertEquals(CheatSheetPresenter.IDENTIFIER, cheetSheetDock.getPlaceRequest().getIdentifier());
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.scenarioCheatSheet(), cheetSheetDock.getLabel());
        assertEquals(UberfireDockPosition.EAST, cheetSheetDock.getDockPosition());
        assertEquals("FILE_TEXT", cheetSheetDock.getIconType());
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
        assertEquals("INFO_CIRCLE", testToolsDock.getIconType());    }

    @Test
    public void expandToolsDock() {
        final Collection<UberfireDock> docks = abstractScenarioSimulationDocksHandlerSpy.provideDocks("id");
        final UberfireDock toolsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.TOOLS.ordinal()];

        abstractScenarioSimulationDocksHandlerSpy.expandToolsDock();

        verify(authoringWorkbenchDocksMock).expandAuthoringDock(eq(toolsDock));
    }

    @Test
    public void setScesimPath() {
        final Collection<UberfireDock> docks = abstractScenarioSimulationDocksHandlerSpy.provideDocks("id");
        final UberfireDock settingsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.SETTINGS.ordinal()];
        final UberfireDock toolsDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.TOOLS.ordinal()];
        final UberfireDock cheatSheetDock = (UberfireDock) docks.toArray()[MANAGED_DOCKS.CHEATSHEET.ordinal()];
        String TEST_PATH = "TEST_PATH";
        abstractScenarioSimulationDocksHandlerSpy.setScesimEditorId(TEST_PATH);
        assertTrue(settingsDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, settingsDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
        assertTrue(toolsDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, toolsDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
        assertTrue(cheatSheetDock.getPlaceRequest().getParameters().containsKey(SCESIMEDITOR_ID));
        assertEquals(TEST_PATH, cheatSheetDock.getPlaceRequest().getParameter(SCESIMEDITOR_ID, "null"));
    }

    @Test
    public void resetDocks() {
        abstractScenarioSimulationDocksHandlerSpy.resetDocks();
        verify(testToolsPresenterMock, times(1)).reset();
        verify(settingsPresenterMock, times(1)).reset();
        verify(cheatSheetPresenterMock, times(1)).reset();
    }

    @Test
    public void addDocks() {
        abstractScenarioSimulationDocksHandlerSpy.addDocks();
        verify(abstractScenarioSimulationDocksHandlerSpy, times(1)).refreshDocks(eq(true), eq(false));
    }

    @Test
    public void removeDocks() {
        abstractScenarioSimulationDocksHandlerSpy.removeDocks();
        verify(abstractScenarioSimulationDocksHandlerSpy, times(1)).refreshDocks(eq(true), eq(true));
    }

    @Test
    public void getCurrentRightDockPlaceRequest() {
        PlaceRequest placeRequest = abstractScenarioSimulationDocksHandlerSpy.getCurrentRightDockPlaceRequest("identifier");
        assertNotNull(placeRequest);
        assertEquals("identifier", placeRequest.getIdentifier());
        assertNotNull(placeRequest.getParameter(SCESIMEDITOR_ID, ""));
    }

    @Test
    public void getTestToolsView() {
        TestToolsView testToolsViewMock = mock(TestToolsView.class);
        AbstractWorkbenchActivity activityMock = mock(AbstractWorkbenchActivity.class);
        when(activityMock.getWidget()).thenReturn(testToolsViewMock);
        PlaceRequest placeRequest = abstractScenarioSimulationDocksHandlerSpy.getCurrentRightDockPlaceRequest(TestToolsPresenter.IDENTIFIER);
        when(placeManagerMock.getActivity(eq(placeRequest))).thenReturn(activityMock);
        Optional<TestToolsView> optional = abstractScenarioSimulationDocksHandlerSpy.getTestToolsView(placeRequest);
        assertSame(testToolsViewMock, optional.get());
    }

    @Test
    public void getTestToolsView_NullActivity() {
        PlaceRequest placeRequest = abstractScenarioSimulationDocksHandlerSpy.getCurrentRightDockPlaceRequest(TestToolsPresenter.IDENTIFIER);
        when(placeManagerMock.getActivity(eq(placeRequest))).thenReturn(null);
        Optional<TestToolsView> optional = abstractScenarioSimulationDocksHandlerSpy.getTestToolsView(placeRequest);
        assertFalse(optional.isPresent());
    }

    @Test
    public void getCheatSheetView() {
        CheatSheetView cheatSheetViewMock = mock(CheatSheetView.class);
        AbstractWorkbenchActivity activityMock = mock(AbstractWorkbenchActivity.class);
        when(activityMock.getWidget()).thenReturn(cheatSheetViewMock);
        PlaceRequest placeRequest = abstractScenarioSimulationDocksHandlerSpy.getCurrentRightDockPlaceRequest(CheatSheetPresenter.IDENTIFIER);
        when(placeManagerMock.getActivity(eq(placeRequest))).thenReturn(activityMock);
        Optional<CheatSheetView> optional = abstractScenarioSimulationDocksHandlerSpy.getCheatSheetView(placeRequest);
        assertSame(cheatSheetViewMock, optional.get());
    }

    @Test
    public void getCheatSheetView_NullActivity() {
        PlaceRequest placeRequest = abstractScenarioSimulationDocksHandlerSpy.getCurrentRightDockPlaceRequest(CheatSheetPresenter.IDENTIFIER);
        when(placeManagerMock.getActivity(eq(placeRequest))).thenReturn(null);
        Optional<CheatSheetView> optional = abstractScenarioSimulationDocksHandlerSpy.getCheatSheetView(placeRequest);
        assertFalse(optional.isPresent());
    }

    @Test
    public void getSettingsView() {
        SettingsView settingsViewMock = mock(SettingsView.class);
        AbstractWorkbenchActivity activityMock = mock(AbstractWorkbenchActivity.class);
        when(activityMock.getWidget()).thenReturn(settingsViewMock);
        PlaceRequest placeRequest = abstractScenarioSimulationDocksHandlerSpy.getCurrentRightDockPlaceRequest(SettingsPresenter.IDENTIFIER);
        when(placeManagerMock.getActivity(eq(placeRequest))).thenReturn(activityMock);
        Optional<SettingsView> optional = abstractScenarioSimulationDocksHandlerSpy.getSettingsView(placeRequest);
        assertSame(settingsViewMock, optional.get());
    }

    @Test
    public void getSettings_NullActivity() {
        PlaceRequest placeRequest = abstractScenarioSimulationDocksHandlerSpy.getCurrentRightDockPlaceRequest(SettingsPresenter.IDENTIFIER);
        when(placeManagerMock.getActivity(eq(placeRequest))).thenReturn(null);
        Optional<SettingsView> optional = abstractScenarioSimulationDocksHandlerSpy.getSettingsView(placeRequest);
        assertFalse(optional.isPresent());
    }
}