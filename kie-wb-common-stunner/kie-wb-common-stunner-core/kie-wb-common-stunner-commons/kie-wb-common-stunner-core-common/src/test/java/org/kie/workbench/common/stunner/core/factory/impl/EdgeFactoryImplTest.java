/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.factory.impl;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EdgeFactoryImplTest {

    public static final String UUID = "uuid1";
    public static final String ID = "defId";
    public static final Set<String> LABELS = Arrays.asList("label1",
                                                           "label2").stream().collect(Collectors.toSet());
    public static final Bounds BOUNDS = Bounds.create(10d, 10.6d, 1034.42d, 1032.26d);

    @Mock
    Object definition;

    private EdgeFactoryImpl tested;
    private TestingGraphMockHandler testingkHelper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.testingkHelper = new TestingGraphMockHandler();
        when(testingkHelper.definitionAdapter.getId(eq(definition))).thenReturn(DefinitionId.build(ID));
        when(testingkHelper.definitionAdapter.getLabels(eq(definition))).thenReturn(LABELS);
        this.tested = new EdgeFactoryImpl(testingkHelper.definitionManager);
    }

    @Test
    public void testType() {
        assertEquals(EdgeFactory.class,
                     this.tested.getFactoryType());
    }

    @Test
    public void testBuild() {
        final Edge<Definition<Object>, Node> edge = tested.build(UUID,
                                                                 definition);
        assertNotNull(edge);
        assertEquals(UUID,
                     edge.getUUID());
        assertEquals(3,
                     edge.getLabels().size());
        assertTrue(edge.getLabels().contains(ID));
        assertTrue(edge.getLabels().contains("label1"));
        assertTrue(edge.getLabels().contains("label2"));
    }
}
