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

package org.kie.workbench.common.screens.server.management.client.wizard.container;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.kie.workbench.common.screens.server.management.client.widget.artifact.ArtifactListWidgetPresenter;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewContainerFormPresenterTest {

    Caller<M2RepoService> m2RepoServiceCaller;

    @Mock
    M2RepoService m2RepoService;

    Caller<SpecManagementService> specManagementServiceCaller;

    @Mock
    SpecManagementService specManagementService;

    @Spy
    Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent = new EventSourceMock<WizardPageStatusChangeEvent>();

    @Mock
    ArtifactListWidgetPresenter artifactListWidgetPresenter;

    @Mock
    NewContainerFormPresenter.View view;

    NewContainerFormPresenter presenter;

    @Before
    public void init() {
        m2RepoServiceCaller = new CallerMock<M2RepoService>( m2RepoService );
        specManagementServiceCaller = new CallerMock<SpecManagementService>( specManagementService );
        doNothing().when( wizardPageStatusChangeEvent ).fire( any( WizardPageStatusChangeEvent.class ) );
        presenter = spy( new NewContainerFormPresenter(
                view,
                m2RepoServiceCaller,
                specManagementServiceCaller,
                wizardPageStatusChangeEvent ) );
        doReturn( artifactListWidgetPresenter ).when( presenter ).buildArtifactListWidgetPresenter();
    }

    @Test
    public void testInit() {
        presenter.init();

        verify( view ).init( presenter );
    }

    @Test
    public void testClear() {
        presenter.clear();

        verify( view ).clear();
        assertEquals( NewContainerFormPresenter.Mode.OPTIONAL, presenter.getMode() );
        assertNull( presenter.getServerTemplate() );
    }

    @Test
    public void testIsEmpty() {
        when( view.getContainerName() ).thenReturn( " " );
        when( view.getGroupId() ).thenReturn( " " );
        when( view.getArtifactId() ).thenReturn( " " );
        when( view.getVersion() ).thenReturn( " " );

        assertTrue( presenter.isEmpty() );
    }

    @Test
    public void testIsValid() {
        when( view.getContainerName() ).thenReturn( " " ).thenReturn( "containerName" ).thenReturn( "" );
        when( view.getGroupId() ).thenReturn( " " ).thenReturn( "groupId" ).thenReturn( "" );
        when( view.getArtifactId() ).thenReturn( " " ).thenReturn( "artifactId" ).thenReturn( "" );
        when( view.getVersion() ).thenReturn( " " ).thenReturn( "1.0" ).thenReturn( "" );

        assertTrue( presenter.isValid() );

        verify( view ).noErrors();

        presenter.setServerTemplate( new ServerTemplate() );

        assertTrue( presenter.isValid() );

        verify( view ).noErrorOnContainerName();
        verify( view ).noErrorOnGroupId();
        verify( view ).noErrorOnArtifactId();
        verify( view ).noErrorOnVersion();

        assertFalse( presenter.isValid() );

        verify( view ).errorOnContainerName();
        verify( view ).errorOnGroupId();
        verify( view ).errorOnArtifactId();
        verify( view ).errorOnVersion();
    }

    @Test
    public void testOnDependencyPathSelectedEvent() {
        final String path = "org:kie:1.0";
        final GAV gav = new GAV( path );
        when( m2RepoService.loadGAVFromJar( path ) ).thenReturn( gav );
        presenter.asWidget();

        presenter.onDependencyPathSelectedEvent( new DependencyPathSelectedEvent( artifactListWidgetPresenter, path ) );

        verify( m2RepoService ).loadGAVFromJar( path );
        verify( view ).setGroupId( gav.getGroupId() );
        verify( view ).setArtifactId( gav.getArtifactId() );
        verify( view ).setVersion( gav.getVersion() );
        verify( wizardPageStatusChangeEvent ).fire( any( WizardPageStatusChangeEvent.class ) );
    }
}