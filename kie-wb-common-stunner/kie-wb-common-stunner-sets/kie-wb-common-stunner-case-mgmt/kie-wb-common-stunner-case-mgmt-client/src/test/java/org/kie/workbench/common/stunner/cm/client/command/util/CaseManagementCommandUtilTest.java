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

package org.kie.workbench.common.stunner.cm.client.command.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionImpl;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementCommandUtilTest {

    @Mock
    private Node parent;

    @Mock
    private Node child;

    @Test
    public void checkGetFirstDiagramNodeWithEmptyGraph() {
        final Graph graph = new GraphImpl<>("uuid",
                                            new GraphNodeStoreImpl());
        final Node<Definition<CaseManagementDiagram>, ?> fNode = CaseManagementCommandUtil.getFirstDiagramNode(graph);
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

        final Node<Definition<CaseManagementDiagram>, ?> fNode = CaseManagementCommandUtil.getFirstDiagramNode(graph);
        assertNotNull(fNode);
        assertEquals("node-uuid",
                     fNode.getUUID());
        assertEquals(content,
                     fNode.getContent().getDefinition());
    }

    @Test
    public void testGetCanvasChildIndex() {
        final Edge edge = mock(Edge.class);
        when(edge.getTargetNode()).thenReturn(child);
        when(parent.getOutEdges()).thenReturn(Collections.singletonList(edge));

        assertEquals(0, CaseManagementCommandUtil.getChildCanvasIndex(parent, child));
    }

    @Test
    public void testGetCanvasChildIndex_withStage() {
        setupForStage();

        assertEquals(1, CaseManagementCommandUtil.getChildCanvasIndex(parent, child));
    }

    @Test
    public void testGetCanvasChildIndex_withSubStage() {
        setupForSubStage();

        assertEquals(1, CaseManagementCommandUtil.getChildCanvasIndex(parent, child));
    }

    @Test
    public void testGetGraphIndex() {
        final Edge edge = mock(Edge.class);
        when(edge.getTargetNode()).thenReturn(child);
        when(parent.getOutEdges()).thenReturn(Collections.singletonList(edge));

        assertEquals(0, CaseManagementCommandUtil.getChildGraphIndex(parent, child));
    }

    @Test
    public void testIsStage_withStage() {
        setupForStage();

        assertTrue(CaseManagementCommandUtil.isStage(parent, child));
    }

    @Test
    public void testIsStage_withSubStage() {
        setupForSubStage();

        assertFalse(CaseManagementCommandUtil.isStage(parent, child));
    }

    @Test
    public void testIsStageNode() {
        setupForStage();

        assertTrue(CaseManagementCommandUtil.isStageNode(child));
    }

    @Test
    public void testIsSubStageNode() {
        setupForSubStage();

        assertTrue(CaseManagementCommandUtil.isSubStageNode(child));
    }

    @Test
    public void testChildPredicate() {
        final Predicate<Edge> predicate = CaseManagementCommandUtil.childPredicate();

        final Edge edge = mock(Edge.class);
        when(edge.getContent()).thenReturn(mock(Child.class));

        assertTrue(predicate.test(edge));
    }

    @Test
    public void testSequencePredicate() {
        final Predicate<Edge> predicate = CaseManagementCommandUtil.sequencePredicate();

        final Edge edge = mock(Edge.class);
        final ViewConnector viewConnector = mock(ViewConnector.class);
        when(viewConnector.getDefinition()).thenReturn(mock(SequenceFlow.class));
        when(edge.getContent()).thenReturn(viewConnector);

        assertTrue(predicate.test(edge));
    }

    @Test
    public void testGetCanvasNewChildIndex_withStage() {
        setupForStage();

        assertEquals(2, CaseManagementCommandUtil.getNewChildCanvasIndex(parent));
    }

    @Test
    public void testGetCanvasNewChildIndex_withSubStage() {
        setupForSubStage();

        assertEquals(2, CaseManagementCommandUtil.getNewChildCanvasIndex(parent));
    }

    @Test
    public void testGetGraphNewChildIndex_withStage() {
        setupForStage();

        assertEquals(2, CaseManagementCommandUtil.getNewChildGraphIndex(parent));
    }

    @Test
    public void testGetGraphNewChildIndex_withSubStage() {
        setupForSubStage();

        assertEquals(2, CaseManagementCommandUtil.getNewChildGraphIndex(parent));
    }

    private void setupForStage() {
        Node c = mock(Node.class);
        View cv = mock(View.class);
        when(c.getContent()).thenReturn(cv);
        when(cv.getDefinition()).thenReturn(mock(AdHocSubprocess.class));

        final Edge ce = mock(Edge.class);
        when(ce.getTargetNode()).thenReturn(c);
        when(ce.getContent()).thenReturn(mock(Child.class));

        final Edge edge = mock(Edge.class);
        when(edge.getTargetNode()).thenReturn(child);
        when(edge.getContent()).thenReturn(mock(Child.class));

        final List<Edge> edges = new ArrayList<>(2);
        edges.add(ce);
        edges.add(edge);

        when(parent.getOutEdges()).thenReturn(edges);

        View pView = mock(View.class);
        when(parent.getContent()).thenReturn(pView);
        when(pView.getDefinition()).thenReturn(mock(CaseManagementDiagram.class));

        View cView = mock(View.class);
        when(child.getContent()).thenReturn(cView);
        when(cView.getDefinition()).thenReturn(mock(AdHocSubprocess.class));
    }

    private void setupForSubStage() {
        Node c = mock(Node.class);
        View cv = mock(View.class);
        when(c.getContent()).thenReturn(cv);
        when(cv.getDefinition()).thenReturn(mock(ProcessReusableSubprocess.class));

        final Edge ce = mock(Edge.class);
        when(ce.getTargetNode()).thenReturn(c);
        when(ce.getContent()).thenReturn(mock(Child.class));

        final Edge edge = mock(Edge.class);
        when(edge.getTargetNode()).thenReturn(child);
        when(edge.getContent()).thenReturn(mock(Child.class));

        final List<Edge> edges = new ArrayList<>(2);
        edges.add(ce);
        edges.add(edge);

        when(parent.getOutEdges()).thenReturn(edges);

        View pView = mock(View.class);
        when(parent.getContent()).thenReturn(pView);
        when(pView.getDefinition()).thenReturn(mock(AdHocSubprocess.class));

        View cView = mock(View.class);
        when(child.getContent()).thenReturn(cView);
        when(cView.getDefinition()).thenReturn(mock(UserTask.class));
    }
}