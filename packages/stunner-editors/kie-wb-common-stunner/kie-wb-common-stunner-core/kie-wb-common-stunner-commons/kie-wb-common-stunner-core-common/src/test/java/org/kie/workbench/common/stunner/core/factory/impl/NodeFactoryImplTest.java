/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.factory.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NodeFactoryImplTest {

    public static final String UUID = "uuid1";
    public static final String DEF_TYPE_ID = "defType";
    public static final String DEF_ID = "defId";
    public static final String[] LABELS = new String[]{"label1", "label2"};
    public static final Bounds BOUNDS = Bounds.create(10d, 10.6d, 1034.42d, 1032.26d);

    @Mock
    DefinitionUtils definitionUtils;

    @Mock
    Object definition;

    private NodeFactoryImpl tested;
    private TestingGraphMockHandler testingkHelper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.testingkHelper = new TestingGraphMockHandler();
        when(testingkHelper.getDefinitionAdapter().getLabels(eq(definition))).thenReturn(LABELS);
        when(definitionUtils.getDefinitionManager()).thenReturn(testingkHelper.getDefinitionManager());
        when(definitionUtils.buildBounds(eq(definition),
                                         anyDouble(),
                                         anyDouble())).thenReturn(BOUNDS);
        this.tested = new NodeFactoryImpl(definitionUtils);
    }

    @Test
    public void testType() {
        assertEquals(NodeFactory.class,
                     this.tested.getFactoryType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild() {
        when(testingkHelper.getDefinitionAdapter().getId(eq(definition))).thenReturn(DefinitionId.build(DEF_ID));
        final Node<Definition<Object>, Edge> node = tested.build(UUID,
                                                                 definition);
        assertNotNull(node);
        assertEquals(UUID,
                     node.getUUID());
        assertEquals(3,
                     node.getLabels().size());
        assertTrue(node.getLabels().contains(DEF_ID));
        assertTrue(node.getLabels().contains("label1"));
        assertTrue(node.getLabels().contains("label2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildDynamicDefinition() {
        when(testingkHelper.getDefinitionAdapter().getId(eq(definition))).thenReturn(DefinitionId.build(DEF_TYPE_ID,
                                                                                                        DEF_ID));
        final Node<Definition<Object>, Edge> node = tested.build(UUID,
                                                                 definition);
        assertNotNull(node);
        assertEquals(UUID,
                     node.getUUID());
        assertEquals(4,
                     node.getLabels().size());
        assertTrue(node.getLabels().contains(DefinitionId.generateId(DEF_TYPE_ID, DEF_ID)));
        assertTrue(node.getLabels().contains(DEF_TYPE_ID));
        assertTrue(node.getLabels().contains("label1"));
        assertTrue(node.getLabels().contains("label2"));
    }
}
