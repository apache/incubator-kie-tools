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

package org.kie.workbench.common.stunner.bpmn.project.backend.workitem.deploy;

import java.util.Collections;

import org.guvnor.common.services.project.model.Dependencies;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionResources;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionRemoteRequest;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionService;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager.Assets;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionRemoteDeployServiceTest {

    private final static String URL = "http://url1";
    private static final WorkItemDefinition WID = new WorkItemDefinition()
            .setName("testWID")
            .setDependencies(new Dependencies(Collections.emptyList()));

    @Mock
    private BackendFileSystemManager.Asset widAsset;

    @Mock
    private BackendFileSystemManager.Asset iconAsset;

    @Mock
    private BackendFileSystemManager backendFileSystemManager;

    @Mock
    private WorkItemDefinitionService<WorkItemDefinitionRemoteRequest> remoteLookupService;

    @Mock
    private WorkItemDefinitionResources resources;

    @Mock
    private WorkItemDefinitionProjectInstaller projectInstaller;

    @Mock
    private Metadata metadata;

    @Mock
    private Path root;

    private WorkItemDefinitionRemoteDeployService tested;

    @Before
    public void init() {
        when(metadata.getRoot()).thenReturn(root);
        when(remoteLookupService.execute(any(WorkItemDefinitionRemoteRequest.class)))
                .thenReturn(Collections.singleton(WID));
        tested = new WorkItemDefinitionRemoteDeployService(remoteLookupService,
                                                           backendFileSystemManager,
                                                           resources,
                                                           projectInstaller,
                                                           (WorkItemDefinition wid) -> WID.equals(wid) ? widAsset : null,
                                                           (WorkItemDefinition wid) -> WID.equals(wid) ? iconAsset : null);
    }

    @Test
    public void testDeploy() {
        org.uberfire.java.nio.file.Path resourcePath = mock(org.uberfire.java.nio.file.Path.class);
        when(resources.resolveResourcesPath(eq(metadata))).thenReturn(resourcePath);
        tested.deploy(metadata,
                      URL);
        ArgumentCaptor<Assets> assetsArgumentCaptor =
                ArgumentCaptor.forClass(Assets.class);
        verify(backendFileSystemManager, times(1))
                .deploy(eq(resourcePath),
                        assetsArgumentCaptor.capture(),
                        anyString());
        Assets assets = assetsArgumentCaptor.getValue();
        assertNotNull(assets);
        assertEquals(2, assets.getAssets().size());
        assertTrue(assets.getAssets().contains(widAsset));
        assertTrue(assets.getAssets().contains(iconAsset));
    }
}
