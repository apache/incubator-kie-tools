/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.Optional;
import java.util.OptionalInt;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementSetChildNodeGraphCommand;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CaseManagementSetChildCommandTest extends CaseManagementAbstractCommandTest {

    private Node<View<?>, Edge> parent;

    private Node<View<?>, Edge> candidate;

    private CaseManagementSetChildCommand command;

    private int index;

    @Before
    public void setUp() throws Exception {
        super.setup();

        this.parent = CommandTestUtils.makeNode("uuid1",
                                                "parent",
                                                10.0,
                                                20.0,
                                                50.0,
                                                50.0);

        this.candidate = CommandTestUtils.makeNode("uuid2",
                                                   "candidate",
                                                   10.0,
                                                   20.0,
                                                   50.0,
                                                   50.0);

        this.command = new CaseManagementSetChildCommand(this.parent,
                                                         this.candidate);

        this.index = parent.getOutEdges().size();
    }

    @Test
    public void testNewGraphCommand() throws Exception {
        assertCommandSuccess(command.execute(canvasHandler));

        assertEquals(1,
                     parent.getOutEdges().size());
        assertEquals(1,
                     candidate.getInEdges().size());
        assertEquals(parent.getOutEdges().get(index),
                     candidate.getInEdges().get(0));

        final Edge edge = parent.getOutEdges().get(index);
        assertEquals(parent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        assertTrue(edge.getContent() instanceof Child);
    }

    @Test
    public void testNewCanvasCommand() throws Exception {
        assertCommandSuccess(command.execute(canvasHandler));

        verify(canvasHandler).addChild(eq(parent),
                                       eq(candidate),
                                       eq(index));
    }

    @Test
    public void testNewGraphCommand_moveForward() throws Exception {
        final Node last = mock(Node.class);
        final Edge lEdge = mock(Edge.class);
        when(lEdge.getTargetNode()).thenReturn(last);
        parent.getOutEdges().add(lEdge);

        final Edge cEdge = mock(Edge.class);
        when(cEdge.getTargetNode()).thenReturn(candidate);
        parent.getOutEdges().add(cEdge);

        command = new CaseManagementSetChildCommand(parent, candidate, Optional.of(last), null, Optional.of(parent), null);

        final Command graphCommand = command.newGraphCommand(canvasHandler);
        assertTrue(graphCommand instanceof CaseManagementSetChildNodeGraphCommand);

        final CaseManagementSetChildNodeGraphCommand cmGraphCommand = (CaseManagementSetChildNodeGraphCommand) graphCommand;
        assertTrue(cmGraphCommand.getIndex().isPresent());
        assertEquals(1, cmGraphCommand.getIndex().getAsInt());
        assertTrue(cmGraphCommand.getOriginalIndex().isPresent());
        assertEquals(1, cmGraphCommand.getOriginalIndex().getAsInt());
    }

    @Test
    public void testNewGraphCommand_moveBackward() throws Exception {
        final Edge cEdge = mock(Edge.class);
        when(cEdge.getTargetNode()).thenReturn(candidate);
        parent.getOutEdges().add(cEdge);

        final Node last = mock(Node.class);
        final Edge lEdge = mock(Edge.class);
        when(lEdge.getTargetNode()).thenReturn(last);
        parent.getOutEdges().add(lEdge);

        command = new CaseManagementSetChildCommand(parent, candidate, Optional.of(last), null, Optional.of(parent), null);

        final Command graphCommand = command.newGraphCommand(canvasHandler);
        assertTrue(graphCommand instanceof CaseManagementSetChildNodeGraphCommand);

        final CaseManagementSetChildNodeGraphCommand cmGraphCommand = (CaseManagementSetChildNodeGraphCommand) graphCommand;
        assertTrue(cmGraphCommand.getIndex().isPresent());
        assertEquals(1, cmGraphCommand.getIndex().getAsInt());
        assertTrue(cmGraphCommand.getOriginalIndex().isPresent());
        assertEquals(0, cmGraphCommand.getOriginalIndex().getAsInt());
    }

    @Test
    public void testNewGraphCommand_addNew() throws Exception {
        command = new CaseManagementSetChildCommand(parent, candidate, Optional.empty(), OptionalInt.empty(), Optional.empty(), null);

        final Command graphCommand = command.newGraphCommand(canvasHandler);
        assertTrue(graphCommand instanceof CaseManagementSetChildNodeGraphCommand);

        final CaseManagementSetChildNodeGraphCommand cmGraphCommand = (CaseManagementSetChildNodeGraphCommand) graphCommand;
        assertFalse(cmGraphCommand.getIndex().isPresent());
        assertFalse(cmGraphCommand.getOriginalIndex().isPresent());
    }

    @Test
    public void testNewGraphCommand_addNewStage() throws Exception {
        final Node childNode = mock(Node.class);
        final View cContent = mock(View.class);
        when(childNode.getContent()).thenReturn(cContent);
        when(cContent.getDefinition()).thenReturn(mock(AdHocSubprocess.class));
        when(childNode.getOutEdges()).thenReturn(new LinkedList());

        final Node parentNode = mock(Node.class);
        final View pContent = mock(View.class);
        when(parentNode.getContent()).thenReturn(pContent);
        when(pContent.getDefinition()).thenReturn(mock(CaseManagementDiagram.class));
        when(parentNode.getOutEdges()).thenReturn(new LinkedList());

        final Node start = mock(Node.class);
        final View sContent = mock(View.class);
        when(start.getContent()).thenReturn(sContent);
        when(sContent.getDefinition()).thenReturn(mock(StartNoneEvent.class));
        final Edge sEdge = mock(Edge.class);
        when(sEdge.getTargetNode()).thenReturn(start);
        parentNode.getOutEdges().add(sEdge);

        command = new CaseManagementSetChildCommand(parentNode, childNode, Optional.empty(), OptionalInt.of(0), Optional.empty(), null);

        final Command graphCommand = command.newGraphCommand(canvasHandler);
        assertTrue(graphCommand instanceof CaseManagementSetChildNodeGraphCommand);

        final CaseManagementSetChildNodeGraphCommand cmGraphCommand = (CaseManagementSetChildNodeGraphCommand) graphCommand;
        assertTrue(cmGraphCommand.getIndex().isPresent());
        assertEquals(1, cmGraphCommand.getIndex().getAsInt());
        assertFalse(cmGraphCommand.getOriginalIndex().isPresent());
    }
}