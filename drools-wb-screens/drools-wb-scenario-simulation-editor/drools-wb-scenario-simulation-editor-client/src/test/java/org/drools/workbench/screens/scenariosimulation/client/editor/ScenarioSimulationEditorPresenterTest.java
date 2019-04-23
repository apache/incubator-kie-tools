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

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.events.ImportEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.producers.ScenarioSimulationProducer;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.model.TestRunResult;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportType;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.kie.workbench.common.workbench.client.test.TestRunnerReportingPanel;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.editor.commons.client.file.exports.TextFileExport;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static junit.framework.TestCase.assertTrue;
import static org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler.SCESIMEDITOR_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationEditorPresenterTest extends AbstractScenarioSimulationEditorTest {

    private ScenarioSimulationEditorPresenter presenter;

    private ScenarioSimulationEditorPresenter presenterSpy;

    @Mock
    private KieEditorWrapperView kieViewMock;
    @Mock
    private OverviewWidgetPresenter overviewWidgetPresenterMock;
    @Mock
    private DefaultFileNameValidator fileNameValidatorMock;
    @Mock
    private AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilderMock;
    @Mock
    private EventSourceMock<NotificationEvent> notificationMock;
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
    private ImportsWidgetPresenter importsWidgetPresenterMock;
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
    private TestRunnerReportingPanel testRunnerReportingPanel;
    @Mock
    private ScenarioSimulationDocksHandler scenarioSimulationDocksHandlerMock;
    private Promises promises;
    @Mock
    private ScenarioMenuItem runScenarioMenuItemMock;
    @Mock
    private ScenarioMenuItem undoMenuItemMock;
    @Mock
    private ScenarioMenuItem redoMenuItemMock;
    @Mock
    private ScenarioMenuItem exportToCsvMenuItemMock;
    @Mock
    private DataManagementStrategy dataManagementStrategyMock;
    @Mock
    private DefaultEditorDock docksMock;
    @Mock
    private PerspectiveManager perspectiveManagerMock;
    @Mock
    private Command saveCommandMock;
    @Mock
    private TextFileExport textFileExportMock;

    @Before
    public void setup() {
        promises = new SyncPromises();
        super.setup();
        when(scenarioGridLayerMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioSimulationViewMock.getScenarioGridPanel()).thenReturn(scenarioGridPanelMock);
        when(scenarioSimulationViewMock.getScenarioGridLayer()).thenReturn(scenarioGridLayerMock);
        when(scenarioSimulationViewMock.getRunScenarioMenuItem()).thenReturn(runScenarioMenuItemMock);
        when(scenarioSimulationViewMock.getUndoMenuItem()).thenReturn(undoMenuItemMock);
        when(scenarioSimulationViewMock.getRedoMenuItem()).thenReturn(redoMenuItemMock);
        when(scenarioSimulationViewMock.getExportToCsvMenuItem()).thenReturn(exportToCsvMenuItemMock);
        when(scenarioGridPanelMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioSimulationProducerMock.getScenarioSimulationView()).thenReturn(scenarioSimulationViewMock);
        when(scenarioSimulationProducerMock.getScenarioSimulationContext()).thenReturn(contextMock);
        when(placeRequestMock.getIdentifier()).thenReturn(ScenarioSimulationEditorPresenter.IDENTIFIER);
        when(testToolsViewMock.getPresenter()).thenReturn(testToolsPresenterMock);
        when(testToolsActivityMock.getWidget()).thenReturn(testToolsViewMock);
        when(placeRequestMock.getPath()).thenReturn(pathMock);
        when(contextMock.getStatus()).thenReturn(statusMock);
        when(perspectiveManagerMock.getCurrentPerspective()).thenReturn(mock(PerspectiveActivity.class));

        this.presenter = new ScenarioSimulationEditorPresenter(new CallerMock<>(scenarioSimulationServiceMock),
                                                               scenarioSimulationProducerMock,
                                                               mock(ScenarioSimulationResourceType.class),
                                                               importsWidgetPresenterMock,
                                                               oracleFactoryMock,
                                                               placeManagerMock,
                                                               testRunnerReportingPanel,
                                                               scenarioSimulationDocksHandlerMock,
                                                               new CallerMock<>(dmnTypeServiceMock),
                                                               new CallerMock<>(importExportServiceMock),
                                                               textFileExportMock,
                                                               mock(ConfirmPopupPresenter.class)) {
            {
                this.kieView = kieViewMock;
                this.overviewWidget = overviewWidgetPresenterMock;
                this.fileMenuBuilder = fileMenuBuilderMock;
                this.fileNameValidator = fileNameValidatorMock;
                this.versionRecordManager = versionRecordManagerMock;
                this.notification = notificationMock;
                this.workbenchContext = workbenchContextMock;
                this.alertsButtonMenuItemBuilder = alertsButtonMenuItemBuilderMock;
                this.path = pathMock;
                this.scenarioGridPanel = scenarioGridPanelMock;
                this.packageName = SCENARIO_PACKAGE;
                this.eventBus = eventBusMock;
                this.context = contextMock;
                this.dataManagementStrategy = dataManagementStrategyMock;
                this.model = scenarioSimulationModelMock;
                this.docks = docksMock;
                this.perspectiveManager = perspectiveManagerMock;
                this.promises = ScenarioSimulationEditorPresenterTest.this.promises;
            }

            @Override
            protected MenuItem downloadMenuItem() {
                return mock(MenuItem.class);
            }

            @Override
            protected Command getSaveAndRename() {
                return mock(Command.class);
            }

            @Override
            protected void clearTestToolsStatus() {

            }

            @Override
            protected void open(String downloadURL) {

            }

            @Override
            protected String getJsonModel(ScenarioSimulationModel model) {
                return "";
            }
        };
        presenterSpy = spy(presenter);
    }

    @Test
    public void testPresenterInit() throws Exception {
        verify(scenarioSimulationViewMock).init(presenter);
    }

    @Test
    public void testOnStartup() {

        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);
        when(oracleFactoryMock.makeAsyncPackageDataModelOracle(any(),
                                                               eq(modelLocal),
                                                               eq(content.getDataModel()))).thenReturn(oracle);
        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));
        verify(testRunnerReportingPanel).reset();
        verify(importsWidgetPresenterMock).setContent(oracle,
                                                      modelLocal.getImports(),
                                                      false);
        verify(kieViewMock).addImportsTab(importsWidgetPresenterMock);
        verify(scenarioSimulationViewMock).showLoading();
        verify(scenarioSimulationViewMock).hideBusyIndicator();
    }

    @Test
    public void validateButtonShouldNotBeAdded() {
        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));
        verify(presenterSpy, never()).getValidateCommand();
    }

    @Test
    public void runScenarioButtonIsAdded() throws Exception {
        final MenuItem menuItem = mock(MenuItem.class);
        doReturn(menuItem).when(scenarioSimulationViewMock).getRunScenarioMenuItem();
        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));
        verify(fileMenuBuilderMock).addNewTopLevelMenu(menuItem);
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
    public void makeMenuBar() {
        presenter.makeMenuBar();
        verify(fileMenuBuilderMock, times(1)).addNewTopLevelMenu(runScenarioMenuItemMock);
        verify(fileMenuBuilderMock, times(1)).addNewTopLevelMenu(undoMenuItemMock);
        verify(fileMenuBuilderMock, times(1)).addNewTopLevelMenu(redoMenuItemMock);
        verify(fileMenuBuilderMock, times(1)).addNewTopLevelMenu(exportToCsvMenuItemMock);
        verify(undoMenuItemMock, times(1)).setEnabled(eq(false));
        verify(redoMenuItemMock, times(1)).setEnabled(eq(false));
    }

    @Test
    public void save() {
        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));
        reset(scenarioSimulationViewMock);
        presenter.save("save message");
        verify(scenarioSimulationViewMock).hideBusyIndicator();
        verify(notificationMock).fire(any(NotificationEvent.class));
        verify(versionRecordManagerMock).reloadVersions(any(Path.class));
    }

    @Test
    public void showDocks() {
        presenterSpy.showDocks();
        verify(scenarioSimulationDocksHandlerMock).addDocks();
        verify(scenarioSimulationDocksHandlerMock).setScesimEditorId(eq(String.valueOf(presenterSpy.scenarioPresenterId)));
        verify(presenterSpy).expandToolsDock();
        verify(presenterSpy, times(1)).registerTestToolsCallback();
        verify(presenterSpy, times(1)).populateRightDocks(eq(TestToolsPresenter.IDENTIFIER));
    }

    @Test
    public void hideDocks() {
        presenterSpy.hideDocks();
        verify(scenarioSimulationDocksHandlerMock).removeDocks();
        verify(scenarioGridMock, times(1)).clearSelections();
        verify(presenterSpy).unRegisterTestToolsCallback();
        verify(presenterSpy).clearTestToolsStatus();
        verify(testRunnerReportingPanel).reset();
    }

    @Test
    public void onUberfireDocksInteractionEventCheatSheet() {
        UberfireDocksInteractionEvent uberfireDocksInteractionEventMock = mock(UberfireDocksInteractionEvent.class);
        doReturn(false).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, never()).getTargetDock();
        //
        reset(presenterSpy);
        presenterSpy.dataManagementStrategy = null;
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(presenterSpy, never()).getCheatSheetPresenter(any());
        //
        reset(presenterSpy);
        reset(uberfireDocksInteractionEventMock);
        presenterSpy.dataManagementStrategy = dataManagementStrategyMock;
        UberfireDock targetDockMock = mock(UberfireDock.class);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        when(targetDockMock.getIdentifier()).thenReturn("UNKNOWN");
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock();
        verify(presenterSpy, never()).getCheatSheetPresenter(any());
        //
        PlaceRequest cheatSheetPlaceRequestMock = mock(PlaceRequest.class);
        reset(presenterSpy);
        reset(uberfireDocksInteractionEventMock);
        when(presenterSpy.getCurrentRightDockPlaceRequest(anyString())).thenReturn(cheatSheetPlaceRequestMock);
        presenterSpy.dataManagementStrategy = dataManagementStrategyMock;
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        doReturn(Optional.empty()).when(presenterSpy).getCheatSheetPresenter(eq(cheatSheetPlaceRequestMock));
        when(targetDockMock.getIdentifier()).thenReturn(CheatSheetPresenter.IDENTIFIER);
        when(targetDockMock.getPlaceRequest()).thenReturn(placeRequestMock);
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenterSpy, times(1)).getCheatSheetPresenter(eq(cheatSheetPlaceRequestMock));
        verify(presenterSpy, never()).setCheatSheet(eq(cheatSheetPresenterMock));
        //
        reset(presenterSpy);
        reset(uberfireDocksInteractionEventMock);
        when(presenterSpy.getCurrentRightDockPlaceRequest(anyString())).thenReturn(cheatSheetPlaceRequestMock);
        presenterSpy.dataManagementStrategy = dataManagementStrategyMock;
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        doReturn(Optional.of(cheatSheetPresenterMock)).when(presenterSpy).getCheatSheetPresenter(eq(cheatSheetPlaceRequestMock));
        when(targetDockMock.getIdentifier()).thenReturn(CheatSheetPresenter.IDENTIFIER);
        when(targetDockMock.getPlaceRequest()).thenReturn(placeRequestMock);
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenterSpy, times(1)).getCheatSheetPresenter(eq(cheatSheetPlaceRequestMock));
        verify(presenterSpy, times(1)).setCheatSheet(eq(cheatSheetPresenterMock));
    }

    @Test
    public void onUberfireDocksInteractionEventSettings() {
        UberfireDocksInteractionEvent uberfireDocksInteractionEventMock = mock(UberfireDocksInteractionEvent.class);
        doReturn(false).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, never()).getTargetDock();
        //
        reset(presenterSpy);
        presenterSpy.dataManagementStrategy = null;
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(presenterSpy, never()).getCheatSheetPresenter(any());
        //
        PlaceRequest settingsPlaceRequestMock = mock(PlaceRequest.class);
        reset(presenterSpy);
        reset(uberfireDocksInteractionEventMock);
        when(presenterSpy.getCurrentRightDockPlaceRequest(anyString())).thenReturn(settingsPlaceRequestMock);
        presenterSpy.dataManagementStrategy = dataManagementStrategyMock;
        UberfireDock targetDockMock = mock(UberfireDock.class);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        when(targetDockMock.getIdentifier()).thenReturn("UNKNOWN");
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock();
        verify(presenterSpy, never()).getCheatSheetPresenter(any());
        //
        reset(presenterSpy);
        reset(uberfireDocksInteractionEventMock);
        presenterSpy.dataManagementStrategy = dataManagementStrategyMock;
        when(presenterSpy.getCurrentRightDockPlaceRequest(anyString())).thenReturn(settingsPlaceRequestMock);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        doReturn(Optional.empty()).when(presenterSpy).getSettingsPresenter(eq(settingsPlaceRequestMock));
        when(targetDockMock.getIdentifier()).thenReturn(SettingsPresenter.IDENTIFIER);
        when(targetDockMock.getPlaceRequest()).thenReturn(placeRequestMock);
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenterSpy, times(1)).getSettingsPresenter(eq(settingsPlaceRequestMock));
        verify(presenterSpy, never()).setSettings(eq(settingsPresenterMock));
        //
        reset(presenterSpy);
        reset(uberfireDocksInteractionEventMock);
        presenterSpy.dataManagementStrategy = dataManagementStrategyMock;
        when(presenterSpy.getCurrentRightDockPlaceRequest(anyString())).thenReturn(settingsPlaceRequestMock);
        when(uberfireDocksInteractionEventMock.getTargetDock()).thenReturn(targetDockMock);
        doReturn(true).when(presenterSpy).isUberfireDocksInteractionEventToManage(uberfireDocksInteractionEventMock);
        doReturn(Optional.of(settingsPresenterMock)).when(presenterSpy).getSettingsPresenter(eq(settingsPlaceRequestMock));
        when(targetDockMock.getIdentifier()).thenReturn(SettingsPresenter.IDENTIFIER);
        when(targetDockMock.getPlaceRequest()).thenReturn(placeRequestMock);
        presenterSpy.onUberfireDocksInteractionEvent(uberfireDocksInteractionEventMock);
        verify(presenterSpy, times(1)).isUberfireDocksInteractionEventToManage(eq(uberfireDocksInteractionEventMock));
        verify(uberfireDocksInteractionEventMock, times(2)).getTargetDock(); // It's invoked twice
        verify(presenterSpy, times(1)).getSettingsPresenter(eq(settingsPlaceRequestMock));
        verify(presenterSpy, times(1)).getSaveCommand();
        verify(presenterSpy, times(1)).setSettings(eq(settingsPresenterMock));
    }

    @Test
    public void onClose() {
        when(placeManagerMock.getStatus(placeRequestMock)).thenReturn(PlaceStatus.OPEN);
        presenter.onClose();
        onClosePlaceStatusOpen();
        reset(scenarioGridPanelMock);
        reset(versionRecordManagerMock);
        reset(placeManagerMock);
        reset(scenarioSimulationViewMock);
        when(placeManagerMock.getStatus(placeRequestMock)).thenReturn(PlaceStatus.CLOSE);
        presenter.onClose();
        onClosePlaceStatusClose();
    }

    @Test
    public void onRunTest() throws Exception {
        doReturn(new ScenarioSimulationModelContent(modelLocal,
                                                    new Overview(),
                                                    new PackageDataModelOracleBaselinePayload())).when(scenarioSimulationServiceMock).loadContent(any());
        when(scenarioSimulationServiceMock.runScenario(any(), any(), any())).thenReturn(new TestRunResult(scenarioMapMock,
                                                                                                          new TestResultMessage()));
        when(statusMock.getSimulation()).thenReturn(simulationMock);
        when(contextMock.getStatus()).thenReturn(statusMock);
        assertFalse(modelLocal.getSimulation().equals(simulationMock));
        presenter.onStartup(observablePathMock, placeRequestMock);
        presenter.onRunScenario();
        verify(scenarioSimulationServiceMock, times(1)).runScenario(any(), any(), any());
        verify(scenarioGridModelMock, times(1)).resetErrors();
        verify(scenarioSimulationViewMock, times(1)).refreshContent(any());
        verify(scenarioSimulationDocksHandlerMock).expandTestResultsDock();
        assertTrue(modelLocal.getSimulation().equals(simulationMock));
    }

    @Test
    public void onRunTestById() throws Exception {
        when(scenarioSimulationServiceMock.runScenario(any(), any(), any())).thenReturn(new TestRunResult(Collections.EMPTY_MAP,
                                                                                                          new TestResultMessage()));
        when(simulationMock.getScenarioByIndex(anyInt())).thenReturn(mock(Scenario.class));
        presenter.onRunScenario(Collections.singletonList(0));
        verify(scenarioSimulationServiceMock, times(1)).runScenario(any(), any(), any());
        verify(scenarioGridModelMock, times(1)).resetErrors();
        verify(scenarioSimulationViewMock, times(1)).refreshContent(any());
        verify(scenarioSimulationDocksHandlerMock).expandTestResultsDock();
    }

    @Test
    public void refreshModelContent() {
        when(scenarioSimulationModelMock.getSimulation()).thenReturn(simulationMock);
        Set<Map.Entry<Integer, Scenario>> entries = new HashSet<>();
        int scenarioNumber = 1;
        int scenarioIndex = scenarioNumber - 1;
        entries.add(new AbstractMap.SimpleEntry<>(scenarioNumber, new Scenario()));
        when(scenarioMapMock.entrySet()).thenReturn(entries);
        presenter.refreshModelContent(new TestRunResult(scenarioMapMock,
                                                        new TestResultMessage()));
        verify(simulationMock, times(1)).replaceScenario(eq(scenarioIndex), any());
        assertEquals(scenarioSimulationModelMock, presenter.getModel());
        verify(scenarioSimulationViewMock, times(1)).refreshContent(eq(simulationMock));
        verify(statusMock, times(1)).setSimulation(eq(simulationMock));
        verify(dataManagementStrategyMock, times(1)).setModel(eq(scenarioSimulationModelMock));
    }

    @Test
    public void isDirty() {
        when(scenarioSimulationViewMock.getScenarioGridPanel()).thenThrow(new RuntimeException());
        assertFalse(presenter.isDirty());
    }

    @Test
    public void onDownload() {
        String DOWNLOAD_URL = "DOWNLOAD_URL";
        Supplier<Path> pathSupplierMock = mock(Supplier.class);
        doReturn(DOWNLOAD_URL).when(presenterSpy).getFileDownloadURL(eq(pathSupplierMock));
        presenterSpy.onDownload(pathSupplierMock);
        verify(presenterSpy, times(1)).getFileDownloadURL(eq(pathSupplierMock));
        verify(presenterSpy, times(1)).open(eq(DOWNLOAD_URL));
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
        verify(dataManagementStrategyMock, times(1)).populateTestTools(testToolsPresenterMock, scenarioGridModelMock);
    }

    @Test
    public void setCheatSheet() {
        presenter.setCheatSheet(cheatSheetPresenterMock);
        verify(cheatSheetPresenterMock, times(1)).initCheatSheet(any());
    }

    @Test
    public void setSettings() {
        Command saveCommandMock = mock(Command.class);
        when(presenterSpy.getSaveCommand()).thenReturn(saveCommandMock);
        presenterSpy.setSettings(settingsPresenterMock);
        verify(settingsPresenterMock, times(1)).setScenarioType(any(), any(), anyString());
        verify(settingsPresenterMock, times(1)).setSaveCommand(eq(saveCommandMock));
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
    public void getModelSuccessCallbackMethod() {
        presenterSpy.getModelSuccessCallbackMethod(content);
        verify(presenterSpy, times(1)).populateRightDocks(TestToolsPresenter.IDENTIFIER);
        verify(presenterSpy, times(1)).populateRightDocks(SettingsPresenter.IDENTIFIER);
        verify(scenarioSimulationViewMock, times(1)).hideBusyIndicator();
        verify(scenarioSimulationViewMock, times(1)).setContent(eq(content.getModel().getSimulation()));
        verify(statusMock, times(1)).setSimulation(eq(content.getModel().getSimulation()));
        verify(presenterSpy, times(1)).setOriginalHash(anyInt());
    }

    @Test
    public void onExportToCsv() {
        presenter.onExportToCsv();
        verify(importExportServiceMock, times(1)).exportSimulation(eq(ImportExportType.CSV), any());
        verify(textFileExportMock, times(1)).export(any(), anyString());
    }

    @Test
    public void onImport() {
        when(importExportServiceMock.importSimulation(any(), any(), any())).thenReturn(new Simulation());
        String FILE_CONTENT = "FILE_CONTENT";
        presenterSpy.onImport(FILE_CONTENT);
        verify(importExportServiceMock, times(1)).importSimulation(eq(ImportExportType.CSV), eq(FILE_CONTENT), any());
    }

    private void onClosePlaceStatusOpen() {
        verify(versionRecordManagerMock, times(1)).clear();
        verify(scenarioGridPanelMock, times(1)).unregister();
    }

    private void onClosePlaceStatusClose() {
        verify(versionRecordManagerMock, times(1)).clear();
        verify(placeManagerMock, times(0)).closePlace(placeRequestMock);
        verify(scenarioGridPanelMock, times(1)).unregister();
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
        scenario.addMappingValue(test1.getFactIdentifier(), test1.getExpressionIdentifier(), "value");
        scenario.addMappingValue(test2.getFactIdentifier(), test2.getExpressionIdentifier(), "value");

        presenter.cleanReadOnlyColumn(simulation);

        assertNotNull(scenario.getFactMappingValue(test1.getFactIdentifier(), test1.getExpressionIdentifier()).get().getRawValue());
        assertNull(scenario.getFactMappingValue(test2.getFactIdentifier(), test2.getExpressionIdentifier()).get().getRawValue());
    }
}
