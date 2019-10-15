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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.TestProperties;
import org.drools.workbench.screens.scenariosimulation.client.editor.AbstractScenarioSimulationEditorTest;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationHasBusyIndicatorDefaultErrorCallback;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportService;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportType;
import org.drools.workbench.screens.scenariosimulation.service.RunnerReportService;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.Mock;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationEditorBusinessCentralWrapperTest extends AbstractScenarioSimulationEditorTest {

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
    private AuditLog auditLog;
    @Mock
    private AssetUpdateValidator assetUpdateValidatorMock;
    @Mock
    private Supplier<ScenarioSimulationModel> contentSupplierMock;
    @Mock
    private ProjectController projectControllerMock;


    private CallerMock<ScenarioSimulationService> scenarioSimulationCaller;
    private CallerMock<ImportExportService> importExportCaller;
    private CallerMock<RunnerReportService> runnerReportServiceCaller;
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
        saveAndRenameCommandBuilderMock = spy(new SaveAndRenameCommandBuilder<>(null, null, null, null));
        scenarioSimulationEditorBusinessClientWrapper = spy(new ScenarioSimulationEditorBusinessCentralWrapper(scenarioSimulationCaller,
                                                                                                               scenarioSimulationEditorPresenterMock,
                                                                                                               importsWidgetPresenterMock,
                                                                                                               oracleFactoryMock,
                                                                                                               placeManagerMock,
                                                                                                               new CallerMock<>(dmnTypeServiceMock),
                                                                                                               importExportCaller,
                                                                                                               runnerReportServiceCaller) {
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
            }
        });
        when(placeRequestMock.getPath()).thenReturn(observablePathMock);
        when(scenarioSimulationEditorPresenterMock.getType()).thenReturn(scenarioSimulationResourceType);
        when(scenarioSimulationEditorPresenterMock.getPopulateTestToolsCommand()).thenReturn(populateTestToolsCommand);
        when(scenarioSimulationEditorPresenterMock.getJsonModel(any())).thenReturn("");
        when(scenarioSimulationEditorPresenterMock.getView()).thenReturn(scenarioSimulationViewMock);
        when(scenarioSimulationEditorPresenterMock.getModel()).thenReturn(scenarioSimulationModelMock);
        when(scenarioSimulationEditorPresenterMock.getContentSupplier()).thenReturn(contentSupplierMock);
        when(scenarioSimulationViewMock.getScenarioGridLayer()).thenReturn(scenarioGridLayerMock);
        when(alertsButtonMenuItemBuilderMock.build()).thenReturn(alertsButtonMenuItemMock);
        when(versionRecordManagerMock.buildMenu()).thenReturn(versionRecordMenuItemMock);
    }

    @Test
    public void onStartup() {
        scenarioSimulationEditorBusinessClientWrapper.onStartup(observablePathMock, placeRequestMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).init(eq(scenarioSimulationEditorBusinessClientWrapper), eq(observablePathMock));
    }

    @Test
    public void onClose() {
        scenarioSimulationEditorBusinessClientWrapper.onClose();
        verify(versionRecordManagerMock, times(1)).clear();
        verify(scenarioSimulationEditorPresenterMock, times(1)).onClose();
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
        verify(scenarioSimulationEditorPresenterMock, times(1)).showDocks(same(placeStatusMock));
    }

    @Test
    public void onImport() {
        String FILE_CONTENT = "FILE_CONTENT";
        RemoteCallback<Simulation> remoteCallback = mock(RemoteCallback.class);
        ErrorCallback<Object> errorCallBack = mock(ErrorCallback.class);
        scenarioSimulationEditorBusinessClientWrapper.onImport(FILE_CONTENT, remoteCallback, errorCallBack, simulationMock);
        verify(importExportCaller, times(1)).call(eq(remoteCallback), eq(errorCallBack));
        verify(importExportServiceMock, times(1)).importSimulation(eq(ImportExportType.CSV), eq(FILE_CONTENT), eq(simulationMock));
    }

    @Test
    public void onExportToCSV() {
        RemoteCallback<Object> remoteCallback = mock(RemoteCallback.class);
        ScenarioSimulationHasBusyIndicatorDefaultErrorCallback errorCallback = mock(ScenarioSimulationHasBusyIndicatorDefaultErrorCallback.class);
        scenarioSimulationEditorBusinessClientWrapper.onExportToCsv(remoteCallback, errorCallback, simulationMock);
        verify(importExportCaller, times(1)).call(eq(remoteCallback), eq(errorCallback));
        verify(importExportServiceMock, times(1)).exportSimulation(eq(ImportExportType.CSV), eq(simulationMock));
    }

    @Test
    public void onDownloadReportToCSV() {
        RemoteCallback<Object> remoteCallback = mock(RemoteCallback.class);
        ScenarioSimulationHasBusyIndicatorDefaultErrorCallback errorCallback = mock(ScenarioSimulationHasBusyIndicatorDefaultErrorCallback.class);
        scenarioSimulationEditorBusinessClientWrapper.onDownloadReportToCsv(remoteCallback, errorCallback, auditLog);
        verify(runnerReportServiceCaller, times(1)).call(eq(remoteCallback), eq(errorCallback));
        verify(runnerReportServiceMock, times(1)).getReport(eq(auditLog));
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
        scenarioSimulationEditorBusinessClientWrapper.onRunScenario(remoteCallback, errorCallback, simulationDescriptorMock, scenarioWithIndexLocal);
        verify(scenarioSimulationCaller, times(1)).call(eq(remoteCallback), eq(errorCallback));
        verify(scenarioSimulationServiceMock, times(1)).runScenario(eq(observablePathMock), eq(simulationDescriptorMock), eq(scenarioWithIndexLocal));
    }

    @Test
    public void addDownloadMenuItem() {
        scenarioSimulationEditorBusinessClientWrapper.addDownloadMenuItem(fileMenuBuilderMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).addDownloadMenuItem(eq(fileMenuBuilderMock), isA(Supplier.class));
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
    public void setSaveEnabledTrue() {
        scenarioSimulationEditorBusinessClientWrapper.setSaveEnabled(true);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setSaveEnabled(eq(true));
    }

    @Test
    public void setSaveEnabledFalse() {
        scenarioSimulationEditorBusinessClientWrapper.setSaveEnabled(false);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setSaveEnabled(eq(false));
    }


    @Test
    public void makeMenuBarCanUpdateProjectTrue() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContextMock).getActiveWorkspaceProject();
        doReturn(promises.resolve(true)).when(projectControllerMock).canUpdateProject(any());
        scenarioSimulationEditorBusinessClientWrapper.makeMenuBar();
        verify(scenarioSimulationEditorPresenterMock, times(1)).makeMenuBar(same(fileMenuBuilderMock));
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).setSaveEnabled(eq(true));
    }

    @Test
    public void makeMenuBarCanUpdateProjectFalse() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContextMock).getActiveWorkspaceProject();
        doReturn(promises.resolve(false)).when(projectControllerMock).canUpdateProject(any());
        scenarioSimulationEditorBusinessClientWrapper.makeMenuBar();
        verify(scenarioSimulationEditorPresenterMock, times(1)).makeMenuBar(same(fileMenuBuilderMock));
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).setSaveEnabled(eq(false));
    }

    @Test
    public void getContentSupplier() {
        scenarioSimulationEditorBusinessClientWrapper.getContentSupplier();
        verify(scenarioSimulationEditorPresenterMock, times(1)).getContentSupplier();
    }

    @Test
    public void save() {
        String saveMessage = "Save";
        scenarioSimulationEditorBusinessClientWrapper.save(saveMessage);
        verify(scenarioSimulationEditorBusinessClientWrapper, times(1)).synchronizeColumnsDimension();
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModel();
        verify(scenarioSimulationCaller, times(1)).call(isA(RemoteCallback.class), isA(HasBusyIndicatorDefaultErrorCallback.class));
        verify(scenarioSimulationServiceMock, times(1)).save(eq(observablePathMock), eq(scenarioSimulationModelMock), eq(metaDataMock), eq(saveMessage));
    }

    @Test
    public void synchronizeColumnsDimension() {
        scenarioSimulationEditorBusinessClientWrapper.synchronizeColumnsDimension();
        verify(scenarioGridModelMock, times(1)).synchronizeFactMappingsWidths();
    }

    @Test
    public void addCommonActions() {
        scenarioSimulationEditorBusinessClientWrapper.addCommonActions(fileMenuBuilderMock);
        verify(scenarioSimulationEditorPresenterMock, times(1)).addCommonActions(eq(fileMenuBuilderMock), eq(versionRecordMenuItemMock), eq(alertsButtonMenuItemMock));
    }

    @Test
    public void loadContent() {
        scenarioSimulationEditorBusinessClientWrapper.loadContent();
        verify(scenarioSimulationCaller, times(1)).call(isA(RemoteCallback.class), isA(CommandDrivenErrorCallback.class));
        verify(scenarioSimulationServiceMock, times(1)).loadContent(eq(observablePathMock));
    }

    @Test
    public void getModelSuccessCallBackMethod_Rule() {
        modelLocal.setSimulation(getSimulation(ScenarioSimulationModel.Type.RULE, null));
        scenarioSimulationEditorBusinessClientWrapper.getModelSuccessCallbackMethod(content);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPackageName(eq(TestProperties.FACT_PACKAGE));
        /* EventBus is used ONLY with DMN */
        verify(scenarioSimulationEditorPresenterMock, never()).getEventBus();
        /* Not possible to mock a new Instance, for this reason any()s are used */
        verify(importsWidgetPresenterMock, times(1)).setContent(any(), any(), anyBoolean());
        verify(scenarioSimulationViewMock, times(1)).hideBusyIndicator();
        verify(scenarioSimulationEditorPresenterMock, times(1)).getJsonModel(eq(modelLocal));
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModelSuccessCallbackMethod(isA(DataManagementStrategy.class), eq(modelLocal));
    }

    @Test
    public void getModelSuccessCallBackMethod_DMN() {
        modelLocal.setSimulation(getSimulation(ScenarioSimulationModel.Type.DMN, null));
        scenarioSimulationEditorBusinessClientWrapper.getModelSuccessCallbackMethod(content);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setPackageName(eq(TestProperties.FACT_PACKAGE));
        /* EventBus is used ONLY with DMN */
        verify(scenarioSimulationEditorPresenterMock, times(1)).getEventBus();
        verify(versionRecordManagerMock, times(2)).getCurrentPath();
        verify(importsWidgetPresenterMock, never()).setContent(any(), any(), anyBoolean());
        verify(scenarioSimulationViewMock, times(1)).hideBusyIndicator();
        verify(scenarioSimulationEditorPresenterMock, times(1)).getJsonModel(eq(modelLocal));
        verify(scenarioSimulationEditorPresenterMock, times(1)).getModelSuccessCallbackMethod(isA(DataManagementStrategy.class), eq(modelLocal));
    }

}
