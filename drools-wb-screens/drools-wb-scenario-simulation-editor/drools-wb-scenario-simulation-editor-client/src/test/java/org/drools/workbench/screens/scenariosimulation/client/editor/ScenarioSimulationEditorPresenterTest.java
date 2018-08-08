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
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
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

    @Mock
    private ScenarioGridLayer mockScenarioGridLayer;

    @Mock
    private ScenarioSimulationView mockScenarioSimulationView;

    @Mock
    private PlaceManager mockPlaceManager;

    @Mock
    private PlaceRequest mockPlaceRequest;

    @Before
    public void setup() {
        super.setup();
        when(mockScenarioSimulationView.getScenarioGridPanel()).thenReturn(mockScenarioGridPanel);

        when(mockScenarioGridPanel.getDefaultGridLayer()).thenReturn(mockScenarioGridLayer);

        when(mockPlaceRequest.getIdentifier()).thenReturn(ScenarioSimulationEditorPresenter.IDENTIFIER);

        this.presenter = spy(new ScenarioSimulationEditorPresenter(new CallerMock<>(scenarioSimulationService),
                                                                   type,
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
            protected ScenarioSimulationView newScenarioSimulationView() {
                return mockScenarioSimulationView;
            }
        });
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
        verify(presenter.getView()).showLoading();
        verify(presenter.getView()).hideBusyIndicator();
        verify(mockScenarioGridLayer, times(1)).enterPinnedMode(any(), any());
    }

    @Test
    public void validateButtonShouldNotBeAdded() {
        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));

        verify(presenter, never()).getValidateCommand();
    }

    @Test
    public void save() {
        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));
        reset(presenter.getView());

        presenter.save("save message");

        verify(presenter.getView()).hideBusyIndicator();
        verify(mockNotification).fire(any(NotificationEvent.class));
        verify(mockVersionRecordManager).reloadVersions(any(Path.class));
    }

    @Test
    public void onPlaceGainFocusEvent() {
        PlaceGainFocusEvent mockPlaceGainFocusEvent = mock(PlaceGainFocusEvent.class);
        when(mockPlaceGainFocusEvent.getPlace()).thenReturn(mockPlaceRequest);
        when(mockPlaceManager.getStatus(RightPanelPresenter.IDENTIFIER)).thenReturn(PlaceStatus.CLOSE);
        presenter.onPlaceGainFocusEvent(mockPlaceGainFocusEvent);
        verify(mockPlaceManager, times(1)).goTo(RightPanelPresenter.IDENTIFIER);
    }

    @Test
    public void onPlaceHiddenEvent() {
        PlaceHiddenEvent mockPlaceHiddenEvent = mock(PlaceHiddenEvent.class);
        when(mockPlaceHiddenEvent.getPlace()).thenReturn(mockPlaceRequest);
        when(mockPlaceManager.getStatus(RightPanelPresenter.IDENTIFIER)).thenReturn(PlaceStatus.OPEN);
        presenter.onPlaceHiddenEvent(mockPlaceHiddenEvent);
        verify(mockPlaceManager, times(1)).closePlace(RightPanelPresenter.IDENTIFIER);
    }
}
