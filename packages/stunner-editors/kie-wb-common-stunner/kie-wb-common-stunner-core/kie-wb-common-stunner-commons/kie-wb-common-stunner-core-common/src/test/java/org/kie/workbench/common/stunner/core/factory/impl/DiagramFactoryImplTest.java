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
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class DiagramFactoryImplTest {

    public static final String NAME = "name1";

    @Mock
    Metadata metadata;

    @Mock
    Graph<DefinitionSet, ?> graph;

    private DiagramFactoryImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.tested = new DiagramFactoryImpl();
    }

    @Test
    public void testType() {
        assertEquals(Metadata.class,
                     this.tested.getMetadataType());
    }

    @Test
    public void testBuild() {
        final Diagram<Graph, Metadata> diagram = tested.build(NAME,
                                                              metadata,
                                                              graph);
        assertNotNull(diagram);
        assertEquals(NAME,
                     diagram.getName());
        assertEquals(metadata,
                     diagram.getMetadata());
        assertEquals(graph,
                     diagram.getGraph());
    }
}
