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


package org.kie.workbench.common.stunner.core.graph.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.core.graph.util.NodeDefinitionHelper.getContentDefinitionId;
import static org.kie.workbench.common.stunner.core.graph.util.NodeDefinitionHelper.getDiagramId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeDefinitionHelperTest {

    @Test
    public void testGetDiagram() {

        final String diagramId = "diagramId";
        final Node node = createNode("id", diagramId);

        final String actual = getDiagramId(node);

        assertEquals(diagramId, actual);
    }

    @Test
    public void testGetContentDefinitionId() {
        final String contentDefinitionId = "contentDefinitionId";
        final Node node = createNode(contentDefinitionId, "id");

        final String actual = getContentDefinitionId(node);

        assertEquals(contentDefinitionId, actual);
    }

    private Node createNode(final String contentDefinitionId,
                            final String diagramId) {

        final Node node = mock(Node.class);
        final Definition content = mock(Definition.class);
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);
        when(hasContentDefinitionId.getContentDefinitionId()).thenReturn(contentDefinitionId);
        when(hasContentDefinitionId.getDiagramId()).thenReturn(diagramId);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(hasContentDefinitionId);

        return node;
    }
}