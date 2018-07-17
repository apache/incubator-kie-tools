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

import java.util.Optional;

import com.google.gwt.core.client.GWT;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.type.ScenarioSimulationResourceType;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationEditorPresenterTest {

    @Mock
    private ScenarioSimulationView view;
    @Mock
    private KieEditorWrapperView mockKieView;

    @Mock
    private OverviewWidgetPresenter mockOverviewWidget;

    @Mock
    private VersionRecordManager mockVersionRecordManager;

    @Mock
    private FileMenuBuilder mockFileMenuBuilder;

    @Mock
    private DefaultFileNameValidator mockFileNameValidator;

    @Mock
    private ScenarioSimulationService scenarioSimulationService;

    @Mock
    private ObservablePath path;

    @Mock
    private PlaceRequest place;

    @Mock
    private Overview overview;

    @Mock
    private WorkspaceProjectContext mockWorkbenchContext;

    @Mock
    private SaveAndRenameCommandBuilder<String, Metadata> mockSaveAndRenameCommandBuilder;

    @Mock
    private AlertsButtonMenuItemBuilder mockAlertsButtonMenuItemBuilder;

    @GwtMock
    private ViewDRLSourceWidget sourceWidget;

    @Captor
    private ArgumentCaptor<String> enumStringArgumentCaptor;

    @Mock
    private EventSourceMock<NotificationEvent> mockNotification;

    private ScenarioSimulationResourceType type;

    private ScenarioSimulationEditorPresenter presenter;

    private ScenarioSimulationModelContent content;
    private ScenarioSimulationModel model;

    @Before
    public void setup() {
        this.type = GWT.create(ScenarioSimulationResourceType.class);

        //Mock FileMenuBuilder usage since we cannot use FileMenuBuilderImpl either
        when(mockFileMenuBuilder.addSave(any(MenuItem.class))).thenReturn(mockFileMenuBuilder);
        when(mockFileMenuBuilder.addCopy(any(ObservablePath.class), any(DefaultFileNameValidator.class))).thenReturn(mockFileMenuBuilder);
        when(mockFileMenuBuilder.addRename(any(Command.class))).thenReturn(mockFileMenuBuilder);
        when(mockFileMenuBuilder.addDelete(any(ObservablePath.class))).thenReturn(mockFileMenuBuilder);
        when(mockFileMenuBuilder.addValidate(any(Command.class))).thenReturn(mockFileMenuBuilder);
        when(mockFileMenuBuilder.addNewTopLevelMenu(any(MenuItem.class))).thenReturn(mockFileMenuBuilder);

        when(mockVersionRecordManager.getCurrentPath()).thenReturn(path);
        when(mockVersionRecordManager.getPathToLatest()).thenReturn(path);

        when(mockWorkbenchContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());

        this.model = new ScenarioSimulationModel();
        this.content = new ScenarioSimulationModelContent(model,
                                                          overview);

        when(scenarioSimulationService.loadContent(path)).thenReturn(content);

        this.presenter = spy(new ScenarioSimulationEditorPresenter(view,
                                                                   new CallerMock<>(scenarioSimulationService),
                                                                   type) {
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
            protected Command getSaveAndRename() {
                return mock(Command.class);
            }
        });
    }

    @Test
    public void testOnStartup() {
        presenter.onStartup(mock(ObservablePath.class),
                            mock(PlaceRequest.class));

        verify(view).showLoading();
        verify(view).hideBusyIndicator();
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

        reset(view);

        presenter.save("save message");

        verify(view).hideBusyIndicator();
        verify(mockNotification).fire(any(NotificationEvent.class));
        verify(mockVersionRecordManager).reloadVersions(any(Path.class));
    }
}
