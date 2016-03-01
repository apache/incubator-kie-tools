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

package org.kie.workbench.common.screens.server.management.client.container.config.process;

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.widget.config.process.ProcessConfigPresenter;
import org.kie.workbench.common.screens.server.management.model.MergeMode;
import org.kie.workbench.common.screens.server.management.model.RuntimeStrategy;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerProcessConfigPresenterTest {

    @Mock
    ContainerProcessConfigPresenter.View view;

    @Mock
    ProcessConfigPresenter processConfigPresenter;

    @Mock
    ProcessConfigPresenter.View processConfigPresenterView;

    @Mock
    ContainerSpecKey containerSpecKey;

    @Mock
    ServerTemplateKey serverTemplateKey;

    Caller<SpecManagementService> specManagementServiceCaller;

    @Mock
    Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    @Mock
    SpecManagementService specManagementService;

    @Mock
    ProcessConfig processConfig;

    ContainerProcessConfigPresenter presenter;

    @Before
    public void init() {
        specManagementServiceCaller = new CallerMock<SpecManagementService>( specManagementService );
        when( containerSpecKey.getServerTemplateKey() ).thenReturn( serverTemplateKey );
        when( processConfigPresenter.getProcessConfig() ).thenReturn( processConfig );
        when( processConfigPresenter.getContainerSpecKey() ).thenReturn( containerSpecKey );
        when( processConfigPresenter.getView() ).thenReturn( processConfigPresenterView );
        presenter = new ContainerProcessConfigPresenter( view, processConfigPresenter, specManagementServiceCaller, notification );
    }

    @Test
    public void testInit() {
        presenter.init();

        verify( view ).init( presenter );
        verify( view ).setProcessConfigView( processConfigPresenterView );
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testDisable() {
        presenter.disable();

        verify( view ).disable();
    }

    @Test
    public void testCancel() {
        presenter.cancel();

        verify( view ).enableActions();
        verify( processConfigPresenter ).setProcessConfig( processConfig );
    }

    @Test
    public void testSave() {
        final String templateKey = "templateKey";
        final String containerKey = "containerKey";
        when( serverTemplateKey.getId() ).thenReturn( templateKey );
        when( containerSpecKey.getId() ).thenReturn( containerKey );
        when( view.getSaveSuccessMessage() ).thenReturn( "SUCCESS" );

        presenter.save();

        verify( notification ).fire( new NotificationEvent( "SUCCESS", NotificationEvent.NotificationType.SUCCESS ) );
        verify( view ).disableActions();
        verify( processConfigPresenter ).buildProcessConfig();

        final ArgumentCaptor<ProcessConfig> processConfigCaptor = ArgumentCaptor.forClass( ProcessConfig.class );
        verify( specManagementService ).updateContainerConfig( eq( templateKey ), eq( containerKey ), eq( Capability.PROCESS ), processConfigCaptor.capture() );

        verify( view ).enableActions();
        verify( processConfigPresenter ).setProcessConfig( processConfigCaptor.getValue() );
    }

    @Test
    public void testSaveError() {
        final String templateKey = "templateKey";
        final String containerKey = "containerKey";
        when( serverTemplateKey.getId() ).thenReturn( templateKey );
        when( containerSpecKey.getId() ).thenReturn( containerKey );
        when( view.getSaveErrorMessage() ).thenReturn( "ERROR" );
        doThrow( new RuntimeException() ).when( specManagementService ).updateContainerConfig( anyString(), anyString(), any( Capability.class ), any( ContainerConfig.class ) );

        presenter.save();

        verify( notification ).fire( new NotificationEvent( "ERROR", NotificationEvent.NotificationType.ERROR ) );
        verify( view ).disableActions();
        verify( view ).enableActions();
        verify( processConfigPresenter ).setProcessConfig( processConfig );
    }

    @Test
    public void testSetup() {
        final ContainerSpecKey containerSpecKey = new ContainerSpecKey( "id", "container-name", new ServerTemplateKey( "template-id", "template-name" ) );
        final ProcessConfig processConfig = new ProcessConfig( RuntimeStrategy.PER_REQUEST.toString(), "kbase", "ksession", MergeMode.KEEP_ALL.toString() );

        presenter.setup( containerSpecKey, processConfig );

        verify( view ).enableActions();
        verify( processConfigPresenter ).setup( containerSpecKey, processConfig );
        verify( processConfigPresenter ).setProcessConfig( processConfig );
    }

}
