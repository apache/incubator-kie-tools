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

package org.kie.workbench.common.stunner.bpmn.backend.workitem;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionMetadataRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionService;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionBackendRegistryTest {

    private static final WorkItemDefinition DEF1 = new WorkItemDefinition().setName("def1");
    private static final WorkItemDefinition DEF2 = new WorkItemDefinition().setName("def2");

    @Mock
    private WorkItemDefinitionService service;

    @Mock
    private Path root;

    @Mock
    private Metadata metadata;

    private WorkItemDefinitionBackendRegistry tested;

    @Before
    public void init() {
        WorkItemDefinitionCacheRegistry registry = new WorkItemDefinitionCacheRegistry();
        WorkItemDefinitionMetadataRegistry metadataRegistry = new WorkItemDefinitionMetadataRegistry();
        when(metadata.getRoot()).thenReturn(root);
        when(service.search(eq(root))).thenReturn(Arrays.asList(DEF1, DEF2));
        this.tested = new WorkItemDefinitionBackendRegistry(registry,
                                                            service,
                                                            metadataRegistry);
        tested.init();
    }

    @Test
    public void testLoad() {
        assertTrue(tested.items().isEmpty());
        assertNull(tested.get("def1"));
        tested.load(metadata);
        assertFalse(tested.items().isEmpty());
        assertEquals(2, tested.items().size());
        assertEquals(DEF1.getName(), tested.get("def1").getName());
        assertEquals(DEF2.getName(), tested.get("def2").getName());
    }
}
