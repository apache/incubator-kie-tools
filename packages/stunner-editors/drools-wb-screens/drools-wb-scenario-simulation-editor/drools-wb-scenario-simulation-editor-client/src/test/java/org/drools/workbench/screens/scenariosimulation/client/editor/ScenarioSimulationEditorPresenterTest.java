/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.drools.workbench.screens.scenariosimulation.client.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.BackgroundData;
import org.drools.scenariosimulation.api.model.BackgroundDataWithIndex;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.ImportEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.AbstractScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.producers.AbstractScenarioSimulationProducer;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.drools.workbench.screens.scenariosimulation.utils.ScenarioSimulationI18nServerMessage;
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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.editor.commons.client.file.exports.TextFileExport;
import org.uberfire.workbench.events.NotificationEvent;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LOWER_CASE_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationEditorPresenterTest extends AbstractScenarioSimulationEditorTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Mock
    private ScenarioSimulationEditorWrapper scenarioSimulationEditorWrapperMock;
    @Mock
    private ScenarioSimulationView scenarioSimulationViewMock;
    @Mock
    private AbstractScenarioSimulationProducer abstractScenarioSimulationProducerMock;
    @Mock
    private TestToolsView testToolsViewMock;
    @Mock
    private ObservablePath pathMock;
    @Mock
    private AbstractScenarioSimulationDocksHandler abstractScenarioSimulationDocksHandlerMock;
    @Mock
    private DataManagementStrategy dataManagementStrategyMock;
    @Mock
    private TextFileExport textFileExportMock;
    @Mock
    private ConfirmPopupPresenter confirmPopupPresenterMock;
    @Captor
    private ArgumentCaptor<List<ScenarioWithIndex>> scenarioWithIndexCaptor;
    @Captor
    private ArgumentCaptor<ScenarioNotificationEvent> scenarioNotificationEventArgumentCaptor;

    private ScenarioSimulationEditorPresenter presenterSpy;

    @Before
    public void setup() {
        super.setup();
        when(abstractScenarioSimulationProducerMock.getScenarioSimulationView()).thenReturn(scenarioSimulationViewMock);
        when(scenarioSimulationViewMock.getScenarioGridWidget()).thenReturn(scenarioGridWidgetSpy);
        when(abstractScenarioSimulationProducerMock.getScenarioBackgroundGridWidget()).thenReturn(backgroundGridWidgetSpy);
        when(testToolsViewMock.getPresenter()).thenReturn(testToolsPresenterMock);
        when(simulationMock.getUnmodifiableData()).thenReturn(Arrays.asList(new Scenario()));
        when(abstractScenarioSimulationDocksHandlerMock.getTestToolsPresenter()).thenReturn(testToolsPresenterMock);
        when(abstractScenarioSimulationDocksHandlerMock.getSettingsPresenter()).thenReturn(settingsPresenterMock);

        this.presenterSpy = spy(new ScenarioSimulationEditorPresenter(abstractScenarioSimulationProducerMock,
                                                                      mock(ScenarioSimulationResourceType.class),
                                                                      abstractScenarioSimulationDocksHandlerMock,
                                                                      textFileExportMock,
                                                                      confirmPopupPresenterMock) {
            {
                this.path = pathMock;
                this.packageName = SCENARIO_PACKAGE;
                this.eventBus = eventBusMock;
                this.context = scenarioSimulationContextLocal;
                this.dataManagementStrategy = dataManagementStrategyMock;
                this.model = scenarioSimulationModelMock;
                this.scenarioSimulationEditorWrapper = scenarioSimulationEditorWrapperMock;
            }

            @Override
            protected void open(String downloadURL) {

            }

            @Override
            public String getJsonModel(ScenarioSimulationModel model) {
                return "";
            }
        });
    }

    @Test
    public void setWrapper() {
        presenterSpy.setWrapper(scenarioSimulationEditorWrapperMock);
        assertSame(scenarioSimulationEditorWrapperMock, presenterSpy.scenarioSimulationEditorWrapper);
    }

    @Test
    public void setPath() {
        presenterSpy.setPath(observablePathMock);
        assertSame(observablePathMock, presenterSpy.path);
    }

   @Test
    public void onClose() {
        presenterSpy.onClose();
        verify(scenarioGridWidgetSpy, times(1)).unregister();
        verify(backgroundGridWidgetSpy, times(1)).unregister();
    }

    @Test
    public void initializeDocks_PlaceStatusOpen() {
        presenterSpy.initializeDocks();
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).addDocks();
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).setScesimEditorId(String.valueOf(presenterSpy.scenarioPresenterId));
        verify(presenterSpy, times(1)).registerTestToolsCallback();
        verify(presenterSpy, times(1)).resetDocks();
    }

    @Test
    public void initializeDocks_PlaceStatusClose() {
        presenterSpy.initializeDocks();
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).addDocks();
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).setScesimEditorId(String.valueOf(presenterSpy.scenarioPresenterId));
        verify(presenterSpy, times(1)).expandToolsDock();
        verify(presenterSpy, times(1)).registerTestToolsCallback();
        verify(presenterSpy, times(1)).resetDocks();
    }

    @Test
    public void hideDocks() {
        presenterSpy.hideDocks();
        verify(abstractScenarioSimulationDocksHandlerMock).removeDocks();
        verify(scenarioGridWidgetSpy, times(1)).clearSelections();
        verify(backgroundGridWidgetSpy, times(1)).clearSelections();
        verify(presenterSpy).unRegisterTestToolsCallback();
        verify(presenterSpy).clearTestToolsStatus();
    }

    @Test
    public void expandToolsDock() {
        presenterSpy.expandToolsDock();
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).expandToolsDock();
    }

    @Test
    public void reloadTestTools_NotDisable() {
        presenterSpy.reloadTestTools(false);
        verify(presenterSpy, times(1)).populateRightDocks(TestToolsPresenter.IDENTIFIER);
        verify(abstractScenarioSimulationDocksHandlerMock, never()).getTestToolsPresenter();
        verify(testToolsPresenterMock, never()).onDisableEditorTab();
    }

    @Test
    public void reloadTestTools_Disable() {
        presenterSpy.reloadTestTools(true);
        verify(presenterSpy, times(1)).populateRightDocks(TestToolsPresenter.IDENTIFIER);
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).getTestToolsPresenter();
        verify(testToolsPresenterMock, times(1)).onDisableEditorTab();
    }

    @Test
    public void clearTestTools() {
        presenterSpy.clearTestToolsStatus();
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).getTestToolsPresenter();
        verify(testToolsPresenterMock, times(1)).onClearStatus();
    }

    @Test
    public void onRunTest() {
        presenterSpy.onRunScenario();
        verify(presenterSpy, times(1)).onRunScenario(Arrays.asList(0));
    }

    @Test
    public void onRunScenario() {
        scenarioWithIndexLocal.add(new ScenarioWithIndex(1, new Scenario()));
        scenarioWithIndexLocal.add(new ScenarioWithIndex(2, new Scenario()));
        scenarioWithIndexLocal.add(new ScenarioWithIndex(3, new Scenario()));
        when(simulationMock.getDataByIndex(anyInt())).thenReturn(mock(Scenario.class));
        List<Integer> indexList = Arrays.asList(0, 2);

        presenterSpy.onRunScenario(indexList);
        verify(scenarioGridWidgetSpy, times(1)).resetErrors();
        verify(backgroundGridWidgetSpy, times(1)).resetErrors();
        verify(scenarioSimulationModelMock, times(1)).setSimulation(simulationMock);
        verify(scenarioSimulationModelMock, times(1)).setBackground(backgroundMock);
        verify(scenarioSimulationViewMock, times(1)).showBusyIndicator(anyString());
        verify(scenarioSimulationEditorWrapperMock, times(1)).onRunScenario(any(), any(), any(), eq(settingsLocal), scenarioWithIndexCaptor.capture(), any());

        List<ScenarioWithIndex> capturedValue = scenarioWithIndexCaptor.getValue();
        assertEquals(2, capturedValue.size());

        for (Integer requestedIndex : indexList) {
            assertEquals(1, capturedValue.stream().filter(elem -> elem.getIndex() == (requestedIndex + 1)).count());
        }
    }

    @Test
    public void onUndo() {
        presenterSpy.onUndo();
        verify(eventBusMock, times(1)).fireEvent(isA(UndoEvent.class));
    }

    @Test
    public void onRedo() {
        presenterSpy.onRedo();
        verify(eventBusMock, times(1)).fireEvent(isA(RedoEvent.class));
    }

    @Test
    public void onImportSIMULATION() {
        String FILE_CONTENT = "FILE_CONTENT";
        presenterSpy.onImport(FILE_CONTENT, GridWidget.SIMULATION);
        verify(scenarioSimulationEditorWrapperMock, times(1)).onImport(eq(FILE_CONTENT), isA(RemoteCallback.class), isA(ErrorCallback.class), same(simulationMock));
    }

    @Test
    public void onImportBACKGROUND() {
        String FILE_CONTENT = "FILE_CONTENT";
        presenterSpy.onImport(FILE_CONTENT, GridWidget.BACKGROUND);
        verify(scenarioSimulationEditorWrapperMock, times(1)).onImport(eq(FILE_CONTENT), isA(RemoteCallback.class), isA(ErrorCallback.class), same(backgroundMock));
    }

    @Test
    public void onImportCheckSwitch() {
        // Test to verify there are not new, un-managed, GridWidget
        String FILE_CONTENT = "FILE_CONTENT";
        for (GridWidget gridWidget : GridWidget.values()) {
            presenterSpy.onImport(FILE_CONTENT, gridWidget);
        }
        verify(scenarioSimulationEditorWrapperMock, times(1)).onImport(eq(FILE_CONTENT),
                                                                       isA(RemoteCallback.class),
                                                                       isA(ErrorCallback.class),
                                                                       eq(simulationMock));
        verify(scenarioSimulationEditorWrapperMock, times(1)).onImport(eq(FILE_CONTENT),
                                                                       isA(RemoteCallback.class),
                                                                       isA(ErrorCallback.class),
                                                                       eq(backgroundMock));
    }

    @Test
    public void resetDocks() {
        presenterSpy.resetDocks();
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).resetDocks();
    }

    @Test
    public void onUberfireDocksInteractionEvent() {
        UberfireDocksInteractionEvent uberfireDocksInteractionEventMock = mock(UberfireDocksInteractionEvent.class);
        doReturn(false).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        verify(uberfireDocksInteractionEventMock, never()).getTargetDock();
        verify(presenterSpy, never()).populateRightDocks(anyString());
        verify(scenarioSimulationEditorWrapperMock, never()).populateDocks(anyString());
        //
        reset(presenterSpy);
        presenterSpy.dataManagementStrategy = null;
        doReturn(mock(UberfireDock.class)).when(uberfireDocksInteractionEventMock).getTargetDock();
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        verify(presenterSpy, never()).populateRightDocks(anyString());
        verify(scenarioSimulationEditorWrapperMock, never()).populateDocks(anyString());
        //
        reset(presenterSpy);
        reset(uberfireDocksInteractionEventMock);
        presenterSpy.dataManagementStrategy = dataManagementStrategyMock;
        UberfireDock targetDockMock = mock(UberfireDock.class);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        when(targetDockMock.getIdentifier()).thenReturn(SettingsPresenter.IDENTIFIER);
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenterSpy, times(1)).populateRightDocks(SettingsPresenter.IDENTIFIER);
        verify(scenarioSimulationEditorWrapperMock, times(1)).populateDocks(SettingsPresenter.IDENTIFIER);
        //
        reset(presenterSpy);
        reset(uberfireDocksInteractionEventMock);
        presenterSpy.dataManagementStrategy = dataManagementStrategyMock;
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        when(targetDockMock.getIdentifier()).thenReturn(CheatSheetPresenter.IDENTIFIER);
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenterSpy, times(1)).populateRightDocks(CheatSheetPresenter.IDENTIFIER);
        verify(scenarioSimulationEditorWrapperMock, times(1)).populateDocks(CheatSheetPresenter.IDENTIFIER);
        //
        reset(presenterSpy, scenarioSimulationEditorWrapperMock, uberfireDocksInteractionEventMock);
        presenterSpy.dataManagementStrategy = dataManagementStrategyMock;
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        when(targetDockMock.getIdentifier()).thenReturn(TestToolsPresenter.IDENTIFIER);
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        verify(uberfireDocksInteractionEventMock, times(1)).getTargetDock();
        verify(presenterSpy, never()).populateRightDocks(anyString());
        verify(scenarioSimulationEditorWrapperMock, never()).populateDocks(anyString());
    }

    @Test
    public void refreshModelContent() {
        when(scenarioSimulationModelMock.getSimulation()).thenReturn(simulationMock);
        List<ScenarioWithIndex> scenarioWithIndex = new ArrayList<>();
        int scenarioNumber = 1;
        int scenarioIndex = scenarioNumber - 1;
        Scenario scenario = mock(Scenario.class);
        scenarioWithIndex.add(new ScenarioWithIndex(scenarioNumber, scenario));

        List<BackgroundDataWithIndex> backgroundDataWithIndex = new ArrayList<>();
        BackgroundData backgroundData = mock(BackgroundData.class);
        backgroundDataWithIndex.add(new BackgroundDataWithIndex(scenarioNumber, backgroundData));

        TestResultMessage testResultMessage = mock(TestResultMessage.class);
        SimulationRunResult testRunResult = new SimulationRunResult(scenarioWithIndex, backgroundDataWithIndex, new SimulationRunMetadata(), testResultMessage);
        presenterSpy.refreshModelContent(testRunResult);
        verify(scenarioSimulationViewMock, times(1)).hideBusyIndicator();

        verify(simulationMock, times(1)).replaceData(scenarioIndex, scenario);
        assertEquals(scenarioSimulationModelMock, presenterSpy.getModel());
        verify(scenarioGridWidgetSpy, times(1)).refreshContent(simulationMock);
        assertEquals(scenarioSimulationContextLocal.getStatus().getSimulation(), simulationMock);

        verify(backgroundMock, times(1)).replaceData(scenarioIndex, backgroundData);
        verify(backgroundGridWidgetSpy, times(1)).refreshContent(backgroundMock);
        assertEquals(scenarioSimulationContextLocal.getStatus().getBackground(), backgroundMock);

        assertEquals(scenarioSimulationModelMock, presenterSpy.getModel());
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).expandTestResultsDock();
        verify(dataManagementStrategyMock, times(1)).setModel(scenarioSimulationModelMock);
        verify(scenarioSimulationEditorWrapperMock, times(1)).onRefreshedModelContent(testRunResult);
    }

    @Test
    public void isDirty() {
        when(scenarioSimulationViewMock.getScenarioGridWidget()).thenThrow(new RuntimeException());
        assertFalse(presenterSpy.isDirty());
    }

    @Test
    public void onEditTabSelected() {
        presenterSpy.onEditTabSelected();
        InOrder inOrder = inOrder(presenterSpy, scenarioGridWidgetSpy, backgroundGridWidgetSpy);
        inOrder.verify(scenarioGridWidgetSpy, times(1)).selectAndFocus();
        inOrder.verify(backgroundGridWidgetSpy, times(1)).deselectAndUnFocus();
    }

    @Test
    public void onBackgroundTabSelected() {
        presenterSpy.onBackgroundTabSelected();
        InOrder inOrder = inOrder(presenterSpy, scenarioGridWidgetSpy, backgroundGridWidgetSpy);
        inOrder.verify(backgroundGridWidgetSpy, times(1)).selectAndFocus();
        inOrder.verify(scenarioGridWidgetSpy, times(1)).deselectAndUnFocus();
    }

    @Test
    public void onImportsTabSelected() {
        presenterSpy.onImportsTabSelected();
        verify(scenarioGridWidgetSpy, times(1)).deselectAndUnFocus();
        verify(backgroundGridWidgetSpy, times(1)).deselectAndUnFocus();
    }

    @Test
    public void validateSimulation() {
        presenterSpy.validateSimulation();
        verify(scenarioSimulationEditorWrapperMock, times(1)).validate(eq(simulationMock), eq(settingsLocal), isA(RemoteCallback.class));
    }

    @Test
    public void selectSimulationTab() {
        presenterSpy.selectSimulationTab();
        verify(scenarioSimulationEditorWrapperMock, times(1)).selectSimulationTab();
    }

    @Test
    public void selectBackgroundTab() {
        presenterSpy.selectBackgroundTab();
        verify(scenarioSimulationEditorWrapperMock, times(1)).selectBackgroundTab();
    }

    @Test
    public void onDownload() {
        String DOWNLOAD_URL = "DOWNLOAD_URL";
        Supplier<Path> pathSupplierMock = mock(Supplier.class);
        doReturn(DOWNLOAD_URL).when(presenterSpy).getFileDownloadURL(pathSupplierMock);
        presenterSpy.onDownload(pathSupplierMock);
        verify(presenterSpy, times(1)).getFileDownloadURL(pathSupplierMock);
        verify(presenterSpy, times(1)).open(DOWNLOAD_URL);
    }

    @Test
    public void showImportDialogSIMULATION() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        presenterSpy.showImportDialog();
        verify(eventBusMock, times(1)).fireEvent(isA(ImportEvent.class));
    }

    @Test
    public void showImportDialogBACKGROUND() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(true);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        presenterSpy.showImportDialog();
        verify(eventBusMock, times(1)).fireEvent(isA(ImportEvent.class));
    }

    @Test
    public void showImportDialogNone() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        presenterSpy.showImportDialog();
        verify(eventBusMock, never()).fireEvent(isA(ImportEvent.class));
    }

    @Test
    public void setTestTools() {
        presenterSpy.setTestTools(testToolsPresenterMock);
        assertEquals(scenarioSimulationContextLocal.getTestToolsPresenter(), testToolsPresenterMock);
        verify(testToolsPresenterMock, times(1)).setEventBus(eventBusMock);
        verify(dataManagementStrategyMock, times(1)).populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal, GridWidget.SIMULATION);
    }

    @Test
    public void setCheatSheet() {
        presenterSpy.setCheatSheet(cheatSheetPresenterMock);
        verify(cheatSheetPresenterMock, times(1)).initCheatSheet(isA(ScenarioSimulationModel.Type.class));
    }

    @Test
    public void setSettings() {
        presenterSpy.setSettings(settingsPresenterMock);
        verify(settingsPresenterMock, times(1)).setScenarioType(isA(ScenarioSimulationModel.Type.class), any(), any());
    }

    @Test
    public void populateRightDocks() {
        presenterSpy.populateRightDocks(TestToolsPresenter.IDENTIFIER);
        verify(scenarioSimulationEditorWrapperMock, times(1)).populateDocks(TestToolsPresenter.IDENTIFIER);
    }

    @Test
    public void populateRightDocksEmptyDataStrategy() {
        presenterSpy.dataManagementStrategy = null;
        presenterSpy.populateRightDocks(TestToolsPresenter.IDENTIFIER);
        verify(scenarioSimulationEditorWrapperMock, never()).populateDocks(anyString());
    }

    @Test
    public void getModelSuccessCallbackMethod() {
        scenarioGridWidgetSpy.selectAndFocus();
        presenterSpy.getModelSuccessCallbackMethod(dataManagementStrategyMock, modelLocal);
        verify(presenterSpy, times(1)).initializeDocks();
        verify(presenterSpy, times(1)).populateRightDocks(TestToolsPresenter.IDENTIFIER);
        verify(presenterSpy, times(1)).populateRightDocks(SettingsPresenter.IDENTIFIER);
        verify(scenarioGridWidgetSpy, times(1)).setContent(modelLocal.getSimulation(), settingsLocal.getType());
        assertEquals(scenarioSimulationContextLocal.getStatus().getSimulation(), modelLocal.getSimulation());
        assertEquals(scenarioSimulationContextLocal.getStatus().getBackground(), modelLocal.getBackground());
        verify(presenterSpy, times(1)).getValidateCommand();
        verify(scenarioGridWidgetSpy, atLeastOnce()).selectAndFocus();
    }

    @Test
    public void onExportToCsvSIMULATION() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        presenterSpy.onExportToCsv();
        verify(scenarioSimulationEditorWrapperMock, times(1)).onExportToCsv(isA(RemoteCallback.class), isA(ScenarioSimulationHasBusyIndicatorDefaultErrorCallback.class), eq(simulationMock));
    }

    @Test
    public void cleanReadOnlyColumn() {
        Simulation simulation = new Simulation();
        ScesimModelDescriptor simulationDescriptor = simulation.getScesimModelDescriptor();
        FactMapping test1 = simulationDescriptor
                .addFactMapping(FactIdentifier.create("test1", String.class.getCanonicalName()),
                                ExpressionIdentifier.create("", FactMappingType.GIVEN));
        FactMapping test2 = simulationDescriptor
                .addFactMapping(FactIdentifier.create("test2", String.class.getCanonicalName()),
                                ExpressionIdentifier.create("", FactMappingType.GIVEN));

        test1.addExpressionElement("test", String.class.getCanonicalName());
        Scenario scenario = simulation.addData();
        scenario.addMappingValue(test1.getFactIdentifier(), test1.getExpressionIdentifier(), LOWER_CASE_VALUE);
        scenario.addMappingValue(test2.getFactIdentifier(), test2.getExpressionIdentifier(), LOWER_CASE_VALUE);

        presenterSpy.cleanReadOnlyColumn(simulation);

        assertNotNull(scenario.getFactMappingValue(test1.getFactIdentifier(), test1.getExpressionIdentifier()).get().getRawValue());
        assertNull(scenario.getFactMappingValue(test2.getFactIdentifier(), test2.getExpressionIdentifier()).get().getRawValue());
    }

    @Test
    public void getValidationCallback() {
        presenterSpy.getValidationCallback().callback(null);
        verify(confirmPopupPresenterMock, never()).show(anyString(), anyString());

        List<FactMappingValidationError> validationErrors = new ArrayList<>();
        presenterSpy.getValidationCallback().callback(validationErrors);
        verify(confirmPopupPresenterMock, never()).show(anyString(), anyString());

        String errorMessage = "errorMessage";
        String errorId = "errorId";
        String errorId2 = "errorId2";
        validationErrors.add(new FactMappingValidationError(errorId, errorMessage));
        validationErrors.add(new FactMappingValidationError("errorId2", ScenarioSimulationI18nServerMessage.SCENARIO_VALIDATION_NODE_CHANGED_ERROR, "p1", "p2"));
        presenterSpy.getValidationCallback().callback(validationErrors);
        verify(confirmPopupPresenterMock, times(1)).show(anyString(), contains(errorId));
        verify(confirmPopupPresenterMock, times(1)).show(anyString(), contains(errorMessage));
        verify(confirmPopupPresenterMock, times(1)).show(anyString(), contains(errorId2));
        verify(confirmPopupPresenterMock, times(1)).show(anyString(), contains(ScenarioSimulationEditorConstants.INSTANCE.scenarioValidationNodeChangedError("p1", "p2")));
    }

    @Test
    public void getImportCallback() {
        List<AbstractScesimModel> toTest = Arrays.asList(new Simulation(), new Background());

        for (AbstractScesimModel abstractScesimModel : toTest) {
            FactMapping factMapping = abstractScesimModel.getScesimModelDescriptor().addFactMapping(FactIdentifier.EMPTY, ExpressionIdentifier.create("empty", FactMappingType.GIVEN));
            FactMappingValue toBeRemoved = abstractScesimModel.addData().addOrUpdateMappingValue(factMapping.getFactIdentifier(), factMapping.getExpressionIdentifier(), "toBeRemoved");

            presenterSpy.getImportCallBack().callback(abstractScesimModel);

            verify(presenterSpy, times(1)).cleanReadOnlyColumn(abstractScesimModel);
            assertNull(toBeRemoved.getRawValue());

            reset(presenterSpy);
        }
    }

    @Test
    public void sendNotification() {
        presenterSpy.sendNotification("message", NotificationEvent.NotificationType.ERROR);
        verify(eventBusMock, times(1)).fireEvent(scenarioNotificationEventArgumentCaptor.capture());
        assertEquals("message", scenarioNotificationEventArgumentCaptor.getValue().getMessage());
        assertEquals(NotificationEvent.NotificationType.ERROR, scenarioNotificationEventArgumentCaptor.getValue().getNotificationType());
        assertTrue(scenarioNotificationEventArgumentCaptor.getValue().isAutoHide());
    }

    @Test
    public void sendNotificationAutoHide() {
        presenterSpy.sendNotification("message", NotificationEvent.NotificationType.ERROR, false);
        verify(eventBusMock, times(1)).fireEvent(scenarioNotificationEventArgumentCaptor.capture());
        assertEquals("message", scenarioNotificationEventArgumentCaptor.getValue().getMessage());
        assertEquals(NotificationEvent.NotificationType.ERROR, scenarioNotificationEventArgumentCaptor.getValue().getNotificationType());
        assertFalse(scenarioNotificationEventArgumentCaptor.getValue().isAutoHide());
    }

    @Test
    public void expandSettingsDock() {
        presenterSpy.expandSettingsDock();
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).expandSettingsDock();
    }

    @Test
    public void reloadSettingsDock() {
        presenterSpy.reloadSettingsDock();
        verify(abstractScenarioSimulationDocksHandlerMock, times(1)).getSettingsPresenter();
        verify(presenterSpy, times(1)).updateSettings(settingsPresenterMock);
    }

    @Test
    public void unpublishTestResultsAlerts(){
        presenterSpy.unpublishTestResultsAlerts();
        verify(scenarioSimulationEditorWrapperMock, times(1)).unpublishTestResultsAlerts();
    }

    @Test
    public void getUpdateDMNMetadataCommand() {
        presenterSpy.getUpdateDMNMetadataCommand().execute();
        verify(scenarioSimulationEditorWrapperMock, times(1)).getDMNMetadata();
    }
}
