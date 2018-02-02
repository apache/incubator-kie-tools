/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.client.editor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectScreenPresenterTest
        extends ProjectScreenPresenterTestBase {

    @GwtMock
    @SuppressWarnings("unused")
    private com.google.gwt.user.client.ui.Widget dependenciesPart;

    private ProjectScreenModel model;

    @Before
    public void setup() {
        ApplicationPreferences.setUp(new HashMap<>());

        //The BuildOptions widget is manipulated in the Presenter so we need some nasty mocking
        mockBuildOptions();

        //Mock ProjectScreenService
        model = new ProjectScreenModel();

        final POM pom = mockProjectScreenService(model);

        //Mock WorkspaceProjectContext
        mockWorkspaceProjectContext(pom,
                           repository,
                           module,
                           pomPath);

        //Mock LockManager initialisation
        mockLockManager(model);

        doReturn(new WorkspaceProject()).when(projectScreenService).save(any(), any(), anyString(), any());

        constructProjectScreenPresenter(module);

        verify(view,
               times(1)).setGAVCheckDisabledSetting(eq(false));
        verifyBusyShowHideAnyString(1,
                                    1,
                                    CommonConstants.INSTANCE.Loading());
    }

    @Test
    public void testIsDeploymentDescriptorEditorAvailable() {
        final ResourceTypeDefinition editor = mock(ResourceTypeDefinition.class);
        when(editor.getPrefix()).thenReturn("kie-deployment-descriptor");
        when(editor.getSuffix()).thenReturn("xml");
        when(editor.accept(any(Path.class))).thenReturn(true);

        assertFalse(presenter.isDeploymentDescritorEditorAvailable(Arrays.<ResourceTypeDefinition>asList(new AnyResourceTypeDefinition()).stream(),
                                                                   mock(Path.class)));

        assertTrue(presenter.isDeploymentDescritorEditorAvailable(Arrays.<ResourceTypeDefinition>asList(new AnyResourceTypeDefinition(),
                                                                                                        editor).stream(),
                                                                  mock(Path.class)));
    }

    @Test
    public void testIsDirtyBuild() {
        model.setPOM(mock(POM.class)); // causes isDirty evaluates as true
        presenter.triggerBuild();

        verify(view,
               times(1)).showSaveBeforeContinue(any(Command.class),
                                                any(Command.class),
                                                any(Command.class));
        verify(notificationEvent,
               never()).fire(any(NotificationEvent.class));
        verifyBusyShowHideAnyString(1,
                                    1);
    }

    @Test
    public void testIsDirtyBuildAndInstall() {
        model.setPOM(mock(POM.class)); // causes isDirty evaluates as true
        presenter.triggerBuildAndDeploy();

        verify(view,
               times(1)).showSaveBeforeContinue(any(Command.class),
                                                any(Command.class),
                                                any(Command.class));
        verify(notificationEvent,
               never()).fire(any(NotificationEvent.class));
        verifyBusyShowHideAnyString(1,
                                    1);
    }

    @Test
    public void testIsDirtyBuildAndDeploy() {
        model.setPOM(mock(POM.class)); // causes isDirty evaluates as true
        presenter.triggerBuildAndDeploy();

        verify(view,
               times(1)).showSaveBeforeContinue(any(Command.class),
                                                any(Command.class),
                                                any(Command.class));
        verify(notificationEvent,
               never()).fire(any(NotificationEvent.class));
        verifyBusyShowHideAnyString(1,
                                    1);
    }

    @Test
    public void testOnDependenciesSelected() throws Exception {

        when(lockManagerInstanceProvider.get()).thenReturn(mock(LockManager.class));

        Path pathToPOM = mock(Path.class);
        model.setPathToPOM(pathToPOM);

        when(view.getDependenciesPart()).thenReturn(dependenciesPart);

        presenter.onStartup(mock(PlaceRequest.class));

        presenter.onDependenciesSelected();

        verify(view).showDependenciesPanel();
    }

    @Test
    public void testSaveNonClashingGAV() throws Exception {
        savePopUpPresenterShowMock();

        verifyBusyShowHideAnyString(1,
                                    1,
                                    CommonConstants.INSTANCE.Loading());

        final Command command = presenter.getSaveCommand(DeploymentMode.VALIDATED);
        command.execute();

        verify(projectScreenService,
               times(1)).save(eq(presenter.pathToPomXML),
                              eq(model),
                              eq(""),
                              eq(DeploymentMode.VALIDATED));
        verifyBusyShowHideAnyString(1,
                                    3,
                                    CommonConstants.INSTANCE.Saving());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSaveClashingGAV() throws Exception {
        savePopUpPresenterShowMock();

        verifyBusyShowHideAnyString(1,
                                    1,
                                    CommonConstants.INSTANCE.Loading());

        doThrow(GAVAlreadyExistsException.class).when(projectScreenService).save(eq(presenter.pathToPomXML),
                                                                                 eq(model),
                                                                                 eq(""),
                                                                                 eq(DeploymentMode.VALIDATED));

        final GAV gav = model.getPOM().getGav();
        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        final Command command = presenter.getSaveCommand(DeploymentMode.VALIDATED);

        command.execute();

        verify(projectScreenService,
               times(1)).save(eq(presenter.pathToPomXML),
                              eq(model),
                              eq(""),
                              eq(DeploymentMode.VALIDATED));

        verify(conflictingRepositoriesPopup,
               times(1)).setContent(eq(gav),
                                    any(Set.class),
                                    commandArgumentCaptor.capture());
        verify(conflictingRepositoriesPopup,
               times(1)).show();

        assertNotNull(commandArgumentCaptor.getValue());

        //Emulate User electing to force save
        commandArgumentCaptor.getValue().execute();

        verify(projectScreenService,
               times(1)).save(eq(presenter.pathToPomXML),
                              eq(model),
                              eq(""),
                              eq(DeploymentMode.FORCED));
        //We attempted to save the Project twice
        //We hid the BusyPopup 1 x loading, 1 x per save attempt
        verifyBusyShowHideAnyString(2,
                                    4,
                                    CommonConstants.INSTANCE.Saving());
    }

    private void savePopUpPresenterShowMock() {
        Answer fakeShow = invocation -> {
            ParameterizedCommand<String> cmd = (ParameterizedCommand<String>) invocation.getArguments()[1];
            cmd.execute("");
            return null;
        };
        doAnswer(fakeShow).when(savePopUpPresenter).show(any(Path.class),
                                                         any(ParameterizedCommand.class));
    }

    @Test
    public void testGetReimportCommand() throws Exception {

        Command reImportCommand = presenter.getReImportCommand();

        reImportCommand.execute();

        verify(projectScreenService,
               times(1)).reImport(eq(presenter.pathToPomXML));
    }

    @Test
    public void moduleContextWithProjectEnablesMenus() {
        presenter.onStartup(mock(PlaceRequest.class));

        assertMenuItems(true);
    }

    private void assertMenuItems(final boolean enabled) {
        presenter.getMenus().getItems().forEach((m) -> assertEquals(enabled,
                                                                    m.isEnabled()));
    }

    private void verifyBusyShowHideAnyString(final int show,
                                             final int hide) {
        verifyBusyShowHideAnyString(show,
                                    hide,
                                    null);
    }

    private void verifyBusyShowHideAnyString(final int show,
                                             final int hide,
                                             final String message) {
        if (message != null) {
            verify(view,
                   times(show)).showBusyIndicator(message);
        } else {
            verify(view,
                   times(show)).showBusyIndicator(anyString());
        }

        verify(view,
               times(hide)).hideBusyIndicator();
    }
}
