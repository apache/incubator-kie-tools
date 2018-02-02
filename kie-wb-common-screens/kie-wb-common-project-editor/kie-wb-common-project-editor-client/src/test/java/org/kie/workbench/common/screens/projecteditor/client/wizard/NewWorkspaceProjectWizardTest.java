/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.kie.workbench.common.screens.projecteditor.client.wizard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.util.KiePOMDefaultOptions;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@RunWith(GwtMockitoTestRunner.class)
public class NewWorkspaceProjectWizardTest {

    @GwtMock
    WizardView view;
    @Mock
    BusyIndicatorView busyIndicatorView;
    @Mock
    ConflictingRepositoriesPopup conflictingRepositoriesPopup;
    KiePOMDefaultOptions pomDefaultOptions;
    @Mock
    private POMWizardPage pomWizardPage;
    @Mock
    private WorkspaceProjectContext moduleContext;
    @Mock
    private WorkspaceProjectService projectService;
    @Spy
    private Event<NotificationEvent> notificationEventEvent = new EventSourceMock<NotificationEvent>() {
        @Override
        public void fire(final NotificationEvent event) {
            //Do nothing. Default implementation throws an UnsupportedOperationException
        }
    };
    private NewWorkspaceProjectWizardExtended wizard;

    private HashMap<String, String> preferences;

    @Before
    public void setUp() throws Exception {
        preferences = new HashMap<>();
        ApplicationPreferences.setUp(preferences);
        pomDefaultOptions = new KiePOMDefaultOptions();
        PlaceManager placeManager = mock(PlaceManager.class);
        wizard = new NewWorkspaceProjectWizardExtended(new CallerMock<>(projectService),
                                                       placeManager,
                                                       notificationEventEvent,
                                                       pomWizardPage,
                                                       busyIndicatorView,
                                                       conflictingRepositoriesPopup,
                                                       moduleContext,
                                                       view,
                                                       pomDefaultOptions
        );

        when(moduleContext.getActiveOrganizationalUnit()).thenReturn(Optional.empty());
        when(moduleContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());
        when(moduleContext.getActiveModule()).thenReturn(Optional.empty());
        when(moduleContext.getActiveRepositoryRoot()).thenReturn(Optional.empty());
        when(moduleContext.getActivePackage()).thenReturn(Optional.empty());

        wizard.setupPages();
    }

    @Test
    public void testGetPages() {
        assertEquals(1,
                     wizard.getPages().size());
        assertEquals(pomWizardPage,
                     wizard.getPages().get(0));
    }

    @Test
    public void testIsComplete() {
        Callback<Boolean> callback = mock(Callback.class);
        wizard.isComplete(callback);

        verify(pomWizardPage,
               times(1)).isComplete(callback);
    }

    @Test
    public void testWizardIsCompleted() {
        doAnswer(new PageCompletedAnswer(true)).when(pomWizardPage).isComplete(any(Callback.class));
        wizard.start();
        verify(view,
               times(1)).setPageCompletionState(0,
                                                true);
        verify(view,
               times(1)).setCompletionStatus(true);
    }

    @Test
    public void testWizardIsNotCompleted() {
        doAnswer(new PageCompletedAnswer(false)).when(pomWizardPage).isComplete(any(Callback.class));
        wizard.start();
        verify(view,
               times(1)).setPageCompletionState(0,
                                                false);
        verify(view,
               times(1)).setCompletionStatus(false);
    }

    @Test
    public void testSetContentGAV() throws Exception {
        preferences.put("kie_version",
                        "1.3.0");
        OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(organizationalUnit.getDefaultGroupId()).thenReturn("mygroup");
        when(moduleContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(organizationalUnit));

        POM pom = new POM();
        pom.setName("another module");
        pom.getGav().setArtifactId("another.artifact");
        pom.getGav().setGroupId("another.group");
        pom.getGav().setVersion("1.2.3");
        wizard.initialise(pom);

        ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);

        verify(pomWizardPage).setPom(pomArgumentCaptor.capture());

        POM result = pomArgumentCaptor.getValue();

        assertEquals("1.2.3",
                     result.getGav().getVersion());
        assertEquals("another.artifact",
                     result.getGav().getArtifactId());
        assertEquals("another.group",
                     result.getGav().getGroupId());
        assertEquals("another module",
                     result.getName());

        assertEquals(1,
                     result.getBuild().getPlugins().size());
        assertEquals("1.3.0",
                     result.getBuild().getPlugins().get(0).getVersion());
    }

    @Test
    public void testInitialize() throws Exception {
        OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(organizationalUnit.getDefaultGroupId()).thenReturn("mygroup");
        when(moduleContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(organizationalUnit));

        wizard.initialise();

        ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);
        verify(pomWizardPage).setPom(pomArgumentCaptor.capture());

        assertEquals("mygroup",
                     pomArgumentCaptor.getValue().getGav().getGroupId());
    }

    @Test
    public void testCompleteNonClashingGAV() throws Exception {
        final Path repositoryRoot = mock(Path.class);
        final POM pom = mock(POM.class);
        when(moduleContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(mock(OrganizationalUnit.class)));
        when(moduleContext.getActiveRepositoryRoot()).thenReturn(Optional.of(repositoryRoot));
        when(pomWizardPage.getPom()).thenReturn(pom);

        wizard.complete();
        verify(projectService,
               times(1)).newProject(any(OrganizationalUnit.class),
                                    eq(pom),
                                    eq(DeploymentMode.VALIDATED));
        verify(busyIndicatorView,
               times(1)).showBusyIndicator(any(String.class));
        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompleteClashingGAV() throws Exception {
        final Path repositoryRoot = mock(Path.class);
        final POM pom = mock(POM.class);
        final GAV gav = mock(GAV.class);
        when(projectService.newProject(any(OrganizationalUnit.class),
                                       eq(pom),
                                       eq(DeploymentMode.VALIDATED))).thenThrow(GAVAlreadyExistsException.class);
        when(moduleContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(mock(OrganizationalUnit.class)));
        when(moduleContext.getActiveRepositoryRoot()).thenReturn(Optional.of(repositoryRoot));
        when(pomWizardPage.getPom()).thenReturn(pom);
        when(pom.getGav()).thenReturn(gav);

        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);

        wizard.complete();
        verify(projectService,
               timeout(1)).newProject(any(OrganizationalUnit.class),
                                      eq(pom),
                                      eq(DeploymentMode.VALIDATED));

        verify(busyIndicatorView,
               times(1)).showBusyIndicator(any(String.class));
        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
        verify(conflictingRepositoriesPopup,
               times(1)).setContent(eq(gav),
                                    any(Set.class),
                                    commandArgumentCaptor.capture());
        verify(conflictingRepositoriesPopup,
               times(1)).show();

        assertNotNull(commandArgumentCaptor.getValue());

        //Emulate User electing to force save
        commandArgumentCaptor.getValue().execute();

        verify(conflictingRepositoriesPopup,
               times(1)).hide();

        verify(projectService,
               times(1)).newProject(any(OrganizationalUnit.class),
                                    eq(pom),
                                    eq(DeploymentMode.FORCED));
        verify(busyIndicatorView,
               times(2)).showBusyIndicator(any(String.class));
        verify(busyIndicatorView,
               times(2)).hideBusyIndicator();
    }

    @Test
    public void testCompleteCallsCallbackOnce() {
        final Callback<WorkspaceProject> projectCallback = mock(Callback.class);
        when(moduleContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(mock(OrganizationalUnit.class)));

        wizard.start(projectCallback,
                     false);
        wizard.close();

        verify(projectCallback,
               never()).callback(any());

        wizard.complete();

        verify(projectCallback,
               times(1)).callback(any());
    }

    public static class NewWorkspaceProjectWizardExtended extends NewWorkspaceProjectWizard {

        public NewWorkspaceProjectWizardExtended(final Caller<WorkspaceProjectService> projectService,
                                                 final PlaceManager placeManager,
                                                 final Event<NotificationEvent> notificationEvent,
                                                 final POMWizardPage pomWizardPage,
                                                 final BusyIndicatorView busyIndicatorView,
                                                 final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                                                 final WorkspaceProjectContext context,
                                                 final WizardView view,
                                                 final KiePOMDefaultOptions pomDefaultOptions) {
            super(projectService,
                  placeManager,
                  notificationEvent,
                  pomWizardPage,
                  busyIndicatorView,
                  conflictingRepositoriesPopup,
                  context,
                  pomDefaultOptions);

            super.view = view;
        }
    }

    private class PageCompletedAnswer implements Answer {

        private boolean isComplete;

        public PageCompletedAnswer(boolean isComplete) {
            this.isComplete = isComplete;
        }

        @Override
        public Object answer(InvocationOnMock invocationOnMock) throws Throwable {

            Callback<Boolean> callback = (Callback<Boolean>) invocationOnMock.getArguments()[0];
            callback.callback(isComplete);

            return null;
        }
    }
}
