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
package org.kie.workbench.common.stunner.cm.util;

import org.junit.Test;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CaseManagementUtilsTest {

    @Test
    public void checkGetFirstDiagramNodeWithEmptyGraph() {
        final Graph graph = new GraphImpl<>("uuid",
                                            new GraphNodeStoreImpl());
        final Node<Definition<CaseManagementDiagram>, ?> fNode = CaseManagementUtils.getFirstDiagramNode(graph);
        assertNull(fNode);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkGetFirstDiagramNodeWithNonEmptyGraph() {
        final Graph graph = new GraphImpl<>("uuid",
                                            new GraphNodeStoreImpl());
        final Node node = new NodeImpl<Definition>("node-uuid");
        final CaseManagementDiagram content = new CaseManagementDiagram();
        node.setContent(new DefinitionImpl<>(content));

        graph.addNode(node);

        final Node<Definition<CaseManagementDiagram>, ?> fNode = CaseManagementUtils.getFirstDiagramNode(graph);
        assertNotNull(fNode);
        assertEquals("node-uuid",
                     fNode.getUUID());
        assertEquals(content,
                     fNode.getContent().getDefinition());
    }
}
