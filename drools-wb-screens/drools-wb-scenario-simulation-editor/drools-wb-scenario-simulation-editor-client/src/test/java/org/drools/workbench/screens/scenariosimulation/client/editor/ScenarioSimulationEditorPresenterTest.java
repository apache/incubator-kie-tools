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

package org.drools.workbench.screens.scenariosimulation.client.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.workbench.screens.scenariosimulation.client.MockProducer;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.dropdown.SettingsScenarioSimulationDropdown;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.events.ImportEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.producers.ScenarioSimulationProducer;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CoverageReportPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestRunnerReportingPanelWrapper;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.editor.commons.client.file.exports.TextFileExport;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LOWER_CASE_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler.SCESIMEDITOR_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(ScenarioMenuItem.class)
public class ScenarioSimulationEditorPresenterTest extends AbstractScenarioSimulationEditorTest {

    private ScenarioSimulationEditorPresenter presenter;

    @Mock
    private ScenarioSimulationEditorWrapper scenarioSimulationEditorWrapper;
    @Mock
    private ScenarioGrid scenarioGridMock;
    @Mock
    private ScenarioGridLayer scenarioGridLayerMock;
    @Mock
    private ScenarioSimulationView scenarioSimulationViewMock;
    @Mock
    private ScenarioGridModel scenarioGridModelMock;
    @Mock
    private ScenarioSimulationProducer scenarioSimulationProducerMock;
    @Mock
    private PlaceManager placeManagerMock;
    @Mock
    private AbstractWorkbenchActivity testToolsActivityMock;
    @Mock
    private TestToolsView testToolsViewMock;
    @Mock
    private ObservablePath pathMock;
    @Mock
    private PathPlaceRequest placeRequestMock;
    @Mock
    private ScenarioSimulationContext contextMock;
    @Mock
    private ScenarioSimulationContext.Status statusMock;
    @Mock
    private TestRunnerReportingPanelWrapper testRunnerReportingPanelMock;
    @Mock
    private IsWidget testRunnerReportingPanelWidgetMock;
    @Mock
    private ScenarioSimulationDocksHandler scenarioSimulationDocksHandlerMock;
    @Mock
    private ScenarioMenuItem runScenarioMenuItemMock;
    @Mock
    private ScenarioMenuItem undoMenuItemMock;
    @Mock
    private ScenarioMenuItem redoMenuItemMock;
    @Mock
    private ScenarioMenuItem exportToCsvMenuItemMock;
    @Mock
    private ScenarioMenuItem importMenuItemMock;
    @Mock
    private ScenarioMenuItem downloadMenuItemMock;
    @Mock
    private DataManagementStrategy dataManagementStrategyMock;
    @Mock
    private TextFileExport textFileExportMock;
    @Mock
    private ConfirmPopupPresenter confirmPopupPresenterMock;
    @Mock
    private ScenarioGridWidget scenarioBackgroundGridWidgetMock;
    @Captor
    private ArgumentCaptor<List<ScenarioWithIndex>> scenarioWithIndexCaptor;
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        super.setup();
        when(scenarioGridLayerMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioSimulationViewMock.getScenarioGridWidget()).thenReturn(scenarioGridWidgetMock);
        when(scenarioGridPanelMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioSimulationProducerMock.getScenarioSimulationView()).thenReturn(scenarioSimulationViewMock);
        when(scenarioSimulationProducerMock.getScenarioBackgroundGridWidget()).thenReturn(scenarioBackgroundGridWidgetMock);
        when(scenarioBackgroundGridWidgetMock.getScenarioSimulationContext()).thenReturn(contextMock);
        when(scenarioGridWidgetMock.getScenarioSimulationContext()).thenReturn(contextMock);
        when(placeRequestMock.getIdentifier()).thenReturn(ScenarioSimulationEditorPresenter.IDENTIFIER);
        when(testToolsViewMock.getPresenter()).thenReturn(testToolsPresenterMock);
        when(testToolsActivityMock.getWidget()).thenReturn(testToolsViewMock);
        when(placeRequestMock.getPath()).thenReturn(pathMock);
        when(contextMock.getStatus()).thenReturn(statusMock);
        when(statusMock.getSimulation()).thenReturn(simulationMock);
        when(simulationMock.getUnmodifiableScenarios()).thenReturn(Arrays.asList(new Scenario()));
        when(testRunnerReportingPanelMock.asWidget()).thenReturn(testRunnerReportingPanelWidgetMock);

        this.presenter = spy(new ScenarioSimulationEditorPresenter(scenarioSimulationProducerMock,
                                                                   mock(ScenarioSimulationResourceType.class),
                                                                   placeManagerMock,
                                                                   testRunnerReportingPanelMock,
                                                                   scenarioSimulationDocksHandlerMock,
                                                                   textFileExportMock,
                                                                   confirmPopupPresenterMock) {
            {
                this.path = pathMock;
                this.packageName = SCENARIO_PACKAGE;
                this.eventBus = eventBusMock;
                this.focusedContext = contextMock;
                this.dataManagementStrategy = dataManagementStrategyMock;
                this.model = scenarioSimulationModelMock;
                this.testRunnerReportingPanel = testRunnerReportingPanelMock;
                this.undoMenuItem = undoMenuItemMock;
                this.redoMenuItem = redoMenuItemMock;
                this.downloadMenuItem = downloadMenuItemMock;
                this.runScenarioMenuItem = runScenarioMenuItemMock;
                this.exportToCSVMenuItem = exportToCsvMenuItemMock;
                this.importMenuItem = importMenuItemMock;
            }

            @Override
            protected void clearTestToolsStatus() {

            }

            @Override
            protected void open(String downloadURL) {

            }

            @Override
            public String getJsonModel(ScenarioSimulationModel model) {
                return "";
            }
        });

        doReturn(Optional.of(cheatSheetPresenterMock)).when(presenter).getCheatSheetPresenter(eq(placeRequestMock));
        doReturn(Optional.of(testToolsPresenterMock)).when(presenter).getTestToolsPresenter(eq(placeRequestMock));
        doReturn(Optional.of(settingsPresenterMock)).when(presenter).getSettingsPresenter(eq(placeRequestMock));
        doReturn(Optional.of(coverageReportPresenterMock)).when(presenter).getCoverageReportPresenter(eq(placeRequestMock));
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(placeRequestMock);
    }

    @Test
    public void init() {
        presenter.init(scenarioSimulationEditorWrapper, observablePathMock);
        verify(testRunnerReportingPanelMock, times(1)).reset();
    }

    @Test
    public void setFocusedContext() {
        presenter.setFocusedContext(contextMock);
        verify(presenter, times(1)).populateRightDocks(eq(TestToolsPresenter.IDENTIFIER));
    }

    @Test
    public void setSaveEnabledTrue() {
        presenter.setSaveEnabled(true);
        assertTrue(presenter.saveEnabled);
        verify(settingsPresenterMock, times(1)).setSaveEnabled(eq(true));
    }

    @Test
    public void setSaveEnabledFalse() {
        presenter.setSaveEnabled(false);
        assertFalse(presenter.saveEnabled);
        verify(settingsPresenterMock, times(1)).setSaveEnabled(eq(false));
    }

    @Test
    public void setSaveEnabledPopulateSettingsCombinedTrue() {
        SettingsPresenter settingsPresenterSpy = getSettingsPresenterSpy();
        doReturn(Optional.of(settingsPresenterSpy)).when(presenter).getSettingsPresenter(eq(placeRequestMock));
        presenter.setSaveEnabled(true);
        presenter.populateRightDocks(SettingsPresenter.IDENTIFIER);
        assertTrue(presenter.saveEnabled);
        assertTrue(settingsPresenterSpy.isSaveEnabled());
        verify(settingsPresenterSpy.getView(), atLeastOnce()).restoreSaveButton();
        verify(settingsPresenterSpy.getView(), never()).removeSaveButton();
    }

    @Test
    public void setSaveEnabledPopulateSettingsCombinedFalse() {
        SettingsPresenter settingsPresenterSpy = getSettingsPresenterSpy();
        doReturn(Optional.of(settingsPresenterSpy)).when(presenter).getSettingsPresenter(eq(placeRequestMock));
        presenter.setSaveEnabled(false);
        presenter.populateRightDocks(SettingsPresenter.IDENTIFIER);
        assertFalse(presenter.saveEnabled);
        assertFalse(settingsPresenterSpy.isSaveEnabled());
        verify(settingsPresenterSpy.getView(), atLeastOnce()).removeSaveButton();
        verify(settingsPresenterSpy.getView(), never()).restoreSaveButton();
    }

    @Test
    public void setPopulateSettingsSaveEnabledCombinedTrue() {
        SettingsPresenter settingsPresenterSpy = getSettingsPresenterSpy();
        doReturn(Optional.of(settingsPresenterSpy)).when(presenter).getSettingsPresenter(eq(placeRequestMock));
        presenter.populateRightDocks(SettingsPresenter.IDENTIFIER);
        presenter.setSaveEnabled(true);
        assertTrue(presenter.saveEnabled);
        assertTrue(settingsPresenterSpy.isSaveEnabled());
        verify(settingsPresenterSpy.getView(), atLeastOnce()).restoreSaveButton();
        verify(settingsPresenterSpy.getView(), never()).removeSaveButton();
    }

    @Test
    public void setPopulateSettingsSaveEnabledCombinedFalse() {
        SettingsPresenter settingsPresenterSpy = getSettingsPresenterSpy();
        doReturn(Optional.of(settingsPresenterSpy)).when(presenter).getSettingsPresenter(eq(placeRequestMock));
        presenter.populateRightDocks(SettingsPresenter.IDENTIFIER);
        presenter.setSaveEnabled(false);
        assertFalse(presenter.saveEnabled);
        assertFalse(settingsPresenterSpy.isSaveEnabled());
        verify(settingsPresenterSpy.getView(), atLeastOnce()).removeSaveButton();
    }

    @Test
    public void onClose() {
        presenter.onClose();
        verify(scenarioGridWidgetMock, times(1)).unregister();
        verify(scenarioBackgroundGridWidgetMock, times(1)).unregister();
    }

    @Test
    public void showDocks_PlaceStatusOpen() {
        presenter.init(scenarioSimulationEditorWrapper, observablePathMock);
        presenter.showDocks(PlaceStatus.OPEN);
        verify(scenarioSimulationEditorWrapper, times(1)).wrappedRegisterDock(eq(ScenarioSimulationDocksHandler.TEST_RUNNER_REPORTING_PANEL), eq(testRunnerReportingPanelWidgetMock));
        verify(scenarioSimulationDocksHandlerMock, times(1)).addDocks();
        verify(scenarioSimulationDocksHandlerMock, times(1)).setScesimEditorId(eq(String.valueOf(presenter.scenarioPresenterId)));
        verify(presenter, never()).expandToolsDock();
        verify(presenter, times(1)).registerTestToolsCallback();
        verify(presenter, times(1)).resetDocks();
        verify(presenter, times(1)).populateRightDocks(eq(TestToolsPresenter.IDENTIFIER));
    }

    @Test
    public void showDocks_PlaceStatusClose() {
        presenter.init(scenarioSimulationEditorWrapper, observablePathMock);
        presenter.showDocks(PlaceStatus.CLOSE);
        verify(scenarioSimulationEditorWrapper, times(1)).wrappedRegisterDock(eq(ScenarioSimulationDocksHandler.TEST_RUNNER_REPORTING_PANEL), eq(testRunnerReportingPanelWidgetMock));
        verify(scenarioSimulationDocksHandlerMock, times(1)).addDocks();
        verify(scenarioSimulationDocksHandlerMock, times(1)).setScesimEditorId(eq(String.valueOf(presenter.scenarioPresenterId)));
        verify(presenter, times(1)).expandToolsDock();
        verify(presenter, times(1)).registerTestToolsCallback();
        verify(presenter, times(1)).resetDocks();
        verify(presenter, times(1)).populateRightDocks(eq(TestToolsPresenter.IDENTIFIER));
    }

    @Test
    public void hideDocks() {
        presenter.hideDocks();
        verify(scenarioSimulationDocksHandlerMock).removeDocks();
        verify(scenarioGridWidgetMock, times(1)).clearSelections();
        verify(scenarioBackgroundGridWidgetMock, times(1)).clearSelections();
        verify(presenter).unRegisterTestToolsCallback();
        verify(presenter).clearTestToolsStatus();
    }

    @Test
    public void expandToolsDock() {
        presenter.expandToolsDock();
        verify(scenarioSimulationDocksHandlerMock, times(1)).expandToolsDock();
    }

    @Test
    public void reloadTestTools_NotDisable() {
        presenter.reloadTestTools(false);
        verify(presenter, never()).getTestToolsPresenter(any());
    }

    @Test
    public void reloadTestTools_Disable() {
        presenter.reloadTestTools(true);
        verify(presenter, times(1)).getTestToolsPresenter(eq(placeRequestMock));
    }

    @Test
    public void onRunTest() {
        presenter.init(scenarioSimulationEditorWrapper, observablePathMock);
        presenter.onRunScenario();
        verify(presenter, times(1)).onRunScenario(eq(Arrays.asList(0)));
    }

    @Test
    public void onRunScenario() {
        scenarioWithIndexLocal.add(new ScenarioWithIndex(1, new Scenario()));
        scenarioWithIndexLocal.add(new ScenarioWithIndex(2, new Scenario()));
        scenarioWithIndexLocal.add(new ScenarioWithIndex(3, new Scenario()));
        when(simulationMock.getScenarioByIndex(anyInt())).thenReturn(mock(Scenario.class));
        List<Integer> indexList = Arrays.asList(0, 2);

        presenter.init(scenarioSimulationEditorWrapper, observablePathMock);
        presenter.onRunScenario(indexList);
        verify(scenarioGridWidgetMock, times(1)).resetErrors();
        verify(scenarioSimulationModelMock, times(1)).setSimulation(simulationMock);
        verify(scenarioSimulationViewMock, times(1)).showBusyIndicator(anyString());
        verify(scenarioSimulationEditorWrapper, times(1)).onRunScenario(any(), any(), any(), scenarioWithIndexCaptor.capture());

        List<ScenarioWithIndex> capturedValue = scenarioWithIndexCaptor.getValue();
        assertEquals(2, capturedValue.size());

        for (Integer requestedIndex : indexList) {
            assertEquals(1, capturedValue.stream().filter(elem -> elem.getIndex() == (requestedIndex + 1)).count());
        }
    }

    @Test
    public void onUndo() {
        presenter.onUndo();
        verify(eventBusMock, times(1)).fireEvent(isA(UndoEvent.class));
    }

    @Test
    public void onRedo() {
        presenter.onRedo();
        verify(eventBusMock, times(1)).fireEvent(isA(RedoEvent.class));
    }

    @Test
    public void setUndoButtonEnabledStatus() {
        presenter.setUndoButtonEnabledStatus(true);
        verify(undoMenuItemMock, times(1)).setEnabled(eq(true));
        //
        reset(undoMenuItemMock);
        presenter.setUndoButtonEnabledStatus(false);
        verify(undoMenuItemMock, times(1)).setEnabled(eq(false));
    }

    @Test
    public void setRedoButtonEnabledStatus() {
        presenter.setRedoButtonEnabledStatus(true);
        verify(redoMenuItemMock, times(1)).setEnabled(eq(true));
        //
        reset(redoMenuItemMock);
        presenter.setRedoButtonEnabledStatus(false);
        verify(redoMenuItemMock, times(1)).setEnabled(eq(false));
    }

    @Test
    public void setItemMenuForMainGridEnabled() {
        presenter.setItemMenuForMainGridEnabled(false);
        verify(runScenarioMenuItemMock, times(1)).setEnabled(eq(false));
        verify(importMenuItemMock, times(1)).setEnabled(eq(false));
        verify(exportToCsvMenuItemMock, times(1)).setEnabled(eq(false));
        verify(downloadMenuItemMock, times(1)).setEnabled(eq(false));
        //
        presenter.setItemMenuForMainGridEnabled(true);
        verify(runScenarioMenuItemMock, times(1)).setEnabled(eq(true));
        verify(importMenuItemMock, times(1)).setEnabled(eq(true));
        verify(exportToCsvMenuItemMock, times(1)).setEnabled(eq(true));
        verify(downloadMenuItemMock, times(1)).setEnabled(eq(true));
    }

    @Test
    public void onImport() {
        String FILE_CONTENT = "FILE_CONTENT";
        presenter.init(scenarioSimulationEditorWrapper, observablePathMock);
        presenter.onImport(FILE_CONTENT);
        verify(scenarioSimulationEditorWrapper, times(1)).onImport(eq(FILE_CONTENT), isA(RemoteCallback.class), isA(ErrorCallback.class), same(simulationMock));
    }

    @Test
    public void resetDocks() {
        presenter.resetDocks();
        verify(presenter, times(1)).getCheatSheetPresenter(eq(placeRequestMock));
        verify(cheatSheetPresenterMock, times(1)).reset();
        verify(presenter, times(1)).getTestToolsPresenter(eq(placeRequestMock));
        verify(testToolsPresenterMock, times(1)).reset();
        verify(presenter, times(1)).getSettingsPresenter(eq(placeRequestMock));
        verify(settingsPresenterMock, times(1)).reset();
        verify(presenter, times(1)).getCoverageReportPresenter(eq(placeRequestMock));
        verify(coverageReportPresenterMock, times(1)).reset();
    }

    @Test
    public void onUberfireDocksInteractionEventCheatSheet() {
        UberfireDocksInteractionEvent uberfireDocksInteractionEventMock = mock(UberfireDocksInteractionEvent.class);
        doReturn(false).when(presenter).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, never()).getTargetDock();
        //
        reset(presenter);
        presenter.dataManagementStrategy = null;
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(presenter, never()).getCheatSheetPresenter(any());
        //
        reset(presenter);
        reset(uberfireDocksInteractionEventMock);
        presenter.dataManagementStrategy = dataManagementStrategyMock;
        UberfireDock targetDockMock = mock(UberfireDock.class);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenter).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        when(targetDockMock.getIdentifier()).thenReturn("UNKNOWN");
        exceptionRule.expect(IllegalArgumentException.class);
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock();
        verify(presenter, never()).getCheatSheetPresenter(any());
        //
        PlaceRequest cheatSheetPlaceRequestMock = mock(PlaceRequest.class);
        reset(presenter);
        reset(uberfireDocksInteractionEventMock);
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(cheatSheetPlaceRequestMock);
        presenter.dataManagementStrategy = dataManagementStrategyMock;
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenter).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        doReturn(Optional.empty()).when(presenter).getCheatSheetPresenter(eq(cheatSheetPlaceRequestMock));
        when(targetDockMock.getIdentifier()).thenReturn(CheatSheetPresenter.IDENTIFIER);
        when(targetDockMock.getPlaceRequest()).thenReturn(placeRequestMock);
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenter, times(1)).getCheatSheetPresenter(eq(cheatSheetPlaceRequestMock));
        verify(presenter, never()).setCheatSheet(eq(cheatSheetPresenterMock));
        //
        reset(presenter);
        reset(uberfireDocksInteractionEventMock);
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(cheatSheetPlaceRequestMock);
        presenter.dataManagementStrategy = dataManagementStrategyMock;
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenter).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        doReturn(Optional.of(cheatSheetPresenterMock)).when(presenter).getCheatSheetPresenter(eq(cheatSheetPlaceRequestMock));
        when(targetDockMock.getIdentifier()).thenReturn(CheatSheetPresenter.IDENTIFIER);
        when(targetDockMock.getPlaceRequest()).thenReturn(placeRequestMock);
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenter, times(1)).getCheatSheetPresenter(eq(cheatSheetPlaceRequestMock));
        verify(presenter, times(1)).setCheatSheet(eq(cheatSheetPresenterMock));
    }

    @Test
    public void onUberfireDocksInteractionEventSettings() {
        UberfireDocksInteractionEvent uberfireDocksInteractionEventMock = mock(UberfireDocksInteractionEvent.class);
        doReturn(false).when(presenter).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, never()).getTargetDock();
        //
        reset(presenter);
        presenter.dataManagementStrategy = null;
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(presenter, never()).getCheatSheetPresenter(any());
        //
        PlaceRequest settingsPlaceRequestMock = mock(PlaceRequest.class);
        reset(presenter);
        reset(uberfireDocksInteractionEventMock);
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(settingsPlaceRequestMock);
        presenter.dataManagementStrategy = dataManagementStrategyMock;
        UberfireDock targetDockMock = mock(UberfireDock.class);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenter).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        exceptionRule.expect(IllegalArgumentException.class);
        when(targetDockMock.getIdentifier()).thenReturn("UNKNOWN");
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock();
        verify(presenter, never()).getCheatSheetPresenter(any());
        //
        reset(presenter);
        reset(uberfireDocksInteractionEventMock);
        presenter.dataManagementStrategy = dataManagementStrategyMock;
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(settingsPlaceRequestMock);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenter).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        doReturn(Optional.empty()).when(presenter).getSettingsPresenter(eq(settingsPlaceRequestMock));
        when(targetDockMock.getIdentifier()).thenReturn(SettingsPresenter.IDENTIFIER);
        when(targetDockMock.getPlaceRequest()).thenReturn(placeRequestMock);
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenter, times(1)).getSettingsPresenter(eq(settingsPlaceRequestMock));
        verify(presenter, never()).setSettings(eq(settingsPresenterMock));
        //
        reset(presenter);
        reset(uberfireDocksInteractionEventMock);
        presenter.dataManagementStrategy = dataManagementStrategyMock;
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(settingsPlaceRequestMock);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenter).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        doReturn(Optional.of(settingsPresenterMock)).when(presenter).getSettingsPresenter(eq(settingsPlaceRequestMock));
        when(targetDockMock.getIdentifier()).thenReturn(SettingsPresenter.IDENTIFIER);
        when(targetDockMock.getPlaceRequest()).thenReturn(placeRequestMock);
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenter, times(1)).getSettingsPresenter(eq(settingsPlaceRequestMock));
        verify(presenter, times(1)).getSaveCommand();
        verify(presenter, times(1)).setSettings(eq(settingsPresenterMock));
        //
        PlaceRequest coverageReportPlaceRequestMock = mock(PlaceRequest.class);
        reset(presenter);
        reset(uberfireDocksInteractionEventMock);
        presenter.dataManagementStrategy = dataManagementStrategyMock;
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(coverageReportPlaceRequestMock);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenter).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        doReturn(Optional.of(coverageReportPresenterMock)).when(presenter).getCoverageReportPresenter(eq(coverageReportPlaceRequestMock));
        when(targetDockMock.getIdentifier()).thenReturn(CoverageReportPresenter.IDENTIFIER);
        when(targetDockMock.getPlaceRequest()).thenReturn(placeRequestMock);
        presenter.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenter, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenter, times(1)).getCoverageReportPresenter(eq(coverageReportPlaceRequestMock));
        verify(presenter, times(1)).setCoverageReport(eq(coverageReportPresenterMock));
    }

    @Test
    public void isUberfireDocksInteractionEventToManage() {
        UberfireDocksInteractionEvent uberfireDocksInteractionEventMock = mock(UberfireDocksInteractionEvent.class);
        doReturn(null).when(uberfireDocksInteractionEventMock).getTargetDock();
        assertFalse(presenter.isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock));
        //
        UberfireDock targetDockMock = mock(UberfireDock.class);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        when(targetDockMock.getPlaceRequest()).thenReturn(placeRequestMock);
        when(placeRequestMock.getParameter(eq(SCESIMEDITOR_ID), eq(""))).thenReturn("UNKNOWN");
        assertFalse(presenter.isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock));
        doReturn(String.valueOf(presenter.scenarioPresenterId)).when(placeRequestMock).getParameter(eq(SCESIMEDITOR_ID), eq(""));
        assertTrue(presenter.isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock));
    }

    @Test
    public void refreshModelContent() {
        when(scenarioSimulationModelMock.getSimulation()).thenReturn(simulationMock);
        List<ScenarioWithIndex> entries = new ArrayList<>();
        int scenarioNumber = 1;
        int scenarioIndex = scenarioNumber - 1;
        Scenario scenario = mock(Scenario.class);
        entries.add(new ScenarioWithIndex(scenarioNumber, scenario));

        assertNull(presenter.lastRunResult);
        TestResultMessage testResultMessage = mock(TestResultMessage.class);
        presenter.refreshModelContent(new SimulationRunResult(entries, new SimulationRunMetadata(), testResultMessage));
        verify(scenarioSimulationViewMock, times(1)).hideBusyIndicator();
        verify(simulationMock, times(1)).replaceScenario(eq(scenarioIndex), eq(scenario));
        assertEquals(scenarioSimulationModelMock, presenter.getModel());
        verify(scenarioGridWidgetMock, times(1)).refreshContent(eq(simulationMock));
        verify(scenarioSimulationDocksHandlerMock, times(1)).expandTestResultsDock();
        verify(dataManagementStrategyMock, times(1)).setModel(eq(scenarioSimulationModelMock));
        verify(testRunnerReportingPanelMock, times(1)).onTestRun(eq(testResultMessage));
        assertNotNull(presenter.lastRunResult);
    }

    @Test
    public void makeMenuBar() {
        presenter.makeMenuBar(fileMenuBuilderMock);
        verify(fileMenuBuilderMock, times(1)).addValidate(any());
        verify(presenter, times(1)).getValidateCommand();
        verify(fileMenuBuilderMock, times(1)).addNewTopLevelMenu(runScenarioMenuItemMock);
        verify(fileMenuBuilderMock, times(1)).addNewTopLevelMenu(undoMenuItemMock);
        verify(fileMenuBuilderMock, times(1)).addNewTopLevelMenu(redoMenuItemMock);
        verify(fileMenuBuilderMock, times(1)).addNewTopLevelMenu(exportToCsvMenuItemMock);
        verify(undoMenuItemMock, times(1)).setEnabled(eq(false));
        verify(redoMenuItemMock, times(1)).setEnabled(eq(false));
    }

    @Test
    public void isDirty() {
        when(scenarioSimulationViewMock.getScenarioGridWidget()).thenThrow(new RuntimeException());
        assertFalse(presenter.isDirty());
    }

    @Test
    public void onDownload() {
        String DOWNLOAD_URL = "DOWNLOAD_URL";
        Supplier<Path> pathSupplierMock = mock(Supplier.class);
        doReturn(DOWNLOAD_URL).when(presenter).getFileDownloadURL(eq(pathSupplierMock));
        presenter.onDownload(pathSupplierMock);
        verify(presenter, times(1)).getFileDownloadURL(eq(pathSupplierMock));
        verify(presenter, times(1)).open(eq(DOWNLOAD_URL));
    }

    @Test
    public void showImportDialog() {
        presenter.showImportDialog();
        verify(eventBusMock, times(1)).fireEvent(isA(ImportEvent.class));
    }

    @Test
    public void setTestTools() {
        presenter.setTestTools(testToolsPresenterMock);
        verify(contextMock, times(1)).setTestToolsPresenter(testToolsPresenterMock);
        verify(testToolsPresenterMock, times(1)).setEventBus(eventBusMock);
        verify(dataManagementStrategyMock, times(1)).populateTestTools(eq(testToolsPresenterMock), eq(contextMock));
    }

    @Test
    public void setCheatSheet() {
        presenter.setCheatSheet(cheatSheetPresenterMock);
        verify(cheatSheetPresenterMock, times(1)).initCheatSheet(any());
    }

    @Test
    public void setSettings() {
        Command saveCommandMock = mock(Command.class);
        when(presenter.getSaveCommand()).thenReturn(saveCommandMock);
        presenter.setSettings(settingsPresenterMock);
        verify(settingsPresenterMock, times(1)).setScenarioType(any(), any(), anyString());
        verify(settingsPresenterMock, times(1)).setSaveCommand(eq(saveCommandMock));
    }

    @Test
    public void populateRightDocks_Settings() {
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(placeRequestMock);
        doReturn(Optional.of(settingsPresenterMock)).when(presenter).getSettingsPresenter(eq(placeRequestMock));
        presenter.populateRightDocks(SettingsPresenter.IDENTIFIER);
        verify(presenter, times(1)).setSettings(eq(settingsPresenterMock));
        verify(settingsPresenterMock, times(1)).setCurrentPath(eq(pathMock));
        verify(presenter, never()).setTestTools(any());
        verify(presenter, never()).setCoverageReport(any());
        verify(coverageReportPresenterMock, never()).setCurrentPath(any());
    }

    @Test
    public void populateRightDocks_TestTools() {
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(placeRequestMock);
        doReturn(Optional.of(testToolsPresenterMock)).when(presenter).getTestToolsPresenter(eq(placeRequestMock));
        presenter.populateRightDocks(TestToolsPresenter.IDENTIFIER);
        verify(presenter, never()).setSettings(any());
        verify(settingsPresenterMock, never()).setCurrentPath(any());
        verify(presenter, times(1)).setTestTools(eq(testToolsPresenterMock));
        verify(cheatSheetPresenterMock, never()).setCurrentPath(any());
        verify(presenter, never()).setCheatSheet(any());
        verify(presenter, never()).setCoverageReport(any());
        verify(coverageReportPresenterMock, never()).setCurrentPath(any());
    }

    @Test
    public void populateRightDocks_CheatSheetPresenter_NotShown() {
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(placeRequestMock);
        doReturn(Optional.of(testToolsPresenterMock)).when(presenter).getTestToolsPresenter(eq(placeRequestMock));
        presenter.populateRightDocks(CheatSheetPresenter.IDENTIFIER);
        verify(presenter, never()).setSettings(any());
        verify(settingsPresenterMock, never()).setCurrentPath(any());
        verify(presenter, never()).setTestTools(any());
        verify(cheatSheetPresenterMock, times(1)).setCurrentPath(pathMock);
        verify(presenter, times(1)).setCheatSheet(eq(cheatSheetPresenterMock));
        verify(presenter, never()).setCoverageReport(any());
        verify(coverageReportPresenterMock, never()).setCurrentPath(any());
    }

    @Test
    public void populateRightDocks_CheatSheetPresenter_IsShown() {
        when(cheatSheetPresenterMock.isCurrentlyShow(pathMock)).thenReturn(true);
        when(presenter.getCurrentRightDockPlaceRequest(anyString())).thenReturn(placeRequestMock);
        doReturn(Optional.of(testToolsPresenterMock)).when(presenter).getTestToolsPresenter(eq(placeRequestMock));
        presenter.populateRightDocks(CheatSheetPresenter.IDENTIFIER);
        verify(presenter, never()).setSettings(any());
        verify(settingsPresenterMock, never()).setCurrentPath(any());
        verify(presenter, never()).setTestTools(any());
        verify(cheatSheetPresenterMock, never()).setCurrentPath(any());
        verify(presenter, never()).setCheatSheet(any());
        verify(presenter, never()).setCoverageReport(any());
        verify(coverageReportPresenterMock, never()).setCurrentPath(any());
    }

    @Test
    public void populateRightDocks_CoverageReportPresenter() {
        presenter.populateRightDocks(CoverageReportPresenter.IDENTIFIER);
        verify(presenter, never()).setSettings(any());
        verify(settingsPresenterMock, never()).setCurrentPath(any());
        verify(presenter, never()).setTestTools(any());
        verify(cheatSheetPresenterMock, never()).setCurrentPath(any());
        verify(presenter, never()).setCheatSheet(any());
        verify(presenter, times(1)).setCoverageReport(eq(coverageReportPresenterMock));
        verify(coverageReportPresenterMock, times(1)).setCurrentPath(eq(pathMock));
    }

    @Test
    public void getModelSuccessCallbackMethod() {
        presenter.init(scenarioSimulationEditorWrapper, observablePathMock);
        presenter.getModelSuccessCallbackMethod(dataManagementStrategyMock, modelLocal);
        verify(presenter, times(1)).populateRightDocks(TestToolsPresenter.IDENTIFIER);
        verify(presenter, times(1)).populateRightDocks(SettingsPresenter.IDENTIFIER);
        verify(scenarioGridWidgetMock, times(1)).setContent(eq(content.getModel().getSimulation()));
        verify(scenarioSimulationEditorWrapper, times(1)).addBackgroundPage(eq(scenarioBackgroundGridWidgetMock));
        verify(scenarioBackgroundGridWidgetMock, times(1)).setContent(isA(Simulation.class));
        verify(statusMock, times(1)).setSimulation(eq(content.getModel().getSimulation()));
        verify(presenter, times(1)).getValidateCommand();
    }

    @Test
    public void onExportToCsv() {
        presenter.init(scenarioSimulationEditorWrapper, observablePathMock);
        presenter.onExportToCsv();
        verify(scenarioSimulationEditorWrapper, times(1)).onExportToCsv(isA(RemoteCallback.class), isA(ScenarioSimulationHasBusyIndicatorDefaultErrorCallback.class), eq(simulationMock));
    }

    @Test
    public void cleanReadOnlyColumn() {
        Simulation simulation = new Simulation();
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        FactMapping test1 = simulationDescriptor
                .addFactMapping(FactIdentifier.create("test1", String.class.getCanonicalName()),
                                ExpressionIdentifier.create("", FactMappingType.GIVEN));
        FactMapping test2 = simulationDescriptor
                .addFactMapping(FactIdentifier.create("test2", String.class.getCanonicalName()),
                                ExpressionIdentifier.create("", FactMappingType.GIVEN));

        test1.addExpressionElement("test", String.class.getCanonicalName());
        Scenario scenario = simulation.addScenario();
        scenario.addMappingValue(test1.getFactIdentifier(), test1.getExpressionIdentifier(), LOWER_CASE_VALUE);
        scenario.addMappingValue(test2.getFactIdentifier(), test2.getExpressionIdentifier(), LOWER_CASE_VALUE);

        presenter.cleanReadOnlyColumn(simulation);

        assertNotNull(scenario.getFactMappingValue(test1.getFactIdentifier(), test1.getExpressionIdentifier()).get().getRawValue());
        assertNull(scenario.getFactMappingValue(test2.getFactIdentifier(), test2.getExpressionIdentifier()).get().getRawValue());
    }

    @Test
    public void getValidationCallback() {
        presenter.getValidationCallback().callback(null);
        verify(confirmPopupPresenterMock, never()).show(anyString(), anyString());

        List<FactMappingValidationError> validationErrors = new ArrayList<>();
        presenter.getValidationCallback().callback(validationErrors);
        verify(confirmPopupPresenterMock, never()).show(anyString(), anyString());

        String errorMessage = "errorMessage";
        String errorId = "errorId";
        validationErrors.add(new FactMappingValidationError(errorId, errorMessage));
        presenter.getValidationCallback().callback(validationErrors);
        verify(confirmPopupPresenterMock, times(1)).show(anyString(), contains(errorId));
        verify(confirmPopupPresenterMock, times(1)).show(anyString(), contains(errorMessage));
    }

    private SettingsPresenter getSettingsPresenterSpy() {
        return spy(new SettingsPresenter(mock(SettingsScenarioSimulationDropdown.class), MockProducer.getSettingsViewMock()));
    }
}
