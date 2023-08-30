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
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GraphFactoryImplTest {

    public static final String UUID = "uuid1";
    public static final String ID = "defId";

    @Mock
    DefinitionManager definitionManager;

    private GraphFactoryImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.tested = new GraphFactoryImpl(definitionManager);
    }

    @Test
    public void testType() {
        assertEquals(GraphFactory.class,
                     this.tested.getFactoryType());
    }

    @Test
    public void testBuild() {
        final Graph<DefinitionSet, Node> graph = tested.build(UUID,
                                                              ID);
        assertNotNull(graph);
        assertEquals(UUID,
                     graph.getUUID());
        assertEquals(1,
                     graph.getLabels().size());
        assertTrue(graph.getLabels().contains(ID));
    }
}
