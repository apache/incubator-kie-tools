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

package org.kie.workbench.common.stunner.bpmn.backend.workitem.deploy;

import java.util.Collection;
import java.util.function.BiFunction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionResources;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager.Asset;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager.Assets;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionDefaultDeployServiceTest {

    @Mock
    private WorkItemDefinitionResources resources;

    @Mock
    private BackendFileSystemManager backendFileSystemManager;

    @Mock
    private Metadata metadata;

    @Mock
    private Path globalPath;

    @Mock
    private BiFunction<String, String, Asset> assetBuilder;

    @Mock
    private Asset widAsset;

    @Mock
    private Asset brIcon;

    @Mock
    private Asset decisionIcon;

    @Mock
    private Asset logIcon;

    @Mock
    private Asset emailIcon;

    @Mock
    private Asset serviceNodeIcon;

    @Mock
    private Asset milestoneNodeIcon;

    private WorkItemDefinitionDefaultDeployService tested;

    @Before
    public void init() {
        when(assetBuilder.apply(eq(WorkItemDefinitionDefaultDeployService.WID_FILE),
                                eq(getClassPath(WorkItemDefinitionDefaultDeployService.WID_FILE))))
                .thenReturn(widAsset);
        when(assetBuilder.apply(eq(WorkItemDefinitionDefaultDeployService.EMAIL_ICON),
                                eq(getClassPath(WorkItemDefinitionDefaultDeployService.EMAIL_ICON))))
                .thenReturn(emailIcon);
        when(assetBuilder.apply(eq(WorkItemDefinitionDefaultDeployService.BR_ICON),
                                eq(getClassPath(WorkItemDefinitionDefaultDeployService.BR_ICON))))
                .thenReturn(brIcon);
        when(assetBuilder.apply(eq(WorkItemDefinitionDefaultDeployService.DECISION_ICON),
                                eq(getClassPath(WorkItemDefinitionDefaultDeployService.DECISION_ICON))))
                .thenReturn(decisionIcon);
        when(assetBuilder.apply(eq(WorkItemDefinitionDefaultDeployService.LOG_ICON),
                                eq(getClassPath(WorkItemDefinitionDefaultDeployService.LOG_ICON))))
                .thenReturn(logIcon);
        when(assetBuilder.apply(eq(WorkItemDefinitionDefaultDeployService.SERVICE_NODE_ICON),
                                eq(getClassPath(WorkItemDefinitionDefaultDeployService.SERVICE_NODE_ICON))))
                .thenReturn(serviceNodeIcon);
        when(assetBuilder.apply(eq(WorkItemDefinitionDefaultDeployService.MILESTONE_ICON),
                                eq(getClassPath(WorkItemDefinitionDefaultDeployService.MILESTONE_ICON))))
                .thenReturn(milestoneNodeIcon);

        when(resources.resolveGlobalPath(eq(metadata))).thenReturn(globalPath);
        tested = new WorkItemDefinitionDefaultDeployService(resources,
                                                            backendFileSystemManager, assetBuilder);
    }

    @Test
    public void testDeployAssets() {
        ArgumentCaptor<Assets> assetsArgumentCaptor = ArgumentCaptor.forClass(Assets.class);
        tested.deploy(metadata);
        verify(backendFileSystemManager, times(1))
                .deploy(eq(globalPath),
                        assetsArgumentCaptor.capture(),
                        anyString());
        Collection<Asset> assets = assetsArgumentCaptor.getValue().getAssets();
        assertEquals(WorkItemDefinitionDefaultDeployService.ASSETS.length, assets.size());
        assertTrue(assets.contains(widAsset));
        assertTrue(assets.contains(emailIcon));
        assertTrue(assets.contains(brIcon));
        assertTrue(assets.contains(decisionIcon));
        assertTrue(assets.contains(logIcon));
        assertTrue(assets.contains(serviceNodeIcon));
        assertTrue(assets.contains(milestoneNodeIcon));
    }

    private static String getClassPath(final String asset) {
        return WorkItemDefinitionDefaultDeployService.ASSETS_ROOT + asset;
    }
}
