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

package org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.handlers.ScenarioSimulationBusinessCentralDocksHandler;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage.CoverageReportPresenter;
import org.drools.workbench.screens.scenariosimulation.businesscentral.client.rightpanel.coverage.CoverageReportView;
import org.drools.workbench.screens.scenariosimulation.client.TestProperties;
import org.drools.workbench.screens.scenariosimulation.client.editor.AbstractScenarioSimulationEditorTest;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.AbstractDMODataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.model.DMNMetadata;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportService;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportType;
import org.drools.workbench.screens.scenariosimulation.service.RunnerReportService;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.guvnor.messageconsole.events.UnpublishMessagesEvent;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorViewImpl;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type.DMN;
import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type.RULE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
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
public class ScenarioSimulationEditorBusinessCentralWrapperTest extends AbstractScenarioSimulationEditorTest {

    private static final int BACKGROUND_TAB_INDEX = 1;
    private static final int SIMULATION_TAB_INDEX = 0;
    private static final String SESSION_ID = "session-id-123";

    @Mock
    private PathPlaceRequest placeRequestMock;
    @Mock
    private ScenarioSimulationResourceType scenarioSimulationResourceType;
    @Mock
    private KieEditorWrapperView kieViewMock;
    @Mock
    private ImportsWidgetPresenter importsWidgetPresenterMock;
    @Mock
    private PlaceManager placeManagerMock;
    @Mock
    private AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilderMock;
    @Mock
    private OverviewWidgetPresenter overviewWidgetPresenterMock;
    @Mock
    private EventSourceMock<NotificationEvent> notificationMock;
    @Mock
    private DefaultFileNameValidator fileNameValidatorMock;
    @Mock
    private DefaultEditorDock docksMock;
    @Mock
    private PerspectiveManager perspectiveManagerMock;
    @Mock
    private Command populateTestToolsCommand;
    @Mock
    private MenuItem versionRecordMenuItemMock;
    @Mock
    private MenuItem alertsButtonMenuItemMock;
    @Mock
    private Metadata metaDataMock;
    @Mock
    private AssetUpdateValidator assetUpdateValidatorMock;
    @Mock
    private ProjectController projectControllerMock;
    @Mock
    private MultiPageEditor multiPageEditorMock;
    @Mock
    private MultiPageEditorViewImpl multiPageEditorViewMock;
    @Mock
    private NavTabs navTabsMock;
    @Mock
    private TabListItem simulationTabListItemMock;
    @Mock
    private TabListItem backgroundTabListItemMock;
    @Mock
    private ScenarioSimulationBusinessCentralDocksHandler scenarioSimulationBusinessCentralDocksHandlerMock;
    @Mock
    private IsWidget testRunnerReportingPanelWidgetMock;
    @Mock
    private SimulationRunResult simulationRunResultMock;
    @Mock
    private TestResultMessage testResultMessageMock;
    @Mock
    private SimulationRunMetadata simulationRunMetadataMock;
    @Mock
    private RemoteCallback<String> exportCallBackMock;
    @Mock
    private CoverageReportPresenter coverageReportPresenterMock;
    @Mock
    private ObservablePath pathMock;
    @Mock
    private Event<UnpublishMessagesEvent> unpublishMessagesEventMock;
    @Mock
    private SessionInfo sessionInfoMock;
    @Captor
    private ArgumentCaptor<Command> commandArgumentCaptor;
    @Captor
    private ArgumentCaptor<UnpublishMessagesEvent> unpublishMessagesEventArgumentCaptor;

    private CallerMock<ScenarioSimulationService> scenarioSimulationCaller;
    private CallerMock<ImportExportService> importExportCaller;
    private CallerMock<RunnerReportService> runnerReportServiceCaller;
    private CallerMock<DMNTypeService> dmnTypeServiceCaller;
    private Promises promises;
    private ScenarioSimulationEditorBusinessCentralWrapper scenarioSimulationEditorBusinessClientWrapper;
    private SaveAndRenameCommandBuilder<ScenarioSimulationModel, Metadata> saveAndRenameCommandBuilderMock;

    @Before
    public void setup() {
        super.setup();
        promises = new SyncPromises();
        scenarioSimulationCaller = spy(new CallerMock<>(scenarioSimulationServiceMock));
        importExportCaller = spy(new CallerMock<>(importExportServiceMock));
        runnerReportServiceCaller = spy(new CallerMock<>(runnerReportServiceMock));
        dmnTypeServiceCaller = spy(new CallerMock<>(dmnTypeServiceMock));
        saveAndRenameCommandBuilderMock = spy(new SaveAndRenameCommandBuilder<>(null, null, null, null));
        scenarioSimulationEditorBusinessClientWrapper = spy(new ScenarioSimulationEditorBusinessCentralWrapper(scenarioSimulationCaller,
                                                                                                               scenarioSimulationEditorPresenterMock,
                                                                                                               importsWidgetPresenterMock,
                                                                                                               oracleFactoryMock,
                                                                                                               placeManagerMock,
                                                                                                               dmnTypeServiceCaller,
                                                                                                               importExportCaller,
                                                                                                               runnerReportServiceCaller,
                                                                                                               sessionInfoMock,
                                                                                                               unpublishMessagesEventMock,
                                                                                                               scenarioSimulationBusinessCentralDocksHandlerMock) {
            {
                this.kieView = kieViewMock;
                this.overviewWidget = overviewWidgetPresenterMock;
                this.fileMenuBuilder = fileMenuBuilderMock;
                this.fileNameValidator = fileNameValidatorMock;
                this.versionRecordManager = versionRecordManagerMock;
                this.notification = notificationMock;
                this.workbenchContext = workbenchContextMock;
                this.alertsButtonMenuItemBuilder = alertsButtonMenuItemBuilderMock;
                this.docks = docksMock;
                this.perspectiveManager = perspectiveManagerMock;
                this.baseView = scenarioSimulationViewMock;
                this.promises = ScenarioSimulationEditorBusinessCentralWrapperTest.this.promises;
                this.metadata = metaDataMock;
                this.saveAndRenameCommandBuilder = saveAndRenameCommandBuilderMock;
                this.assetUpdateValidator = assetUpdateValidatorMock;
                this.projectController = projectControllerMock;
                this.place = placeRequestMock;
            }
        });
        when(placeRequestMock.getPath()).thenReturn(observablePathMock);
        when(scenarioSimulationEditorPresenterMock.getType()).thenReturn(scenarioSimulationResourceType);
        when(scenarioSimulationEditorPresenterMock.getPopulateTestToolsCommand()).thenReturn(populateTestToolsCommand);
        when(scenarioSimulationEditorPresenterMock.getJsonModel(any())).thenReturn("");
        when(scenarioSimulationEditorPresenterMock.getView()).thenReturn(scenarioSimulationViewMock);
        when(scenarioSimulationEditorPresenterMock.getModel()).thenReturn(scenarioSimulationModelMock);
        when(scenarioSimulationEditorPresenterMock.getContext()).thenReturn(scenarioSimulationContextLocal);
        when(scenarioSimulationEditorPresenterMock.getExportCallBack()).thenReturn(exportCallBackMock);
        when(scenarioSimulationEditorPresenterMock.getPath()).thenReturn(pathMock);
        when(alertsButtonMenuItemBuilderMock.build()).thenReturn(alertsButtonMenuItemMock);
        when(versionRecordManagerMock.buildMenu()).thenReturn(versionRecordMenuItemMock);
        when(scenarioGridWidgetSpy.getScenarioSimulationContext()).thenReturn(scenarioSimulationContextLocal);
        when(kieViewMock.getMultiPage()).thenReturn(multiPageEditorMock);
        when(multiPageEditorMock.getView()).thenReturn(multiPageEditorViewMock);
        when(multiPageEditorViewMock.getTabBar()).thenReturn(navTabsMock);
        when(multiPageEditorViewMock.getPageIndex(CommonConstants.INSTANCE.EditTabTitle())).thenReturn(SIMULATION_TAB_INDEX);
        when(multiPageEditorViewMock.getPageIndex(ScenarioSimulationEditorConstants.INSTANCE.backgroundTabTitle())).thenReturn(BACKGROUND_TAB_INDEX);
        when(navTabsMock.getWidget(SIMULATION_TAB_INDEX)).thenReturn(simulationTabListItemMock);
        when(navTabsMock.getWidget(BACKGROUND_TAB_INDEX)).thenReturn(backgroundTabListItemMock);
        when(scenarioSimulationBusinessCentralDocksHandlerMock.getCoverageReportPresenter()).thenReturn(coverageReportPresenterMock);
        when(scenarioSimulationBusinessCentralDocksHandlerMock.getTestRunnerReportingPanelWidget()).thenReturn(testRunnerReportingPanelWidgetMock);
        when(simulationRunResultMock.getTestResultMessage()).thenReturn(testResultMessageMock);
        when(simulationRunResultMock.getSimulationRunMetadata()).thenReturn(simulationRunMetadataMock);
        when(sessionInfoMock.getId()).thenReturn(SESSION_ID);
    }

    @Test
    public void onStartup() {
        scenarioSimulationEditorBusinessClientWrapper.onStartup(observablePathMock, placeRequestMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setWrapper(eq(scenarioSimulationEditorBusinessClientWrapper));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPath(eq(observablePathMock));
    }

    @Test
    public void onClose() {
        scenarioSimulationEditorBusinessClientWrapper.onClose();
        verify(versionRecordManagerMock, times(1)).clear();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onClose();
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).unpublishTestResultsAlerts();
    }

    @Test
    public void mayClose() {
        scenarioSimulationEditorBusinessClientWrapper.mayClose();
        verify(scenarioSimulationEditorPresenterMock, times(1)).isDirty();
    }

    @Test
    public void showDocks() {
        PlaceStatus placeStatusMock = mock(PlaceStatus.class);
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER);
        when(placeManagerMock.getStatus(eq(placeRequest))).thenReturn(placeStatusMock);
        scenarioSimulationEditorBusinessClientWrapper.showDocks();
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).wrappedRegisterDock(eq(ScenarioSimulationBusinessCentralDocksHandler.TEST_RUNNER_REPORTING_PANEL), eq(testRunnerReportingPanelWidgetMock));
        verify(scenarioSimulationEditorPresenterMock, times(1)).showDocks(same(placeStatusMock));
        verify(scenarioSimulationBusinessCentralDocksHandlerMock, never()).updateTestRunnerReportingPanelResult(any());
    }

    @Test
    public void showDocks_WithLastRun() {
        PlaceStatus placeStatusMock = mock(PlaceStatus.class);
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER);
        when(placeManagerMock.getStatus(eq(placeRequest))).thenReturn(placeStatusMock);
        scenarioSimulationEditorBusinessClientWrapper.lastRunResult = simulationRunResultMock;
        scenarioSimulationEditorBusinessClientWrapper.showDocks();
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).wrappedRegisterDock(eq(ScenarioSimulationBusinessCentralDocksHandler.TEST_RUNNER_REPORTING_PANEL), eq(testRunnerReportingPanelWidgetMock));
        verify(scenarioSimulationEditorPresenterMock, times(1)).showDocks(same(placeStatusMock));
        verify(scenarioSimulationBusinessCentralDocksHandlerMock, times(1)).updateTestRunnerReportingPanelResult(eq(testResultMessageMock));
    }

    @Test
    public void onImport() {
        String FILE_CONTENT = "FILE_CONTENT";
        RemoteCallback<AbstractScesimModel> remoteCallback = mock(RemoteCallback.class);
        ErrorCallback<Object> errorCallBack = mock(ErrorCallback.class);
        scenarioSimulationEditorBusinessClientWrapper.onImport(FILE_CONTENT, remoteCallback, errorCallBack, simulationMock);
        verify(importExportCaller, times(1)).call(eq(remoteCallback), eq(errorCallBack));
        verify(importExportServiceMock, times(1)).importScesimModel(eq(ImportExportType.CSV), eq(FILE_CONTENT), eq(simulationMock));
    }

    @Test
    public void onExportToCSV() {
        RemoteCallback<String> remoteCallback = mock(RemoteCallback.class);
        ScenarioSimulationHasBusyIndicatorDefaultErrorCallback errorCallback = mock(ScenarioSimulationHasBusyIndicatorDefaultErrorCallback.class);
        scenarioSimulationEditorBusinessClientWrapper.onExportToCsv(remoteCallback, errorCallback, simulationMock);
        verify(importExportCaller, times(1)).call(eq(remoteCallback), eq(errorCallback));
        verify(importExportServiceMock, times(1)).exportScesimModel(eq(ImportExportType.CSV), eq(simulationMock));
    }

    @Test
    public void onDownloadReportToCSV() {
        RemoteCallback<String> remoteCallback = mock(RemoteCallback.class);
        ScenarioSimulationHasBusyIndicatorDefaultErrorCallback errorCallback = mock(ScenarioSimulationHasBusyIndicatorDefaultErrorCallback.class);
        scenarioSimulationEditorBusinessClientWrapper.onDownloadReportToCsv(remoteCallback, errorCallback, simulationRunMetadataMock, RULE);
        verify(runnerReportServiceCaller, times(1)).call(eq(remoteCallback), eq(errorCallback));
        verify(runnerReportServiceMock, times(1)).getReport(eq(simulationRunMetadataMock), eq(RULE));
    }

    @Test
    public void hideDocks() {
        scenarioSimulationEditorBusinessClientWrapper.hideDocks();
        verify(scenarioSimulationEditorPresenterMock, times(1)).hideDocks();
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).unRegisterTestToolsCallback();
    }

    @Test
    public void onRunScenario() {
        scenarioWithIndexLocal.add(new ScenarioWithIndex(1, new Scenario()));
        scenarioWithIndexLocal.add(new ScenarioWithIndex(2, new Scenario()));
        scenarioWithIndexLocal.add(new ScenarioWithIndex(3, new Scenario()));
        RemoteCallback<SimulationRunResult> remoteCallback = mock(RemoteCallback.class);
        ScenarioSimulationHasBusyIndicatorDefaultErrorCallback errorCallback = mock(ScenarioSimulationHasBusyIndicatorDefaultErrorCallback.class);
        scenarioSimulationEditorBusinessClientWrapper.onRunScenario(remoteCallback, errorCallback, simulationDescriptorMock, settingsLocal, scenarioWithIndexLocal, backgroundLocal);
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).unpublishTestResultsAlerts();
        verify(scenarioSimulationCaller, times(1)).call(eq(remoteCallback), eq(errorCallback));
        verify(scenarioSimulationServiceMock, times(1)).runScenario(eq(observablePathMock), eq(simulationDescriptorMock), eq(scenarioWithIndexLocal), eq(settingsLocal), eq(backgroundLocal));
    }

    @Test
    public void getDMNMetadata() {
        ArgumentCaptor<ErrorCallback> errorCallbackArgumentCaptor = ArgumentCaptor.forClass(ErrorCallback.class);
        String dmnPath = "src/test.dmn";
        String dmnName = "DMN-NAME";
        String dmnNameSpace = "DMN-namespace";
        modelLocal.getSettings().setDmnFilePath(dmnPath);
        when(dmnTypeServiceMock.getDMNMetadata(eq(pathMock), eq(dmnPath))).thenReturn(new DMNMetadata(dmnName, dmnNameSpace));
        scenarioSimulationEditorBusinessClientWrapper.getDMNMetadata();
        verify(dmnTypeServiceCaller, times(1)).call(isA(RemoteCallback.class),
                                                                          errorCallbackArgumentCaptor.capture());
        verify(dmnTypeServiceMock, times(1)).getDMNMetadata(eq(pathMock), eq(dmnPath));
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadSettingsDock();
        assertEquals(dmnName, modelLocal.getSettings().getDmnName());
        assertEquals(dmnNameSpace, modelLocal.getSettings().getDmnNamespace());
        errorCallbackArgumentCaptor.getValue().error("ERROR", new Throwable());
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(eq("ERROR"),
                                                                                                       eq(NotificationEvent.NotificationType.ERROR) );
    }

    @Test
    public void addDownloadMenuItem() {
        scenarioSimulationEditorBusinessClientWrapper.addDownloadMenuItem(fileMenuBuilderMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).addDownloadMenuItem(eq(fileMenuBuilderMock), isA(Supplier.class));
    }

    @Test
    public void onRefreshedModelContent() {
        assertNull(scenarioSimulationEditorBusinessClientWrapper.lastRunResult);
        scenarioSimulationEditorBusinessClientWrapper.onRefreshedModelContent(simulationRunResultMock);
        assertEquals(simulationRunResultMock, scenarioSimulationEditorBusinessClientWrapper.lastRunResult);
        verify(scenarioSimulationBusinessCentralDocksHandlerMock, times(1)).updateTestRunnerReportingPanelResult(eq(testResultMessageMock));
    }

    @Test
    public void registerTestToolsCallback() {
        scenarioSimulationEditorBusinessClientWrapper.registerTestToolsCallback();
        DefaultPlaceRequest request = new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER);
        verify(placeManagerMock, times(1)).registerOnOpenCallback(eq(request), eq(populateTestToolsCommand));
    }

    @Test
    public void unRegisterTestToolsCallback() {
        DefaultPlaceRequest request = new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER);
        List<Command> commands = spy(new ArrayList<>());
        commands.add(populateTestToolsCommand);
        assertTrue(commands.contains(populateTestToolsCommand));
        when(placeManagerMock.getOnOpenCallbacks(request)).thenReturn(commands);
        scenarioSimulationEditorBusinessClientWrapper.unRegisterTestToolsCallback();
        verify(placeManagerMock, times(1)).getOnOpenCallbacks(eq(request));
        assertFalse(commands.contains(populateTestToolsCommand));
    }

    @Test
    public void makeMenuBarCanUpdateProjectTrue() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContextMock).getActiveWorkspaceProject();
        doReturn(promises.resolve(true)).when(projectControllerMock).canUpdateProject(any());
        scenarioSimulationEditorBusinessClientWrapper.makeMenuBar();
        verify(scenarioSimulationEditorPresenterMock, times(1)).makeMenuBar(same(fileMenuBuilderMock));
    }

    @Test
    public void makeMenuBarCanUpdateProjectFalse() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContextMock).getActiveWorkspaceProject();
        doReturn(promises.resolve(false)).when(projectControllerMock).canUpdateProject(any());
        scenarioSimulationEditorBusinessClientWrapper.makeMenuBar();
        verify(scenarioSimulationEditorPresenterMock, times(1)).makeMenuBar(same(fileMenuBuilderMock));
    }

    @Test
    public void save() {
        String saveMessage = "Save";
        scenarioSimulationEditorBusinessClientWrapper.save(saveMessage);
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).synchronizeColumnsDimension(eq(scenarioGridPanelMock), eq(backgroundGridPanelMock));
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModel();
        verify(scenarioSimulationCaller, times(1)).call(isA(RemoteCallback.class), isA(HasBusyIndicatorDefaultErrorCallback.class));
        verify(scenarioSimulationServiceMock, times(1)).save(eq(observablePathMock), eq(scenarioSimulationModelMock), eq(metaDataMock), eq(saveMessage));
    }

    @Test
    public void synchronizeColumnsDimension() {
        scenarioSimulationEditorBusinessClientWrapper.synchronizeColumnsDimension(scenarioGridPanelMock, backgroundGridPanelMock);
        verify(scenarioGridPanelMock, times(1)).synchronizeFactMappingsWidths();
        verify(backgroundGridPanelMock, times(1)).synchronizeFactMappingsWidths();
    }

    @Test
    public void addCommonActions() {
        scenarioSimulationEditorBusinessClientWrapper.addCommonActions(fileMenuBuilderMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).addCommonActions(eq(fileMenuBuilderMock), eq(versionRecordMenuItemMock), eq(alertsButtonMenuItemMock));
    }

    @Test
    public void loadContent() {
        scenarioSimulationEditorBusinessClientWrapper.loadContent();
        verify(scenarioSimulationCaller, times(1)).call(isA(RemoteCallback.class), isA(ErrorCallback.class));
        verify(scenarioSimulationServiceMock, times(1)).loadContent(eq(observablePathMock));
    }

    @Test
    public void getLoadContentErrorCallback() {
        ErrorCallback<Boolean> errorCallback = scenarioSimulationEditorBusinessClientWrapper.getLoadContentErrorCallback();
        errorCallback.error(true, new Exception("Message"));

        verify(placeManagerMock, times(1)).forceClosePlace(eq(placeRequestMock));
        verify(scenarioSimulationEditorPresenterMock, times(1)).sendNotification(
                eq(ScenarioSimulationEditorConstants.INSTANCE.loadContentFailedNotification() + "Message"),
                eq(NotificationEvent.NotificationType.ERROR));
    }

    @Test
    public void onEditTabSelected() {
        scenarioSimulationEditorBusinessClientWrapper.onEditTabSelected();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onEditTabSelected();
    }

    @Test
    public void onOverviewSelected() {
        scenarioSimulationEditorBusinessClientWrapper.onOverviewSelected();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onOverviewSelected();
    }

    @Test
    public void onBackGroundTabSelected() {
        scenarioSimulationEditorBusinessClientWrapper.onBackgroundTabSelected();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onBackgroundTabSelected();
    }

    @Test
    public void onImportsTabSelected() {
        scenarioSimulationEditorBusinessClientWrapper.onImportsTabSelected();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onImportsTabSelected();
    }

    @Test
    public void addBackgroundPage() {
        scenarioSimulationEditorBusinessClientWrapper.addBackgroundPage(scenarioGridWidgetSpy);
        verify(multiPageEditorMock, times(1)).addPage(eq(BACKGROUND_TAB_INDEX), isA(PageImpl.class));
    }

    @Test
    public void addImportsTab() {
        scenarioSimulationEditorBusinessClientWrapper.addImportsTab(mock(IsWidget.class));
        verify(multiPageEditorMock, times(1)).addPage(isA(PageImpl.class));
    }

    @Test
    public void selectSimulationTabWithoutItem() {
        when(navTabsMock.getWidget(SIMULATION_TAB_INDEX)).thenReturn(null);
        scenarioSimulationEditorBusinessClientWrapper.selectSimulationTab();
        verify(simulationTabListItemMock, never()).showTab(eq(false));
    }

    @Test
    public void selectSimulationTabWithItem() {
        scenarioSimulationEditorBusinessClientWrapper.selectSimulationTab();
        verify(simulationTabListItemMock, times(1)).showTab(eq(false));
    }

    @Test
    public void selectBackgroundTabWithoutItem() {
        when(navTabsMock.getWidget(BACKGROUND_TAB_INDEX)).thenReturn(null);
        scenarioSimulationEditorBusinessClientWrapper.selectBackgroundTab();
        verify(backgroundTabListItemMock, never()).showTab(eq(false));
    }

    @Test
    public void selectBackgroundTabWithItem() {
        scenarioSimulationEditorBusinessClientWrapper.selectBackgroundTab();
        verify(backgroundTabListItemMock, times(1)).showTab(eq(false));
    }

    @Test
    public void getScenarioSimulationDocksHandler() {
        assertEquals(scenarioSimulationBusinessCentralDocksHandlerMock,
                     scenarioSimulationEditorBusinessClientWrapper.getScenarioSimulationDocksHandler());
    }

    @Test
    public void getScenarioSimulationEditorPresenter() {
        assertEquals(scenarioSimulationEditorPresenterMock,
                     scenarioSimulationEditorBusinessClientWrapper.getScenarioSimulationEditorPresenter());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populateRightDocks_Unknown() {
        scenarioSimulationEditorBusinessClientWrapper.populateDocks("Unknown");
        verify(scenarioSimulationEditorPresenterMock, never()).setTestTools(any());
        verify(coverageReportPresenterMock, never()).setCurrentPath(any());
        verify(scenarioSimulationEditorPresenterMock, never()).setSettings(any());
        verify(settingsPresenterMock, never()).setCurrentPath(any());
        verify(cheatSheetPresenterMock, never()).setCurrentPath(any());
        verify(scenarioSimulationEditorPresenterMock, never()).setCheatSheet(any());
    }

    @Test
    public void populateRightDocks_Settings() {
        doReturn(settingsPresenterMock).when(scenarioSimulationBusinessCentralDocksHandlerMock).getSettingsPresenter();
        scenarioSimulationEditorBusinessClientWrapper.populateDocks(SettingsPresenter.IDENTIFIER);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setSettings(eq(settingsPresenterMock));
        verify(settingsPresenterMock, times(1)).setCurrentPath(eq(pathMock));
        verify(scenarioSimulationEditorPresenterMock, never()).setTestTools(any());
        verify(coverageReportPresenterMock, never()).setCurrentPath(any());
        verify(cheatSheetPresenterMock, never()).setCurrentPath(any());
        verify(scenarioSimulationEditorPresenterMock, never()).setCheatSheet(any());
    }

    @Test
    public void populateRightDocks_TestTools() {
        doReturn(testToolsPresenterMock).when(scenarioSimulationBusinessCentralDocksHandlerMock).getTestToolsPresenter();
        scenarioSimulationEditorBusinessClientWrapper.populateDocks(TestToolsPresenter.IDENTIFIER);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setTestTools(eq(testToolsPresenterMock));
        verify(coverageReportPresenterMock, never()).setCurrentPath(any());
        verify(scenarioSimulationEditorPresenterMock, never()).setSettings(any());
        verify(settingsPresenterMock, never()).setCurrentPath(any());
        verify(cheatSheetPresenterMock, never()).setCurrentPath(any());
        verify(scenarioSimulationEditorPresenterMock, never()).setCheatSheet(any());
    }

    @Test
    public void populateRightDocks_CheatSheetPresenter_NotShown() {
        when(cheatSheetPresenterMock.isCurrentlyShow(pathMock)).thenReturn(false);
        doReturn(cheatSheetPresenterMock).when(scenarioSimulationBusinessCentralDocksHandlerMock).getCheatSheetPresenter();
        scenarioSimulationEditorBusinessClientWrapper.populateDocks(CheatSheetPresenter.IDENTIFIER);
        verify(cheatSheetPresenterMock, times(1)).setCurrentPath(pathMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setCheatSheet(eq(cheatSheetPresenterMock));
        verify(scenarioSimulationEditorBusinessClientWrapper, never()).setCoverageReport(any());
        verify(coverageReportPresenterMock, never()).setCurrentPath(any());
        verify(settingsPresenterMock, never()).setCurrentPath(any());
        verify(scenarioSimulationEditorPresenterMock, never()).setTestTools(any());
    }

    @Test
    public void populateRightDocks_CheatSheetPresenter_IsShown() {
        when(cheatSheetPresenterMock.isCurrentlyShow(pathMock)).thenReturn(true);
        doReturn(cheatSheetPresenterMock).when(scenarioSimulationBusinessCentralDocksHandlerMock).getCheatSheetPresenter();
        scenarioSimulationEditorBusinessClientWrapper.populateDocks(CheatSheetPresenter.IDENTIFIER);
        verify(scenarioSimulationEditorPresenterMock, never()).setSettings(any());
        verify(settingsPresenterMock, never()).setCurrentPath(any());
        verify(scenarioSimulationEditorPresenterMock, never()).setTestTools(any());
        verify(cheatSheetPresenterMock, never()).setCurrentPath(any());
        verify(scenarioSimulationEditorPresenterMock, never()).setCheatSheet(any());
        verify(scenarioSimulationEditorBusinessClientWrapper, never()).setCoverageReport(any());
        verify(coverageReportPresenterMock, never()).setCurrentPath(any());
    }

    @Test
    public void populateRightDocks_CoverageReportPresenter() {
        scenarioSimulationEditorBusinessClientWrapper.populateDocks(CoverageReportPresenter.IDENTIFIER);
        verify(scenarioSimulationEditorPresenterMock, never()).setSettings(any());
        verify(settingsPresenterMock, never()).setCurrentPath(any());
        verify(scenarioSimulationEditorPresenterMock, never()).setTestTools(any());
        verify(cheatSheetPresenterMock, never()).setCurrentPath(any());
        verify(scenarioSimulationEditorPresenterMock, never()).setCheatSheet(any());
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).setCoverageReport(eq(coverageReportPresenterMock));
        verify(coverageReportPresenterMock, times(1)).setCurrentPath(eq(pathMock));
    }

    @Test
    public void getModelSuccessCallBackMethod_Rule() {
        modelLocal.setSimulation(getSimulation());
        modelLocal.getSettings().setType(RULE);
        modelLocal.getSettings().setDmoSession(null);
        scenarioSimulationEditorBusinessClientWrapper.getModelSuccessCallbackMethod(content);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPackageName(eq(TestProperties.FACT_PACKAGE));
        /* EventBus is used ONLY with DMN */
        verify(scenarioSimulationEditorPresenterMock, never()).getEventBus();
        /* Not possible to mock a new Instance, for this reason any()s are used */
        verify(importsWidgetPresenterMock, times(1)).setContent(any(), any(), anyBoolean());
        verify(scenarioSimulationViewMock, times(1)).hideBusyIndicator();
        InOrder inOrder = inOrder(scenarioSimulationEditorBusinessClientWrapper, scenarioSimulationEditorPresenterMock);
        inOrder.verify(scenarioSimulationEditorPresenterMock, times(1)).getModelSuccessCallbackMethod(isA(DataManagementStrategy.class), eq(modelLocal));
        inOrder.verify(scenarioSimulationEditorPresenterMock, times(1)).getJsonModel(eq(modelLocal));
        inOrder.verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).setOriginalHash(anyInt());
    }

    @Test
    public void getModelSuccessCallBackMethod_DMN() {
        modelLocal.setSimulation(getSimulation());
        modelLocal.getSettings().setType(DMN);
        modelLocal.getSettings().setDmnFilePath(null);
        scenarioSimulationEditorBusinessClientWrapper.getModelSuccessCallbackMethod(content);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPackageName(eq(TestProperties.FACT_PACKAGE));
        /* EventBus is used ONLY with DMN */
        verify(scenarioSimulationEditorPresenterMock, times(1)).getEventBus();
        verify(versionRecordManagerMock, times(2)).getCurrentPath();
        verify(importsWidgetPresenterMock, never()).setContent(any(), any(), anyBoolean());
        verify(scenarioSimulationViewMock, times(1)).hideBusyIndicator();
        verify(scenarioSimulationEditorPresenterMock, times(1)).getJsonModel(eq(modelLocal));
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModelSuccessCallbackMethod(isA(DataManagementStrategy.class), eq(modelLocal));
        InOrder inOrder = inOrder(scenarioSimulationEditorBusinessClientWrapper, scenarioSimulationEditorPresenterMock);
        inOrder.verify(scenarioSimulationEditorPresenterMock, times(1)).getModelSuccessCallbackMethod(isA(DataManagementStrategy.class), eq(modelLocal));
        inOrder.verify(scenarioSimulationEditorPresenterMock, times(1)).getJsonModel(eq(modelLocal));
        inOrder.verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).setOriginalHash(anyInt());
    }

    @Test
    public void setCoverageReport() {
        AuditLog auditLogMock = mock(AuditLog.class);
        CoverageReportView.Presenter presenterSpy = spy(CoverageReportView.Presenter.class);
        scenarioSimulationEditorBusinessClientWrapper.setCoverageReport(presenterSpy);
        verify(presenterSpy, times(1)).populateCoverageReport(eq(DMN), eq(null));
        //
        reset(presenterSpy, simulationRunMetadataMock, scenarioSimulationEditorBusinessClientWrapper);
        scenarioSimulationEditorBusinessClientWrapper.lastRunResult = simulationRunResultMock;
        scenarioSimulationEditorBusinessClientWrapper.setCoverageReport(presenterSpy);
        verify(presenterSpy, times(1)).populateCoverageReport(eq(DMN), eq(simulationRunMetadataMock));
        verify(presenterSpy, never()).setDownloadReportCommand(any());
        //
        reset(presenterSpy, simulationRunMetadataMock, scenarioSimulationEditorBusinessClientWrapper);
        when(simulationRunMetadataMock.getAuditLog()).thenReturn(auditLogMock);
        scenarioSimulationEditorBusinessClientWrapper.lastRunResult = simulationRunResultMock;
        scenarioSimulationEditorBusinessClientWrapper.setCoverageReport(presenterSpy);
        verify(presenterSpy, times(1)).populateCoverageReport(eq(DMN), eq(simulationRunMetadataMock));
        verify(presenterSpy, times(1)).setDownloadReportCommand(commandArgumentCaptor.capture());
        commandArgumentCaptor.getValue().execute();
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).onDownloadReportToCsv(eq(exportCallBackMock),
                                                                                              isA(ScenarioSimulationHasBusyIndicatorDefaultErrorCallback.class),
                                                                                              eq(simulationRunMetadataMock),
                                                                                              eq(DMN));
        //
        reset(presenterSpy, simulationRunMetadataMock, scenarioSimulationEditorBusinessClientWrapper);
        scenarioSimulationEditorBusinessClientWrapper.lastRunResult = null;
        when(scenarioSimulationEditorPresenterMock.getDataManagementStrategy()).thenReturn(mock(AbstractDMODataManagementStrategy.class));
        scenarioSimulationEditorBusinessClientWrapper.setCoverageReport(presenterSpy);
        verify(presenterSpy, times(1)).populateCoverageReport(eq(RULE), eq(null));
        //
        reset(presenterSpy, simulationRunMetadataMock, scenarioSimulationEditorBusinessClientWrapper);
        scenarioSimulationEditorBusinessClientWrapper.lastRunResult = simulationRunResultMock;
        scenarioSimulationEditorBusinessClientWrapper.setCoverageReport(presenterSpy);
        verify(presenterSpy, times(1)).populateCoverageReport(eq(RULE), eq(simulationRunMetadataMock));
        verify(presenterSpy, never()).setDownloadReportCommand(any());
        //
        reset(presenterSpy, simulationRunMetadataMock, scenarioSimulationEditorBusinessClientWrapper);
        when(simulationRunMetadataMock.getAuditLog()).thenReturn(auditLogMock);
        scenarioSimulationEditorBusinessClientWrapper.lastRunResult = simulationRunResultMock;
        scenarioSimulationEditorBusinessClientWrapper.setCoverageReport(presenterSpy);
        verify(presenterSpy, times(1)).populateCoverageReport(eq(RULE), eq(simulationRunMetadataMock));
        verify(presenterSpy, times(1)).setDownloadReportCommand(commandArgumentCaptor.capture());
        commandArgumentCaptor.getValue().execute();
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).onDownloadReportToCsv(eq(exportCallBackMock),
                                                                                              isA(ScenarioSimulationHasBusyIndicatorDefaultErrorCallback.class),
                                                                                              eq(simulationRunMetadataMock),
                                                                                              eq(RULE));
    }

    @Test
    public void unpublishTestResultsAlerts() {
        scenarioSimulationEditorBusinessClientWrapper.unpublishTestResultsAlerts();
        verify(unpublishMessagesEventMock, times(1)).fire(unpublishMessagesEventArgumentCaptor.capture());
        assertFalse(unpublishMessagesEventArgumentCaptor.getValue().isShowSystemConsole());
        assertEquals("TestResults", unpublishMessagesEventArgumentCaptor.getValue().getMessageType());
        assertEquals(SESSION_ID, unpublishMessagesEventArgumentCaptor.getValue().getSessionId());
    }
}
