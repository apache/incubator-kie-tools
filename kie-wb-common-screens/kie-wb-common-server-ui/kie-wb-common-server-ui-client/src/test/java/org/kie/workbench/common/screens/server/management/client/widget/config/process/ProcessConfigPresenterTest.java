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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.workbench.common.screens.server.management.client.util.ClientMergeMode;
import org.kie.workbench.common.screens.server.management.client.util.ClientRuntimeStrategy;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessConfigPresenterTest {

    @Mock
    ProcessConfigPresenter.View view;

    @InjectMocks
    ProcessConfigPresenter presenter;

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
        final ProcessConfig processConfig = new ProcessConfig( ClientRuntimeStrategy.SINGLETON.toString(), "kBase", "kSession", ClientMergeMode.KEEP_ALL.toString() );
        presenter.setProcessConfig( processConfig );

        presenter.cancel();

        verify( view, times( 2 ) ).setContent( ClientRuntimeStrategy.convert( processConfig.getRuntimeStrategy() ).toString(),
                                               processConfig.getKBase(),
                                               processConfig.getKSession(),
                                               ClientMergeMode.convert( processConfig.getMergeMode() ).toString() );
    }

    @Test
    public void testSetup() {
        final ContainerSpecKey containerSpecKey = new ContainerSpecKey();
        final ProcessConfig processConfig = new ProcessConfig( ClientRuntimeStrategy.SINGLETON.toString(), "kBase", "kSession", ClientMergeMode.KEEP_ALL.toString() );
        presenter.setup( containerSpecKey, processConfig );

        verify( view ).setContent( ClientRuntimeStrategy.convert( processConfig.getRuntimeStrategy() ).toString(),
                                   processConfig.getKBase(),
                                   processConfig.getKSession(),
                                   ClientMergeMode.convert( processConfig.getMergeMode() ).toString() );

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

        assertEquals( "a", processConfig.getRuntimeStrategy() );
        assertEquals( "b", processConfig.getKBase() );
        assertEquals( "c", processConfig.getKSession() );
        assertEquals( "d", processConfig.getMergeMode() );
    }

}