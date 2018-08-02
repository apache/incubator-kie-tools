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
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
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
    private ScenarioGridLayer scenarioGridLayer;

    @Mock
    private ScenarioSimulationView scenarioSimulationView;

    @Before
    public void setup() {
        super.setup();
        //ScenarioSimulationView scenarioSimulationView = mock(ScenarioSimulationViewImpl.class);
        when(scenarioSimulationView.getScenarioGridPanel()).thenReturn(scenarioGridPanel);

        when(scenarioGridPanel.getDefaultGridLayer()).thenReturn(scenarioGridLayer);

        this.presenter = spy(new ScenarioSimulationEditorPresenter(new CallerMock<>(scenarioSimulationService),
                                                                   type,
                                                                   importsWidget,
                                                                   oracleFactory) {
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
                return scenarioSimulationView/*mock(ScenarioSimulationViewImpl.class)*/;
            }
        });
    }

    @Test
    public void testOnStartup() {

        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);
        when(oracleFactory.makeAsyncPackageDataModelOracle(any(),
                                                           eq(model),
                                                           eq(content.getDataModel()))).thenReturn(oracle);
        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));
        verify(importsWidget).setContent(oracle,
                                         model.getImports(),
                                         false);
        verify(mockKieView).addImportsTab(importsWidget);
        verify(presenter.getView()).showLoading();
        verify(presenter.getView()).hideBusyIndicator();
        verify(scenarioGridLayer, times(1)).enterPinnedMode(any(), any());
        verify(presenter.newScenarioSimulationView(), times(1));
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
}
