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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateElementPositionCommandTest extends AbstractGraphCommandTest {

    private static final String UUID = "testUUID";
    private static final String UUID2 = "testUUID2";
    private static final Point2D PREVIOUS_LOCATION = new Point2D(100d, 100d);
    private static final Point2D LOCATION = new Point2D(200d, 200d);
    private static final Double W = 50d;
    private static final Double H = 50d;

    @Mock
    private Node candidate;

    @Mock
    private Node parent;

    private View content;

    private UpdateElementPositionCommand tested;

    @Mock
    private Node dockedNode;

    @Mock
    private Edge dockEdge;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        content = mockView(PREVIOUS_LOCATION.getX(),
                           PREVIOUS_LOCATION.getY(),
                           W,
                           H);
        when(candidate.getUUID()).thenReturn(UUID);
        when(candidate.getContent()).thenReturn(content);
        when(graphIndex.getNode(eq(UUID))).thenReturn(candidate);

        EdgeImpl<Object> childEdge = new EdgeImpl<>("childEdge");
        childEdge.setContent(new Child());
        childEdge.setSourceNode(parent);
        childEdge.setTargetNode(candidate);
        when(candidate.getInEdges()).thenReturn(Collections.singletonList(childEdge));

        Node rootNode = mock(Node.class);
        EdgeImpl<Object> parentEdge = new EdgeImpl<>("parentEdge");
        parentEdge.setContent(new Child());
        parentEdge.setSourceNode(rootNode);
        parentEdge.setTargetNode(parent);
        when(parent.getInEdges()).thenReturn(Collections.singletonList(parentEdge));

        when(parent.getContent()).thenReturn(new ViewImpl<>(mock(Object.class),
                                                            Bounds.create(0d, 0d, 600d, 600d)));
        when(rootNode.getContent()).thenReturn(new ViewImpl<>(mock(Object.class),
                                                              Bounds.create(0d, 0d, 1200d, 1200d)));

        this.tested = new UpdateElementPositionCommand(candidate,
                                                       LOCATION);

        //docked node mock
        when(dockedNode.getUUID()).thenReturn(UUID2);
        when(dockedNode.getContent()).thenReturn(content);
        when(graphIndex.getNode(eq(UUID2))).thenReturn(dockedNode);
        when(dockedNode.getInEdges()).thenReturn(Collections.singletonList(dockEdge));
        when(dockEdge.getContent()).thenReturn(new Dock());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class));
    }

    @Test(expected = BadCommandArgumentsException.class)
    public void testAllowNodeNotFound() {
        this.tested = new UpdateElementPositionCommand(UUID,
                                                       LOCATION,
                                                       PREVIOUS_LOCATION);
        when(graphIndex.getNode(eq(UUID))).thenReturn(null);
        tested.allow(graphCommandExecutionContext);
    }

    @Test
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        ArgumentCaptor<Bounds> bounds = ArgumentCaptor.forClass(Bounds.class);
        verify(content, times(1)).setBounds(bounds.capture());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        Bounds b = bounds.getValue();
        assertEquals(UUID,
                     tested.getUuid());
        assertEquals(LOCATION,
                     tested.getLocation());
        assertEquals(PREVIOUS_LOCATION,
                     tested.getPreviousLocation());
        assertEquals(PREVIOUS_LOCATION.getY(),
                     tested.getPreviousLocation().getY()
                , 0d);
        assertEquals(Double.valueOf(LOCATION.getX() + W),
                     b.getLowerRight().getX());
        assertEquals(Double.valueOf(LOCATION.getY() + H),
                     b.getLowerRight().getY());
    }

    @Test(expected = BadCommandArgumentsException.class)
    public void testExecuteNodeNotFound() {
        this.tested = new UpdateElementPositionCommand(UUID,
                                                       LOCATION,
                                                       PREVIOUS_LOCATION);
        when(graphIndex.getNode(eq(UUID))).thenReturn(null);
        tested.execute(graphCommandExecutionContext);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteDockedNode() {
        ArgumentCaptor<Bounds> boundsArgumentCaptor = ArgumentCaptor.forClass(Bounds.class);
        this.tested = new UpdateElementPositionCommand(dockedNode, new Point2D(600d, 600d));
        final CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        verify((View) dockedNode.getContent(), times(1)).setBounds(boundsArgumentCaptor.capture());
        assertEquals(CommandResult.Type.INFO, result.getType());
        Bounds bounds = boundsArgumentCaptor.getValue();
        assertEquals(bounds.getUpperLeft(), Bound.create(600d, 600d));
        assertEquals(bounds.getLowerRight(), Bound.create(650d, 650d));
    }
}
