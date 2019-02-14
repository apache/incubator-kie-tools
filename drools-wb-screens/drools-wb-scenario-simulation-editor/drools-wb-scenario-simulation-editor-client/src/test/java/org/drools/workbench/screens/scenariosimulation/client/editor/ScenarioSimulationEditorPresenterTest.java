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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationDocksHandler;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.producers.ScenarioSimulationProducer;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.kie.workbench.common.workbench.client.test.TestRunnerReportingScreen;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyInt;
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
    private AbstractWorkbenchActivity rightPanelActivityMock;
    @Mock
    private RightPanelView rightPanelViewMock;
    @Mock
    private ObservablePath pathMock;
    @Mock
    private PathPlaceRequest placeRequestMock;
    @Mock
    private ScenarioSimulationContext contextMock;
    @Mock
    private ScenarioSimulationContext.Status statusMock;
    @Mock
    private TestRunnerReportingScreen testRunnerReportingScreenMock;
    @Mock
    private ScenarioSimulationDocksHandler scenarioSimulationDocksHandlerMock;
    @Mock
    private ScenarioMenuItem runScenarioMenuItemMock;
    @Mock
    private ScenarioMenuItem undoMenuItemMock;
    @Mock
    private ScenarioMenuItem redoMenuItemMock;
    @Mock
    private DataManagementStrategy dataManagementStrategyMock;

    @Before
    public void setup() {
        super.setup();
        when(scenarioGridLayerMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioSimulationViewMock.getScenarioGridPanel()).thenReturn(scenarioGridPanelMock);
        when(scenarioSimulationViewMock.getScenarioGridLayer()).thenReturn(scenarioGridLayerMock);
        when(scenarioSimulationViewMock.getRunScenarioMenuItem()).thenReturn(runScenarioMenuItemMock);
        when(scenarioSimulationViewMock.getUndoMenuItem()).thenReturn(undoMenuItemMock);
        when(scenarioSimulationViewMock.getRedoMenuItem()).thenReturn(redoMenuItemMock);
        when(scenarioGridPanelMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioSimulationProducerMock.getScenarioSimulationView()).thenReturn(scenarioSimulationViewMock);
        when(scenarioSimulationProducerMock.getScenarioSimulationContext()).thenReturn(contextMock);
        when(placeRequestMock.getIdentifier()).thenReturn(ScenarioSimulationEditorPresenter.IDENTIFIER);
        when(rightPanelViewMock.getPresenter()).thenReturn(rightPanelPresenterMock);
        when(rightPanelActivityMock.getWidget()).thenReturn(rightPanelViewMock);
        when(placeRequestMock.getPath()).thenReturn(pathMock);
        when(contextMock.getStatus()).thenReturn(statusMock);

        this.presenter = new ScenarioSimulationEditorPresenter(new CallerMock<>(scenarioSimulationServiceMock),
                                                               scenarioSimulationProducerMock,
                                                               mock(ScenarioSimulationResourceType.class),
                                                               importsWidgetPresenterMock,
                                                               oracleFactoryMock,
                                                               placeManagerMock,
                                                               testRunnerReportingScreenMock,
                                                               scenarioSimulationDocksHandlerMock,
                                                               new CallerMock<>(dmnTypeServiceMock)) {
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
            protected void populateRightPanel() {
            }

            @Override
            protected void clearRightPanelStatus() {

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
    public void onPlaceGainFocusEvent() {
        PlaceGainFocusEvent mockPlaceGainFocusEvent = mock(PlaceGainFocusEvent.class);
        when(mockPlaceGainFocusEvent.getPlace()).thenReturn(placeRequestMock);
        when(placeManagerMock.getStatus(placeRequestMock)).thenReturn(PlaceStatus.CLOSE);
        presenter.onPlaceGainFocusEvent(mockPlaceGainFocusEvent);
        verify(scenarioSimulationDocksHandlerMock).addDocks();
        verify(scenarioSimulationDocksHandlerMock).expandToolsDock();
    }

    @Test
    public void onPlaceHiddenEvent() {
        PlaceHiddenEvent mockPlaceHiddenEvent = mock(PlaceHiddenEvent.class);
        when(mockPlaceHiddenEvent.getPlace()).thenReturn(placeRequestMock);
        when(placeManagerMock.getStatus(placeRequestMock)).thenReturn(PlaceStatus.OPEN);
        presenter.onPlaceHiddenEvent(mockPlaceHiddenEvent);
        verify(scenarioSimulationDocksHandlerMock).removeDocks();
        verify(testRunnerReportingScreenMock).reset();
        verify(scenarioGridMock, times(1)).clearSelections();
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
        when(scenarioSimulationServiceMock.runScenario(any(), any())).thenReturn(scenarioSimulationModelMock);
        when(statusMock.getSimulation()).thenReturn(simulationMock);
        when(contextMock.getStatus()).thenReturn(statusMock);
        assertFalse(modelLocal.getSimulation().equals(simulationMock));
        presenter.onStartup(observablePathMock, placeRequestMock);
        presenter.onRunScenario();
        verify(scenarioSimulationServiceMock).runScenario(any(), eq(modelLocal));
        verify(scenarioGridModelMock, times(1)).resetErrors();
        verify(scenarioSimulationViewMock, times(1)).refreshContent(any());
        verify(scenarioSimulationDocksHandlerMock).expandTestResultsDock();
        assertTrue(modelLocal.getSimulation().equals(simulationMock));
    }

    @Test
    public void refreshModelContent() {
        when(scenarioSimulationModelMock.getSimulation()).thenReturn(simulationMock);
        presenter.refreshModelContent(scenarioSimulationModelMock);
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
    public void setRightPanel() {
        presenter.setRightPanel(rightPanelPresenterMock);
        verify(contextMock, times(1)).setRightPanelPresenter(rightPanelPresenterMock);
        verify(rightPanelPresenterMock, times(1)).setEventBus(eventBusMock);
        verify(dataManagementStrategyMock, times(1)).populateRightPanel(rightPanelPresenterMock, scenarioGridModelMock);
        verify(rightPanelPresenterMock, times(1)).initCheatSheet(any());
    }

    @Test
    public void getModelSuccessCallbackMethod() {
        presenterSpy.getModelSuccessCallbackMethod(content);
        verify(presenterSpy, times(1)).populateRightPanel();
        verify(scenarioSimulationViewMock, times(1)).hideBusyIndicator();
        verify(scenarioSimulationViewMock, times(1)).setContent(eq(content.getModel().getSimulation()));
        verify(statusMock, times(1)).setSimulation(eq(content.getModel().getSimulation()));
        verify(presenterSpy, times(1)).setOriginalHash(anyInt());
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
}
