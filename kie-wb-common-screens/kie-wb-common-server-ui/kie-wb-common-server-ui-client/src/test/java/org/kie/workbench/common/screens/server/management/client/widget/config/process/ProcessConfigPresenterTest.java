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

package org.kie.workbench.common.screens.server.management.client.widget.config.process;

import java.io.IOException;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.util.ClientMergeMode;
import org.kie.workbench.common.screens.server.management.client.util.ClientRuntimeStrategy;
import org.kie.workbench.common.screens.server.management.model.ProcessConfigModule;
import org.kie.workbench.common.screens.server.management.model.RuntimeStrategy;
import org.kie.workbench.common.screens.server.management.service.DeploymentDescriptorService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessConfigPresenterTest {

    @Mock
    TranslationService translationService;

    @Mock
    ProcessConfigPresenter.View view;

    ProcessConfigPresenter presenter;

    Caller<DeploymentDescriptorService> deploymentDescriptorServiceCaller;

    @Mock
    DeploymentDescriptorService deploymentDescriptorService;

    @Before
    public void setup() {
        when( translationService.format( any( String.class ) ) ).thenAnswer( new Answer<String>() {
            @Override
            public String answer( InvocationOnMock invocation ) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[ 0 ];
            }
        } );

        when( view.getTranslationService() ).thenReturn( translationService );

        deploymentDescriptorServiceCaller = new CallerMock<DeploymentDescriptorService>(deploymentDescriptorService);

        presenter = spy(new ProcessConfigPresenter(view, deploymentDescriptorServiceCaller));
    }

    @Test
    public void testInit() {
        presenter.init();

        verify( view ).init( presenter );
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify( view ).clear();
    }

    @Test
    public void testDisable() {
        presenter.disable();

        verify( view ).disable();
    }

    @Test
    public void testCancel() {
        final ProcessConfig processConfig = new ProcessConfig( ClientRuntimeStrategy.PER_CASE.toString(),
                                                               "kBase",
                                                               "kSession",
                                                               ClientMergeMode.OVERRIDE_ALL.toString() );

        presenter.setProcessConfig( processConfig );

        presenter.cancel();

        verify( view, times( 2 ) ).setContent( ClientRuntimeStrategy.PER_CASE.getValue( translationService ),
                                               processConfig.getKBase(),
                                               processConfig.getKSession(),
                                               ClientMergeMode.OVERRIDE_ALL.getValue( translationService ) );
    }

    @Test
    public void testSetup() {
        final ContainerSpecKey containerSpecKey = new ContainerSpecKey();
        final ProcessConfig processConfig = new ProcessConfig( ClientRuntimeStrategy.PER_CASE.toString(), "kBase", "kSession", ClientMergeMode.KEEP_ALL.toString() );
        presenter.setup( containerSpecKey, processConfig );

        verify( view ).setContent( ClientRuntimeStrategy.PER_CASE.getValue( translationService ),
                                   processConfig.getKBase(),
                                   processConfig.getKSession(),
                                   ClientMergeMode.convert( processConfig.getMergeMode(), translationService ).getValue( translationService ) );

        assertEquals( containerSpecKey, presenter.getContainerSpecKey() );
        assertEquals( processConfig, presenter.getProcessConfig() );

        presenter.clear();

        assertNull( presenter.getContainerSpecKey() );
        assertNull( presenter.getProcessConfig() );
    }

    @Test
    public void testBuildProcessConfig() {
        when( view.getRuntimeStrategy() ).thenReturn( "a" );
        when( view.getKBase() ).thenReturn( "b" );
        when( view.getKSession() ).thenReturn( "c" );
        when( view.getMergeMode() ).thenReturn( "d" );

        final ProcessConfig processConfig = presenter.buildProcessConfig();

        assertEquals( "SINGLETON", processConfig.getRuntimeStrategy() );
        assertEquals( "b", processConfig.getKBase() );
        assertEquals( "c", processConfig.getKSession() );
        assertEquals( "KEEP_ALL", processConfig.getMergeMode() );
    }

    @Test
    public void testBuildProcessConfig2() {
        when( view.getRuntimeStrategy() ).thenReturn( Constants.ClientRuntimeStrategy_PerProcessInstance );
        when( view.getKBase() ).thenReturn( "b" );
        when( view.getKSession() ).thenReturn( "c" );
        when( view.getMergeMode() ).thenReturn( Constants.ClientMergeMode_MergeCollections );

        final ProcessConfig processConfig = presenter.buildProcessConfig();

        assertEquals( "PER_PROCESS_INSTANCE", processConfig.getRuntimeStrategy() );
        assertEquals( "b", processConfig.getKBase() );
        assertEquals( "c", processConfig.getKSession() );
        assertEquals( "MERGE_COLLECTIONS", processConfig.getMergeMode() );
    }


    @Test
    public void testOnDependencyPathSelectedEvent() throws IOException {
        final String path = "org:kie:1.0";
        when(deploymentDescriptorService.getProcessConfig(path)).thenReturn(new ProcessConfigModule(RuntimeStrategy.SINGLETON,
                                                                                                    "mykiebase",
                                                                                                    "mykiesession"));

        presenter.onDependencyPathSelectedEvent(new DependencyPathSelectedEvent("", path));

        verify(view).setContent("ClientRuntimeStrategy.Singleton", "mykiebase", "mykiesession", "ClientMergeMode.MergeCollections");
    }

}
