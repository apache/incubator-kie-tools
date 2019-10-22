/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.backend.workitem.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiPredicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.deploy.WorkItemDefinitionDeployServices;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionVFSLookupService;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionProjectServiceTest {

    private static WorkItemDefinition wid1 = new WorkItemDefinition().setName("wid1");
    private static WorkItemDefinition wid2 = new WorkItemDefinition().setName("wid2");

    @Mock
    private WorkItemDefinitionVFSLookupService vfsService;

    @Mock
    private WorkItemDefinitionDeployServices deployServices;

    @Mock
    private BiPredicate<Metadata, Collection<WorkItemDefinition>> deployPredicate;

    @Mock
    private Metadata metadata;

    private WorkItemDefinitionProjectService tested;
    private WorkItemDefinitionCacheRegistry registry;

    @Before
    public void init() {
        when(vfsService.search(eq(metadata))).thenReturn(Arrays.asList(wid1,
                                                                       wid2));
        registry = new WorkItemDefinitionCacheRegistry();
        tested = new WorkItemDefinitionProjectService(registry,
                                                      vfsService,
                                                      deployServices,
                                                      deployPredicate);
    }

    @Test
    public void testGetRegistry() {
        assertEquals(registry, tested.getRegistry());
    }

    @Test
    public void testExecute() {
        Collection<WorkItemDefinition> result = tested.execute(metadata);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.contains(wid1));
        assertTrue(result.contains(wid2));
    }

    @Test
    public void testDestroy() {
        tested.execute(metadata);
        assertFalse(registry.isEmpty());
        tested.destroy();
        assertTrue(registry.isEmpty());
    }
}
