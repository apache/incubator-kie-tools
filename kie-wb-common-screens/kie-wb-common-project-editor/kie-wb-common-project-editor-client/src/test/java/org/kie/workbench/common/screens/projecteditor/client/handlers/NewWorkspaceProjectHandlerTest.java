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

package org.kie.workbench.common.screens.projecteditor.client.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwt.core.client.Callback;
import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.preferences.LibraryOrganizationalUnitPreferences;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.projecteditor.client.wizard.NewWorkspaceProjectWizard;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;

@RunWith(MockitoJUnitRunner.class)
public class NewWorkspaceProjectHandlerTest {

    @Mock
    private WorkspaceProjectContext context;
    @Mock
    private NewWorkspaceProjectWizard wizard;
    @Mock
    private Repository repository;
    @Mock
    private ProjectController projectController;
    @Mock
    private OrganizationalUnitService organizationalUnitService;
    @Mock
    private EventSourceMock<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;

    @Mock
    private LibraryPreferences libraryPreferences;

    private NewWorkspaceProjectHandler handler;
    private AnyResourceTypeDefinition resourceType = mock(AnyResourceTypeDefinition.class);
    private NewResourcePresenter newResourcePresenter = mock(NewResourcePresenter.class);

    @Before
    public void setup() {

        handler = new NewWorkspaceProjectHandler(context,
                                                 projectContextChangeEvent,
                                                 libraryPreferences,
                                                 wizard,
                                                 new CallerMock<>(organizationalUnitService),
                                                 projectController,
                                                 resourceType);

        when(context.getActiveOrganizationalUnit()).thenReturn(Optional.empty());
        when(context.getActiveWorkspaceProject()).thenReturn(Optional.empty());
        when(context.getActiveModule()).thenReturn(Optional.empty());
        when(context.getActiveRepositoryRoot()).thenReturn(Optional.empty());
        when(context.getActivePackage()).thenReturn(Optional.empty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreate() {
        handler.create(mock(org.guvnor.common.services.project.model.Package.class),
                       "projectName",
                       newResourcePresenter);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testValidate() {
        handler.validate("projectName",
                         mock(ValidatorWithReasonCallback.class));
    }

    @Test
    public void testAcceptContextNoActiveOrganizationalUnit() {
        when(context.getActiveWorkspaceProject()).thenReturn(Optional.empty());

        final Callback<Boolean, Void> callback = mock(Callback.class);
        handler.acceptContext(callback);

        verify(callback,
               times(1)).onSuccess(eq(false));
    }

    @Test
    public void testAcceptContextWithActiveActiveOrganizationalUnit() {
        when(context.getActiveOrganizationalUnit()).thenReturn(Optional.of(mock(OrganizationalUnit.class)));

        final Callback<Boolean, Void> callback = mock(Callback.class);
        handler.acceptContext(callback);

        verify(callback,
               times(1)).onSuccess(eq(true));
    }

    @Test
    public void testGetCommandWithActiveRepository() {

        final LibraryOrganizationalUnitPreferences libraryOrganizationalUnitPreferences = mock(LibraryOrganizationalUnitPreferences.class);
        when(libraryOrganizationalUnitPreferences.getName()).thenReturn("myOU");
        when(libraryPreferences.getOrganizationalUnitPreferences()).thenReturn(libraryOrganizationalUnitPreferences);

        when(context.getActiveWorkspaceProject()).thenReturn(Optional.of(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                                                              repository,
                                                                                              mock(Branch.class),
                                                                                              mock(Module.class))));
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(organizationalUnit.getDefaultGroupId()).thenReturn("defaultGroupId");
        when(organizationalUnitService.getOrganizationalUnit("myOU")).thenReturn(organizationalUnit);

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                when(context.getActiveOrganizationalUnit()).thenReturn(Optional.of(organizationalUnit));
                return null;
            }
        }).when(projectContextChangeEvent).fire(any(WorkspaceProjectContextChangeEvent.class));

        final Command command = handler.getCommand(newResourcePresenter);
        assertNotNull(command);

        command.execute();

        ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(wizard,
               times(1)).initialise(pomArgumentCaptor.capture());
        verify(wizard,
               times(1)).start(any(org.uberfire.client.callbacks.Callback.class),
                               anyBoolean());

        assertEquals("defaultGroupId",
                     pomArgumentCaptor.getValue().getGav().getGroupId());
        assertEquals("kjar",
                     pomArgumentCaptor.getValue().getPackaging());
    }
}
