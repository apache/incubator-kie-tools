/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.wizard;

import java.lang.reflect.Field;
import java.util.Arrays;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.client.wizard.config.process.ProcessConfigPagePresenter;
import org.kie.workbench.common.screens.server.management.client.wizard.container.NewContainerFormPresenter;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewContainerWizardTest {

    @Mock
    NewContainerFormPresenter newContainerFormPresenter;

    @Mock
    NewContainerFormPresenter.View newContainerFormPresenterView;

    @Mock
    ProcessConfigPagePresenter processConfigPagePresenter;

    Caller<SpecManagementService> specManagementServiceCaller;

    @Mock
    SpecManagementService specManagementService;

    Caller<M2RepoService> m2RepoServiceCaller;

    @Mock
    M2RepoService m2RepoService;

    @Spy
    Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    @Spy
    Event<ServerTemplateSelected> serverTemplateSelectedEvent = new EventSourceMock<ServerTemplateSelected>();

    @Mock
    Event<DependencyPathSelectedEvent> dependencyPathSelectedEvent = new EventSourceMock<DependencyPathSelectedEvent>();

    @Mock
    WizardView view;

    @Mock
    ConfirmPopup confirmPopup;

    NewContainerWizard newContainerWizard;

    @Before
    public void setup() throws IllegalAccessException, NoSuchFieldException {
        doNothing().when( notification ).fire( any( NotificationEvent.class ) );
        doNothing().when( serverTemplateSelectedEvent ).fire( any( ServerTemplateSelected.class ) );
        specManagementServiceCaller = new CallerMock<SpecManagementService>( specManagementService );
        m2RepoServiceCaller = new CallerMock<>(m2RepoService);
        when( newContainerFormPresenter.getView() ).thenReturn( newContainerFormPresenterView );
        newContainerWizard = spy( new NewContainerWizard(
                newContainerFormPresenter,
                processConfigPagePresenter,
                specManagementServiceCaller,
                notification,
                serverTemplateSelectedEvent,
                dependencyPathSelectedEvent,
                m2RepoServiceCaller,
                confirmPopup
        ) );

        final Field field = AbstractWizard.class.getDeclaredField( "view" );
        field.setAccessible( true );
        field.set( newContainerWizard, view );
    }

    @Test
    public void testTitle() {
        final String title = "title";
        when( newContainerFormPresenterView.getNewContainerWizardTitle() ).thenReturn( title );

        assertEquals( title, newContainerWizard.getTitle() );
        verify( newContainerFormPresenterView ).getNewContainerWizardTitle();
    }

    @Test
    public void testClear() {
        newContainerWizard.clear();

        verifyClear();
    }

    private void verifyClear() {
        verify( newContainerFormPresenter ).clear();
        verify( processConfigPagePresenter ).clear();
        assertEquals( 1, newContainerWizard.getPages().size() );
        assertTrue( newContainerWizard.getPages().contains( newContainerFormPresenter ) );
        assertFalse(newContainerWizard.isSelected);
    }

    @Test
    public void testSetServerTemplate() {
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateId", "ServerTemplateName" );
        serverTemplate.getCapabilities().add( Capability.PROCESS.toString() );

        newContainerWizard.setServerTemplate( serverTemplate );

        verify( newContainerFormPresenter ).setServerTemplate( serverTemplate );
        assertEquals( 2, newContainerWizard.getPages().size() );
        assertTrue( newContainerWizard.getPages().contains( newContainerFormPresenter ) );
        assertTrue( newContainerWizard.getPages().contains( processConfigPagePresenter ) );
    }

    @Test
    public void testComplete() {
        final ServerTemplate serverTemplate = new ServerTemplate( "ServerTemplateId", "ServerTemplateName" );
        serverTemplate.getCapabilities().add( Capability.PROCESS.toString() );
        final ContainerSpec containerSpec = new ContainerSpec();
        containerSpec.setId( "containerSpecId" );
        PageResponse<JarListPageRow> response = new PageResponse<JarListPageRow>();
        JarListPageRow jarListPageRow = new JarListPageRow();
        GAV gav = new GAV("test", "test", "1.0");
        containerSpec.setReleasedId(new ReleaseId(gav.getGroupId(), gav.getArtifactId(), gav.getVersion()));

        jarListPageRow.setGav(gav);
        jarListPageRow.setPath("test_path");
        response.setPageRowList(Arrays.asList(jarListPageRow));
        when(m2RepoService.listArtifacts(any())).thenReturn(response);
        when( newContainerFormPresenter.buildContainerSpec( eq( serverTemplate.getId() ), anyMap() ) ).thenReturn( containerSpec );
        when( newContainerFormPresenter.getServerTemplate() ).thenReturn( serverTemplate );
        final String successMessage = "SUCCESS";
        when( newContainerFormPresenterView.getNewContainerWizardSaveSuccess() ).thenReturn( successMessage );


        newContainerWizard.setServerTemplate( serverTemplate );
        newContainerWizard.complete();

        verify( processConfigPagePresenter ).buildProcessConfig();
        verify( newContainerFormPresenter ).buildContainerSpec( eq( serverTemplate.getId() ), anyMap() );

        final ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass( NotificationEvent.class );
        verify( notification ).fire( eventCaptor.capture() );
        final NotificationEvent event = eventCaptor.getValue();
        assertEquals( successMessage, event.getNotification() );
        assertEquals( NotificationEvent.NotificationType.SUCCESS, event.getType() );

        final ArgumentCaptor<ServerTemplateSelected> serverTemplateEventCaptor = ArgumentCaptor.forClass( ServerTemplateSelected.class );
        verify( serverTemplateSelectedEvent ).fire( serverTemplateEventCaptor.capture() );
        final ServerTemplateSelected serverEvent = serverTemplateEventCaptor.getValue();
        assertEquals( serverTemplate, serverEvent.getServerTemplateKey() );
        assertEquals( containerSpec.getId(), serverEvent.getContainerId() );

        verifyClear();

        doThrow( new RuntimeException() ).when( specManagementService ).saveContainerSpec( anyString(), any( ContainerSpec.class ) );
        final String errorMessage = "ERROR";
        when( newContainerFormPresenterView.getNewContainerWizardSaveError() ).thenReturn( errorMessage );

        newContainerWizard.complete();

        verify( notification ).fire( new NotificationEvent( errorMessage, NotificationEvent.NotificationType.ERROR ) );
        verify( newContainerWizard ).pageSelected( 0 );
        verify( newContainerWizard ).start();
        verify( newContainerFormPresenter ).initialise();
    }

    @Test
    public void testCompleteByCanNotFind() {
        final ServerTemplate serverTemplate = new ServerTemplate("ServerTemplateId", "ServerTemplateName");
        serverTemplate.getCapabilities().add(Capability.PROCESS.toString());
        final ContainerSpec containerSpec = new ContainerSpec();
        containerSpec.setId("containerSpecId");
        PageResponse<JarListPageRow> response = new PageResponse<JarListPageRow>();
        JarListPageRow jarListPageRow = new JarListPageRow();
        GAV gav = new GAV("test", "test", "1.0");
        containerSpec.setReleasedId(new ReleaseId(gav.getGroupId(), gav.getArtifactId(), gav.getVersion()));
        jarListPageRow.setGav(new GAV("test1", "test1", "2.0"));
        jarListPageRow.setPath("test_path");

        response.setPageRowList(Arrays.asList(jarListPageRow));

        when(m2RepoService.listArtifacts(any())).thenReturn(response);
        when(newContainerFormPresenter.buildContainerSpec(eq(serverTemplate.getId()), anyMap())).thenReturn(containerSpec);
        when(newContainerFormPresenter.getServerTemplate()).thenReturn(serverTemplate);
        final String successMessage = "SUCCESS";
        doNothing().when(specManagementService).saveContainerSpec(anyString(), any(ContainerSpec.class));
        when(newContainerFormPresenterView.getNewContainerWizardSaveSuccess()).thenReturn(successMessage);
        newContainerWizard.setServerTemplate(serverTemplate);
        newContainerWizard.complete();

        verify(processConfigPagePresenter).buildProcessConfig();
        verify(newContainerFormPresenter).buildContainerSpec(eq(serverTemplate.getId()), anyMap());

        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);
        verify(confirmPopup).show(any(), any(), any(), captureCommand.capture());
        captureCommand.getValue().execute();

        final ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notification).fire(eventCaptor.capture());
        final NotificationEvent event = eventCaptor.getValue();
        assertEquals(successMessage, event.getNotification());
        assertEquals(NotificationEvent.NotificationType.SUCCESS, event.getType());

        final ArgumentCaptor<ServerTemplateSelected> serverTemplateEventCaptor = ArgumentCaptor.forClass(ServerTemplateSelected.class);
        verify(serverTemplateSelectedEvent).fire(serverTemplateEventCaptor.capture());
        final ServerTemplateSelected serverEvent = serverTemplateEventCaptor.getValue();
        assertEquals(serverTemplate, serverEvent.getServerTemplateKey());
        assertEquals(containerSpec.getId(), serverEvent.getContainerId());

        verifyClear();
    }

    @Test
    public void testClose() {
        newContainerWizard.close();

        verifyClear();
    }

    @Test
    public void testPageSelectedPageTwo() {
        preparePageSelected();

        newContainerWizard.pageSelected(1);
        verify(dependencyPathSelectedEvent).fire(any());
    }

    private void preparePageSelected() {
        PageResponse<JarListPageRow> response = new PageResponse<JarListPageRow>();
        JarListPageRow jarListPageRow = new JarListPageRow();
        GAV gav = new GAV("test", "test", "");
        jarListPageRow.setGav(gav);
        jarListPageRow.setPath("test_path");
        response.setPageRowList(Arrays.asList(jarListPageRow));
        when(m2RepoService.listArtifacts(any())).thenReturn(response);
        when(newContainerFormPresenter.getCurrentGAV()).thenReturn(gav);

        newContainerWizard.pages.add(mock(WizardPage.class));
        newContainerWizard.pages.add(mock(WizardPage.class));
    }

    @Test
    public void testPageSelectedPageOne() {
        preparePageSelected();

        newContainerWizard.pageSelected(0);
        verify(dependencyPathSelectedEvent, never()).fire(any());
        verify(notification, never()).fire(any());
    }

    @Test
    public void testPageSelectedIsSelected() {
        preparePageSelected();
        newContainerWizard.isSelected = true;
        newContainerWizard.pageSelected(1);
        verify(dependencyPathSelectedEvent, never()).fire(any());
        verify(notification, never()).fire(any());
    }

    @Test
    public void testPageSelectedWithEmptyPath() {
        PageResponse<JarListPageRow> response = new PageResponse<JarListPageRow>();
        JarListPageRow jarListPageRow = new JarListPageRow();
        GAV gav = new GAV("test", "test", "");
        jarListPageRow.setGav(gav);
        jarListPageRow.setPath("");
        response.setPageRowList(Arrays.asList(jarListPageRow));
        when(m2RepoService.listArtifacts(any())).thenReturn(response);
        when(newContainerFormPresenter.getCurrentGAV()).thenReturn(gav);

        newContainerWizard.pages.add(mock(WizardPage.class));
        newContainerWizard.pages.add(mock(WizardPage.class));

        newContainerWizard.pageSelected(1);

        verify(dependencyPathSelectedEvent, never()).fire(any());
        verify(notification, never()).fire(any());
    }

    @Test
    public void testPageSelectedWithNullPath() {
        PageResponse<JarListPageRow> response = new PageResponse<JarListPageRow>();
        JarListPageRow jarListPageRow = new JarListPageRow();
        GAV gav = new GAV("test", "test", "");
        jarListPageRow.setGav(gav);
        jarListPageRow.setPath(null);
        response.setPageRowList(Arrays.asList(jarListPageRow));
        when(m2RepoService.listArtifacts(any())).thenReturn(response);
        when(newContainerFormPresenter.getCurrentGAV()).thenReturn(gav);

        newContainerWizard.pages.add(mock(WizardPage.class));
        newContainerWizard.pages.add(mock(WizardPage.class));

        newContainerWizard.pageSelected(1);

        verify(dependencyPathSelectedEvent, never()).fire(any());
        verify(notification, never()).fire(any());
    }

    @Test
    public void testPageSelectedCanNotFind() {
        PageResponse<JarListPageRow> response = new PageResponse<JarListPageRow>();
        JarListPageRow jarListPageRow = new JarListPageRow();
        GAV gav = new GAV("test", "test", "");
        jarListPageRow.setGav(new GAV("test1", "test1", "1.0"));
        jarListPageRow.setPath("test_path");
        response.setPageRowList(Arrays.asList(jarListPageRow));
        when(m2RepoService.listArtifacts(any())).thenReturn(response);
        when(newContainerFormPresenter.getCurrentGAV()).thenReturn(gav);
        final String gavNotFind = "NOTFIND";
        when(newContainerFormPresenterView.getNewContainerGAVNotExist(any())).thenReturn(gavNotFind);

        newContainerWizard.pages.add(mock(WizardPage.class));
        newContainerWizard.pages.add(mock(WizardPage.class));

        newContainerWizard.pageSelected(1);

        final ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notification).fire(eventCaptor.capture());
        final NotificationEvent event = eventCaptor.getValue();
        assertEquals(gavNotFind, event.getNotification());
    }

    @Test
    public void testOnDependencyPathSelectedEvent() {
        assertFalse(newContainerWizard.isSelected);
        newContainerWizard.onDependencyPathSelectedEvent(new DependencyPathSelectedEvent("null", "test"));
        assertTrue(newContainerWizard.isSelected);
    }
}
