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

package org.kie.workbench.common.stunner.cm.client.command.graph;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.cm.client.command.CommandTestUtils;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil.sequencePredicate;
import static org.kie.workbench.common.stunner.cm.util.CaseManagementUtils.CHILD_HEIGHT;
import static org.kie.workbench.common.stunner.cm.util.CaseManagementUtils.CHILD_WIDTH;
import static org.kie.workbench.common.stunner.cm.util.CaseManagementUtils.STAGE_GAP;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementSetChildNodeGraphCommandTest extends CaseManagementAbstractGraphCommandTest {

    private OptionalInt index;
    private Optional<Node<View<?>, Edge>> originalParent;
    private OptionalInt originalIndex;

    @Before
    public void setup() {
        super.setup();
        this.index = OptionalInt.empty();
        this.originalParent = Optional.empty();
        this.originalIndex = OptionalInt.empty();
    }

    @Test
    public void checkExecute() {
        setChildNode(parent,
                     candidate,
                     index,
                     originalParent,
                     originalIndex);

        assertEquals(1,
                     parent.getOutEdges().size());
        assertEquals(1,
                     candidate.getInEdges().size());
        assertEquals(parent.getOutEdges().get(0),
                     candidate.getInEdges().get(0));

        final Edge edge = parent.getOutEdges().get(0);
        assertEquals(parent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        assertTrue(edge.getContent() instanceof Child);
    }

    private CaseManagementSetChildNodeGraphCommand setChildNode(final Node<View<?>, Edge> parent,
                                                                final Node<View<?>, Edge> candidate,
                                                                final OptionalInt index,
                                                                final Optional<Node<View<?>, Edge>> originalParent,
                                                                final OptionalInt originalIndex) {
        final CaseManagementSetChildNodeGraphCommand command = new CaseManagementSetChildNodeGraphCommand(parent,
                                                                                                          candidate,
                                                                                                          index,
                                                                                                          originalParent,
                                                                                                          originalIndex);
        command.execute(context);
        return command;
    }

    @Test
    public void checkUndo() {
        //Setup the relationship to undo
        final CaseManagementSetChildNodeGraphCommand command = setChildNode(parent,
                                                                            candidate,
                                                                            index,
                                                                            originalParent,
                                                                            originalIndex);

        //Perform test
        command.undo(context);

        assertEquals(0,
                     parent.getOutEdges().size());
        assertEquals(0,
                     candidate.getInEdges().size());
    }

    @Test
    public void checkExecuteWhenChildHasExistingParent() {
        //Setup an existing relationship
        setChildNode(parent,
                     candidate,
                     index,
                     originalParent,
                     originalIndex);

        //Perform test
        final Node<View<?>, Edge> newParent = CommandTestUtils.makeNode("uuid3",
                                                                        "existingParent",
                                                                        10.0,
                                                                        20.0,
                                                                        50.0,
                                                                        50.0);
        setChildNode(newParent,
                     candidate,
                     index,
                     Optional.of(parent),
                     OptionalInt.of(0));

        assertEquals(0,
                     parent.getOutEdges().size());
        assertEquals(1,
                     newParent.getOutEdges().size());
        assertEquals(1,
                     candidate.getInEdges().size());
        assertEquals(newParent.getOutEdges().get(0),
                     candidate.getInEdges().get(0));

        final Edge edge = newParent.getOutEdges().get(0);
        assertEquals(newParent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        assertTrue(edge.getContent() instanceof Child);
    }

    @Test
    public void checkUndoWhenChildHasExistingParent() {
        //Setup an existing relationship
        setChildNode(parent,
                     candidate,
                     index,
                     originalParent,
                     originalIndex);

        final Node<View<?>, Edge> newParent = CommandTestUtils.makeNode("uuid3",
                                                                        "existingParent",
                                                                        10.0,
                                                                        20.0,
                                                                        50.0,
                                                                        50.0);
        final CaseManagementSetChildNodeGraphCommand addToNewParentCommand = setChildNode(newParent,
                                                                                          candidate,
                                                                                          index,
                                                                                          Optional.of(parent),
                                                                                          OptionalInt.of(0));

        //Perform test
        addToNewParentCommand.undo(context);

        assertEquals(0,
                     newParent.getOutEdges().size());
        assertEquals(1,
                     parent.getOutEdges().size());
        assertEquals(1,
                     candidate.getInEdges().size());
        assertEquals(parent.getOutEdges().get(0),
                     candidate.getInEdges().get(0));

        final Edge edge = parent.getOutEdges().get(0);
        assertEquals(parent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        assertTrue(edge.getContent() instanceof Child);
    }

    @Test
    public void testRemoveExistingRelationship() {
        final Node diagram = createNode(CaseManagementDiagram.class);
        final Node stage = createNode(AdHocSubprocess.class);
        final Node start = createNode(StartNoneEvent.class);
        final Node end = createNode(EndNoneEvent.class);

        createChildEdge(diagram, start);
        createChildEdge(diagram, stage);
        createChildEdge(diagram, end);

        createSequenceFlow(start, stage);
        createSequenceFlow(stage, end);

        final CaseManagementSetChildNodeGraphCommand command = new CaseManagementSetChildNodeGraphCommand(diagram,
                                                                                                          stage,
                                                                                                          index,
                                                                                                          Optional.of(diagram),
                                                                                                          originalIndex);

        command.removeExistingRelationship(context);

        assertTrue(command.originalIn.isPresent());
        assertEquals(start, command.originalIn.get());
        assertTrue(command.originalOut.isPresent());
        assertEquals(end, command.originalOut.get());

        assertEquals(2, diagram.getOutEdges().size());
        assertEquals(1, start.getOutEdges().size());
        assertEquals(2, end.getInEdges().size());
        assertEquals(end, ((Edge) start.getOutEdges().get(0)).getTargetNode());
    }

    @Test
    public void testAddNewRelationship_withIndex() {
        final Node diagram = createNode(CaseManagementDiagram.class);
        final Node start = createNode(StartNoneEvent.class);
        final Node end = createNode(EndNoneEvent.class);
        final Node stage = createNode(AdHocSubprocess.class);
        final Node candidate = createNode(AdHocSubprocess.class);

        createChildEdge(diagram, start);
        createChildEdge(diagram, stage);
        createChildEdge(diagram, end);

        final Edge edge = createSequenceFlow(start, stage);
        createSequenceFlow(stage, end);

        final CaseManagementSetChildNodeGraphCommand command = new CaseManagementSetChildNodeGraphCommand(diagram,
                                                                                                          candidate,
                                                                                                          OptionalInt.of(1),
                                                                                                          Optional.of(diagram),
                                                                                                          originalIndex);

        command.addNewRelationship(context);

        assertTrue(command.in.isPresent());
        assertEquals(start, command.in.get());
        assertTrue(command.out.isPresent());
        assertEquals(stage, command.out.get());
        assertTrue(command.edge.isPresent());
        assertEquals(edge, command.edge.get());

        assertEquals(candidate, ((Edge) diagram.getOutEdges().get(1)).getTargetNode());
        assertEquals(candidate, ((Edge) start.getOutEdges().get(0)).getTargetNode());
        assertEquals(1, stage.getInEdges().stream().filter(sequencePredicate()).count());
        assertEquals(candidate, ((List) stage.getInEdges().stream().filter(sequencePredicate())
                .map(e -> ((Edge) e).getSourceNode()).collect(Collectors.toList())).get(0));
    }

    @Test
    public void testAddNewRelationship_noIndex() {
        final Node diagram = createNode(CaseManagementDiagram.class);
        final Node start = createNode(StartNoneEvent.class);
        final Node end = createNode(EndNoneEvent.class);
        final Node stage = createNode(AdHocSubprocess.class);
        final Node candidate = createNode(AdHocSubprocess.class);

        createChildEdge(diagram, start);
        createChildEdge(diagram, stage);
        createChildEdge(diagram, end);

        createSequenceFlow(start, stage);
        final Edge edge = createSequenceFlow(stage, end);

        final CaseManagementSetChildNodeGraphCommand command = new CaseManagementSetChildNodeGraphCommand(diagram,
                                                                                                          candidate,
                                                                                                          OptionalInt.empty(),
                                                                                                          Optional.of(diagram),
                                                                                                          originalIndex);

        command.addNewRelationship(context);

        assertTrue(command.in.isPresent());
        assertEquals(stage, command.in.get());
        assertTrue(command.out.isPresent());
        assertEquals(end, command.out.get());
        assertTrue(command.edge.isPresent());
        assertEquals(edge, command.edge.get());

        assertEquals(candidate, ((Edge) diagram.getOutEdges().get(2)).getTargetNode());
        assertEquals(candidate, ((Edge) stage.getOutEdges().get(0)).getTargetNode());
        assertEquals(1, end.getInEdges().stream().filter(sequencePredicate()).count());
        assertEquals(candidate, ((List) end.getInEdges().stream().filter(sequencePredicate())
                .map(e -> ((Edge) e).getSourceNode()).collect(Collectors.toList())).get(0));
    }

    @Test
    public void testSequenceFlowSupplier() {
        final CaseManagementSetChildNodeGraphCommand command = new CaseManagementSetChildNodeGraphCommand(parent,
                                                                                                          candidate,
                                                                                                          index,
                                                                                                          originalParent,
                                                                                                          originalIndex);

        final Supplier<ViewConnector<SequenceFlow>> supplier = command.sequenceFlowSupplier();
        final ViewConnector<SequenceFlow> viewConnector = supplier.get();

        assertNotNull(viewConnector);
        assertNotNull(viewConnector.getSourceConnection());
        assertNotNull(viewConnector.getTargetConnection());
        assertNotNull(viewConnector.getBounds());
        assertNotNull(viewConnector.getDefinition());
    }

    @Test
    public void testResizeNodes() {
        final Node stage = createNode(AdHocSubprocess.class, 5.0, 5.0, 225.0, 225.0);
        final Node task = createNode(UserTask.class, 5.0, 5.0, 75.0, 75.0);

        final CaseManagementSetChildNodeGraphCommand command = new CaseManagementSetChildNodeGraphCommand(stage,
                                                                                                          task,
                                                                                                          index,
                                                                                                          originalParent,
                                                                                                          originalIndex);

        command.resizeNodes();

        assertEquals(Optional.of(Bounds.create(5.0, 5.0, 225.0, 225.0)), command.originalParentBounds);
        assertEquals(Optional.of(Bounds.create(5.0, 5.0, 75.0, 75.0)), command.originalBounds);
        assertEquals(Bounds.create(5.0, 5.0, 225.0, 225.0), ((View) stage.getContent()).getBounds());
        assertEquals(Bounds.create(STAGE_GAP, STAGE_GAP, STAGE_GAP + CHILD_WIDTH, STAGE_GAP + CHILD_HEIGHT), ((View) task.getContent()).getBounds());

        command.undoResizeNodes();

        assertEquals(Bounds.create(5.0, 5.0, 225.0, 225.0), ((View) stage.getContent()).getBounds());
        assertEquals(Bounds.create(5.0, 5.0, 75.0, 75.0), ((View) task.getContent()).getBounds());
    }

    private <T> Node createNode(Class<T> nClass) {
        return createNode(nClass, 1.0, 1.0, 2.0, 2.0);
    }

    private <T> Node createNode(Class<T> nClass, double x1, double y1, double x2, double y2) {
        final Node node = mock(Node.class);
        final View content = new ViewImpl(mock(nClass), Bounds.create(x1, y1, x2, y2));
        when(node.getContent()).thenReturn(content);
        when(node.getOutEdges()).thenReturn(new LinkedList());
        when(node.getInEdges()).thenReturn(new LinkedList());
        return node;
    }

    private Edge createChildEdge(Node sourceNode, Node targetNode) {
        final Edge edge = mock(Edge.class);
        sourceNode.getOutEdges().add(edge);
        targetNode.getInEdges().add(edge);
        when(edge.getContent()).thenReturn(mock(Child.class));
        when(edge.getSourceNode()).thenReturn(sourceNode);
        when(edge.getTargetNode()).thenReturn(targetNode);
        return edge;
    }

    private Edge createSequenceFlow(Node sourceNode, Node targetNode) {
        final Edge edge = mock(Edge.class);
        sourceNode.getOutEdges().add(edge);
        targetNode.getInEdges().add(edge);
        final ViewConnector viewConnector = mock(ViewConnector.class);
        when(edge.getContent()).thenReturn(viewConnector);
        when(viewConnector.getDefinition()).thenReturn(mock(SequenceFlow.class));
        when(edge.getSourceNode()).thenReturn(sourceNode);
        when(edge.getTargetNode()).thenReturn(targetNode);
        return edge;
    }
}
