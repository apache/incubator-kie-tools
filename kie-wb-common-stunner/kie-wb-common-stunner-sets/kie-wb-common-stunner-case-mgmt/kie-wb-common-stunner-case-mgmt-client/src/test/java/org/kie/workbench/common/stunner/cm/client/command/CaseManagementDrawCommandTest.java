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

package org.kie.workbench.common.stunner.cm.client.command;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasChildNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementDrawCommandTest {

    private static final String SHAPE_SET_ID = "mockShapeSet";

    private CaseManagementDrawCommand tested;

    private TestingGraphInstanceBuilder.TestGraph4 graphHolder;

    @Mock
    private Index graphIndex;

    @Mock
    private AbstractCanvasHandler context;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private ConnectorShape connectorShape;

    @Mock
    private ShapeView shapeView;

    @Mock
    private Shape shape;

    @Captor
    private ArgumentCaptor<CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>> commandsCapture;

    @Before
    public void setUp() {
        this.graphHolder = TestingGraphInstanceBuilder.newGraph4(new TestingGraphMockHandler());

        when(context.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.getGraph()).thenReturn(graphHolder.graph);
        when((diagram.getGraph())).thenReturn(graphHolder.graph);
        when(context.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(context.getAbstractCanvas()).thenReturn(canvas);
        when(context.getCanvas()).thenReturn(canvas);
        StreamSupport
                .stream(graphHolder.graph.nodes().spliterator(), false)
                .forEach(node -> when(canvas.getShape(((Node) node).getUUID())).thenReturn(shape));
        when(canvas.getShape(graphHolder.edge1.getUUID())).thenReturn(connectorShape);
        when(canvas.getShape(graphHolder.edge2.getUUID())).thenReturn(connectorShape);
        when(connectorShape.getShapeView()).thenReturn(shapeView);
        when(shape.getShapeView()).thenReturn(shapeView);

        tested = spy(new CaseManagementDrawCommand(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl())));
    }

    @Test
    public void testExecute() {
        tested.execute(context);
        verify(tested).executeCommands(eq(context), commandsCapture.capture());

        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commands = commandsCapture.getValue();
        assertEquals(5, commands.size());

        Map<Class<?>, Long> commandsMap = IntStream.range(0, commands.size())
                .mapToObj(commands::get).collect(Collectors.groupingBy(Object::getClass, Collectors.counting()));

        assertEquals(2, commandsMap.size());
        assertEquals(1, commandsMap.get(AddCanvasNodeCommand.class).longValue());
        assertEquals(4, commandsMap.get(AddCanvasChildNodeCommand.class).longValue());
    }

    @Test
    public void testIsDrawable_Stage() {
        testIsDrawable(CaseManagementDiagram.class, AdHocSubprocess.class, true);
    }

    @Test
    public void testIsDrawable_UserTask() {
        testIsDrawable(AdHocSubprocess.class, UserTask.class, true);
    }

    @Test
    public void testIsDrawable_ReusableSubprocess() {
        testIsDrawable(AdHocSubprocess.class, ReusableSubprocess.class, true);
    }

    @Test
    public void testIsDrawable_InvalidUserTask() {
        testIsDrawable(CaseManagementDiagram.class, UserTask.class, false);
    }

    @Test
    public void testIsDrawable_InvalidReusableSubprocess() {
        testIsDrawable(CaseManagementDiagram.class, ReusableSubprocess.class, false);
    }

    private <P, C> void testIsDrawable(Class<P> pClass, Class<C> cClass, boolean expectedResult) {
        final Node parent = mock(Node.class);
        final View pContent = mock(View.class);
        when(parent.getContent()).thenReturn(pContent);
        final P pDefinition = mock(pClass);
        when(pContent.getDefinition()).thenReturn(pDefinition);

        final Node child = mock(Node.class);
        final View cContent = mock(View.class);
        when(child.getContent()).thenReturn(cContent);
        final C cDefinition = mock(cClass);
        when(cContent.getDefinition()).thenReturn(cDefinition);

        assertEquals(expectedResult, tested.isDrawable(parent, child));
    }

    @Test
    public void testSortNodes_Stage() {
        final Diagram<Graph<DefinitionSet, Node>, Metadata> diagram = mock(Diagram.class);
        final Graph<DefinitionSet, Node> graph = mock(Graph.class);
        when(diagram.getGraph()).thenReturn(graph);
        final List<Node> nodes = new LinkedList<>();
        when(graph.nodes()).thenReturn(nodes);

        final Node root = createNode(CaseManagementDiagram.class);
        final Node start = createNode(StartNoneEvent.class);
        final Node end = createNode(EndNoneEvent.class);
        final Node stage1 = createNode(AdHocSubprocess.class);
        final Node stage2 = createNode(AdHocSubprocess.class);

        nodes.add(root);
        nodes.add(start);
        nodes.add(end);
        nodes.add(stage1);
        nodes.add(stage2);

        createChildEdge(root, end);
        createChildEdge(root, start);
        createChildEdge(root, stage2);
        createChildEdge(root, stage1);
        createSequenceFlow(start, stage1);
        createSequenceFlow(stage1, stage2);
        createSequenceFlow(stage2, end);

        tested.sortNodes(diagram);

        final List<Edge> rEdges = root.getOutEdges();
        assertEquals(4, rEdges.size());
        assertEquals(start, rEdges.get(0).getTargetNode());
        assertEquals(stage1, rEdges.get(1).getTargetNode());
        assertEquals(stage2, rEdges.get(2).getTargetNode());
        assertEquals(end, rEdges.get(3).getTargetNode());
    }

    @Test
    public void testSortNodes_SubStage() {
        final Diagram<Graph<DefinitionSet, Node>, Metadata> diagram = mock(Diagram.class);
        final Graph<DefinitionSet, Node> graph = mock(Graph.class);
        when(diagram.getGraph()).thenReturn(graph);
        final List<Node> nodes = new LinkedList<>();
        when(graph.nodes()).thenReturn(nodes);

        final Node root = createNode(CaseManagementDiagram.class);
        final Node start = createNode(StartNoneEvent.class);
        final Node end = createNode(EndNoneEvent.class);
        final Node stage = createNode(AdHocSubprocess.class);

        nodes.add(root);
        nodes.add(start);
        nodes.add(end);
        nodes.add(stage);

        createChildEdge(root, end);
        createChildEdge(root, start);
        createChildEdge(root, stage);
        createSequenceFlow(start, stage);
        createSequenceFlow(stage, end);

        final Node ss1 = createNode(UserTask.class, 3.0);
        final Node ss2 = createNode(CaseReusableSubprocess.class, 2.0);
        final Node ss3 = createNode(ProcessReusableSubprocess.class, 1.0);

        nodes.add(ss1);
        nodes.add(ss2);
        nodes.add(ss3);

        createChildEdge(stage, ss1);
        createChildEdge(stage, ss2);
        createChildEdge(stage, ss3);

        tested.sortNodes(diagram);

        final List<Edge> rEdges = root.getOutEdges();
        assertEquals(3, rEdges.size());
        assertEquals(start, rEdges.get(0).getTargetNode());
        assertEquals(stage, rEdges.get(1).getTargetNode());
        assertEquals(end, rEdges.get(2).getTargetNode());

        final List<Edge> sEdges = stage.getOutEdges();
        assertEquals(4, sEdges.size());
        assertEquals(ss3, sEdges.get(0).getTargetNode());
        assertEquals(ss2, sEdges.get(1).getTargetNode());
        assertEquals(ss1, sEdges.get(2).getTargetNode());
    }

    private <T> Node createNode(Class<T> nClass, double... y) {
        final Node node = mock(Node.class);
        final List<Node> outEdges = new LinkedList<>();
        when(node.getOutEdges()).thenReturn(outEdges);
        final List<Node> inEdges = new LinkedList<>();
        when(node.getInEdges()).thenReturn(inEdges);
        final View content = mock(View.class);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(mock(nClass));
        final Bounds bounds = mock(Bounds.class);
        when(content.getBounds()).thenReturn(bounds);
        if (y.length > 0) {
            when(bounds.getY()).thenReturn(y[0]);
        }
        return node;
    }

    private Edge createChildEdge(Node sourceNode, Node targetNode) {
        final Edge edge = mock(Edge.class);
        when(edge.getContent()).thenReturn(mock(Child.class));
        when(edge.getSourceNode()).thenReturn(sourceNode);
        when(edge.getTargetNode()).thenReturn(targetNode);
        sourceNode.getOutEdges().add(edge);
        targetNode.getInEdges().add(edge);
        return edge;
    }

    private Edge createSequenceFlow(Node sourceNode, Node targetNode) {
        final Edge edge = mock(Edge.class);
        final ViewConnector viewConnector = mock(ViewConnector.class);
        when(edge.getContent()).thenReturn(viewConnector);
        when(viewConnector.getDefinition()).thenReturn(mock(SequenceFlow.class));
        when(edge.getSourceNode()).thenReturn(sourceNode);
        when(edge.getTargetNode()).thenReturn(targetNode);
        sourceNode.getOutEdges().add(edge);
        targetNode.getInEdges().add(edge);
        return edge;
    }
}