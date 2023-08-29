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


package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.Collection;

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateChildrenCommand;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ContainmentAcceptorControlImplTest {

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private CanvasHighlight highlight;
    @Mock
    private WiresCanvas canvas;
    @Mock
    private WiresCanvasView canvasView;
    @Mock
    private WiresManager wiresManager;
    @Mock
    private Diagram diagram;
    @Mock
    private Metadata metadata;
    @Mock
    private Node parent;
    @Mock
    private Node candidate;

    private ContainmentAcceptorControlImpl tested;
    private UpdateChildrenCommand updateChildrenCommand;
    private final CommandResult<CanvasViolation> result = CanvasCommandResultBuilder.SUCCESS;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(canvas.getWiresManager()).thenReturn(wiresManager);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(null);
        doAnswer(invocationOnMock -> {
            final Node parent1 = (Node) invocationOnMock.getArguments()[0];
            final Collection candidates = (Collection) invocationOnMock.getArguments()[1];
            updateChildrenCommand = new UpdateChildrenCommand(parent1,
                                                              candidates);
            return updateChildrenCommand;
        }).when(canvasCommandFactory).updateChildren(any(Node.class),
                                                     any(Collection.class));
        when(commandManager.allow(eq(canvasHandler),
                                  eq(updateChildrenCommand))).thenReturn(result);
        when(commandManager.execute(eq(canvasHandler),
                                    eq(updateChildrenCommand))).thenReturn(result);
        this.tested = new ContainmentAcceptorControlImpl(canvasCommandFactory,
                                                         highlight);
        this.tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testEnable() {
        tested.init(canvasHandler);
        verify(highlight, times(1)).setCanvasHandler(eq(canvasHandler));
        assertEquals(canvasHandler,
                     tested.getCanvasHandler());
        verify(wiresManager,
               times(1)).setContainmentAcceptor(any(IContainmentAcceptor.class));
        verify(wiresManager,
               never()).setDockingAcceptor(any(IDockingAcceptor.class));
        verify(wiresManager,
               never()).setConnectionAcceptor(any(IConnectionAcceptor.class));
    }

    @Test
    public void testAllow() {
        tested.init(canvasHandler);
        final boolean allow = tested.allow(parent,
                                           new Node[]{candidate});
        assertTrue(allow);
        verify(commandManager,
               times(1)).allow(eq(canvasHandler),
                               eq(updateChildrenCommand));
        assertEquals(parent,
                     updateChildrenCommand.getParent());
        assertEquals(candidate, updateChildrenCommand.getCandidates().iterator().next());
        verify(highlight, times(1)).unhighLight();
    }

    @Test
    public void testAccept() {
        tested.init(canvasHandler);
        final boolean accept = tested.accept(parent,
                                             new Node[]{candidate});
        assertTrue(accept);
        verify(commandManager,
               times(1)).execute(eq(canvasHandler),
                                 eq(updateChildrenCommand));
        assertEquals(parent,
                     updateChildrenCommand.getParent());
        assertEquals(candidate, updateChildrenCommand.getCandidates().iterator().next());
        verify(highlight, times(1)).unhighLight();
    }

    @Test
    public void testIsSameParent() {
        Node parent = new NodeImpl<>("parentUUID");
        Node child1 = new NodeImpl<>("child1");
        setAsChild(parent, child1);
        Node child2 = new NodeImpl<>("child2");
        setAsChild(parent, child2);
        Node[] children = {child1, child2};
        boolean isSameParent = ContainmentAcceptorControlImpl.areInSameParent(parent, children);
        assertTrue(isSameParent);
    }

    @Test
    public void testIsNotSameParent1() {
        Node parent = new NodeImpl<>("parentUUID");
        Node child1 = new NodeImpl<>("child1");
        Node child2 = new NodeImpl<>("child2");
        setAsChild(parent, child2);
        Node[] children = {child1, child2};
        boolean isSameParent = ContainmentAcceptorControlImpl.areInSameParent(parent, children);
        assertFalse(isSameParent);
    }

    @Test
    public void testIsNotSameParent2() {
        Node parent = new NodeImpl<>("parentUUID");
        Node child1 = new NodeImpl<>("child1");
        setAsChild(parent, child1);
        Node child2 = new NodeImpl<>("child2");
        Node[] children = {child1, child2};
        boolean isSameParent = ContainmentAcceptorControlImpl.areInSameParent(parent, children);
        assertFalse(isSameParent);
    }

    @Test
    public void testIsNotSameParentAll() {
        Node parent = new NodeImpl<>("parentUUID");
        Node child1 = new NodeImpl<>("child1");
        Node child2 = new NodeImpl<>("child2");
        Node[] children = {child1, child2};
        boolean isSameParent = ContainmentAcceptorControlImpl.areInSameParent(parent, children);
        assertFalse(isSameParent);
    }

    @Test
    public void testIsNotSameParentNull() {
        Node parent = new NodeImpl<>("parentUUID");
        Node child1 = new NodeImpl<>("child1");
        setAsChild(parent, child1);
        Node child2 = new NodeImpl<>("child2");
        setAsChild(parent, child2);
        Node[] children = {child1, child2};
        boolean isSameParent = ContainmentAcceptorControlImpl.areInSameParent(null, children);
        assertFalse(isSameParent);
    }

    @SuppressWarnings("unchecked")
    private static void setAsChild(final Node parent,
                                   final Node child) {
        Child childRel = new Child();
        Edge childEdge = new EdgeImpl<>("child_" + parent.getUUID() + "_" + child.getUUID());
        childEdge.setContent(childRel);
        childEdge.setSourceNode(parent);
        parent.getOutEdges().add(childEdge);
        childEdge.setTargetNode(child);
        child.getInEdges().add(childEdge);
    }
}
