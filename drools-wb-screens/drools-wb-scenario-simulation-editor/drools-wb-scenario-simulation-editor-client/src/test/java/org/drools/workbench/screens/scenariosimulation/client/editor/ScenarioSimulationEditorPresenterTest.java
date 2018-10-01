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
import org.drools.workbench.screens.scenariosimulation.client.commands.CommandExecutor;
import org.drools.workbench.screens.scenariosimulation.client.producers.ScenarioSimulationProducer;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.client.widgets.RightPanelMenuItem;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
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
    private KieEditorWrapperView mockKieView;

    @Mock
    private OverviewWidgetPresenter mockOverviewWidget;

    @Mock
    private DefaultFileNameValidator mockFileNameValidator;

    @Mock
    private AlertsButtonMenuItemBuilder mockAlertsButtonMenuItemBuilder;

    @Mock
    private EventSourceMock<NotificationEvent> mockNotification;

    @Mock
    private ScenarioGrid mockScenarioGrid;

    @Mock
    private ScenarioGridLayer mockScenarioGridLayer;

    @Mock
    private ScenarioGridPanel mockScenarioGridPanel;

    @Mock
    private ScenarioSimulationView mockScenarioSimulationView;

    @Mock
    private ScenarioSimulationProducer mockScenarioSimulationProducer;

    @Mock
    private ImportsWidgetPresenter mockImportsWidget;

    @Mock
    private AsyncPackageDataModelOracleFactory mockOracleFactory;

    @Mock
    private AsyncPackageDataModelOracle mockOracle;

    @Mock
    private PlaceManager mockPlaceManager;

    @Mock
    private PathPlaceRequest mockPlaceRequest;

    @Mock
    private AbstractWorkbenchActivity mockRightPanelActivity;

    @Mock
    private RightPanelView mockRightPanelView;

    @Mock
    private RightPanelPresenter mockRightPanelPresenter;

    @Mock
    private RightPanelMenuItem mockRightPanelMenuItem;

    @Mock
    private ObservablePath mockPath;

    @Mock
    private Command mockSetButtonTextFalse;

    @Mock
    private Command mockSetButtonTextTrue;

    @Mock
    private CommandExecutor mockCommandExecutor;

    @Before
    public void setup() {
        super.setup();
        when(mockScenarioGridLayer.getScenarioGrid()).thenReturn(mockScenarioGrid);
        when(mockScenarioSimulationView.getScenarioGridPanel()).thenReturn(mockScenarioGridPanel);
        when(mockScenarioSimulationView.getScenarioGridLayer()).thenReturn(mockScenarioGridLayer);
        when(mockRightPanelMenuItem.getSetButtonTextFalse()).thenReturn(mockSetButtonTextFalse);
        when(mockRightPanelMenuItem.getSetButtonTextTrue()).thenReturn(mockSetButtonTextTrue);
        when(mockScenarioSimulationProducer.getScenarioSimulationView()).thenReturn(mockScenarioSimulationView);
        when(mockScenarioSimulationProducer.getCommandExecutor()).thenReturn(mockCommandExecutor);
        when(mockPlaceRequest.getIdentifier()).thenReturn(ScenarioSimulationEditorPresenter.IDENTIFIER);

        when(mockOracleFactory.makeAsyncPackageDataModelOracle(anyObject(), anyObject(), anyObject())).thenReturn(mockOracle);

        when(mockRightPanelView.getPresenter()).thenReturn(mockRightPanelPresenter);
        when(mockRightPanelActivity.getWidget()).thenReturn(mockRightPanelView);

        when(mockPlaceRequest.getPath()).thenReturn(mockPath);

        this.presenter = new ScenarioSimulationEditorPresenter(new CallerMock<>(scenarioSimulationService),
                                                               mockScenarioSimulationProducer,
                                                               mock(ScenarioSimulationResourceType.class),
                                                               mockImportsWidget,
                                                               mockOracleFactory,
                                                               mockPlaceManager) {
            {
                this.kieView = mockKieView;
                this.overviewWidget = mockOverviewWidget;
                this.fileMenuBuilder = mockFileMenuBuilder;
                this.fileNameValidator = mockFileNameValidator;
                this.versionRecordManager = mockVersionRecordManager;
                this.notification = mockNotification;
                this.workbenchContext = mockWorkbenchContext;
                this.alertsButtonMenuItemBuilder = mockAlertsButtonMenuItemBuilder;
                this.rightPanelRequest = mockPlaceRequest;
                this.path = mockPath;
                this.rightPanelMenuItem = mockRightPanelMenuItem;
                this.scenarioGridPanel = mockScenarioGridPanel;
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
            void populateRightPanel() {
            }

            @Override
            void clearRightPanelStatus() {

            }

            @Override
            String getJsonModel(ScenarioSimulationModel model) {
                return "";
            }
        };
        presenterSpy = spy(presenter);
    }

    @Test
    public void testPresenterInit() throws Exception {
        verify(mockScenarioSimulationView).init(presenter);
    }

    @Test
    public void testOnStartup() {

        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);
        when(mockOracleFactory.makeAsyncPackageDataModelOracle(any(),
                                                               eq(model),
                                                               eq(content.getDataModel()))).thenReturn(oracle);
        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));
        verify(mockImportsWidget).setContent(oracle,
                                             model.getImports(),
                                             false);
        verify(mockKieView).addImportsTab(mockImportsWidget);
        verify(mockScenarioSimulationView).showLoading();
        verify(mockScenarioSimulationView).hideBusyIndicator();
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
        doReturn(menuItem).when(mockScenarioSimulationView).getRunScenarioMenuItem();

        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));

        verify(mockFileMenuBuilder).addNewTopLevelMenu(menuItem);
    }

    @Test
    public void save() {
        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));
        reset(mockScenarioSimulationView);

        presenter.save("save message");

        verify(mockScenarioSimulationView).hideBusyIndicator();
        verify(mockNotification).fire(any(NotificationEvent.class));
        verify(mockVersionRecordManager).reloadVersions(any(Path.class));
    }

    @Test
    public void onPlaceGainFocusEvent() {
        PlaceGainFocusEvent mockPlaceGainFocusEvent = mock(PlaceGainFocusEvent.class);
        when(mockPlaceGainFocusEvent.getPlace()).thenReturn(mockPlaceRequest);
        when(mockPlaceManager.getStatus(mockPlaceRequest)).thenReturn(PlaceStatus.CLOSE);
        presenter.onPlaceGainFocusEvent(mockPlaceGainFocusEvent);
        verify(mockPlaceManager, times(1)).goTo(mockPlaceRequest);
    }

    @Test
    public void onPlaceHiddenEvent() {
        PlaceHiddenEvent mockPlaceHiddenEvent = mock(PlaceHiddenEvent.class);
        when(mockPlaceHiddenEvent.getPlace()).thenReturn(mockPlaceRequest);
        when(mockPlaceManager.getStatus(mockPlaceRequest)).thenReturn(PlaceStatus.OPEN);
        presenter.onPlaceHiddenEvent(mockPlaceHiddenEvent);
        verify(mockPlaceManager, times(1)).closePlace(mockPlaceRequest);
        verify(mockScenarioGrid, times(1)).clearSelections();
    }

    @Test
    public void onClose() {
        when(mockPlaceManager.getStatus(mockPlaceRequest)).thenReturn(PlaceStatus.OPEN);
        presenter.onClose();
        onClosePlaceStatusOpen();
        reset(mockScenarioGridPanel);
        reset(mockVersionRecordManager);
        reset(mockPlaceManager);
        reset(mockScenarioSimulationView);
        when(mockPlaceManager.getStatus(mockPlaceRequest)).thenReturn(PlaceStatus.CLOSE);
        presenter.onClose();
        onClosePlaceStatusClose();
    }

    @Test
    public void onRunTest() throws Exception {

        final ScenarioSimulationModel model = new ScenarioSimulationModel();
        doReturn(new ScenarioSimulationModelContent(model,
                                                    new Overview(),
                                                    new PackageDataModelOracleBaselinePayload())).when(scenarioSimulationService).loadContent(any());
        presenter.onStartup(mock(ObservablePath.class), mock(PlaceRequest.class));

        presenter.onRunScenario();

        verify(scenarioSimulationService).runScenario(any(), eq(model));
    }

    private void onClosePlaceStatusOpen() {
        verify(mockVersionRecordManager, times(1)).clear();
        verify(mockPlaceManager, times(1)).closePlace(mockPlaceRequest);
        verify(presenter.getView()).showLoading();
        verify(mockScenarioGridPanel, times(1)).unregister();
    }

    private void onClosePlaceStatusClose() {
        verify(mockVersionRecordManager, times(1)).clear();
        verify(mockPlaceManager, times(0)).closePlace(mockPlaceRequest);
        verify(mockScenarioGridPanel, times(1)).unregister();
    }
}
