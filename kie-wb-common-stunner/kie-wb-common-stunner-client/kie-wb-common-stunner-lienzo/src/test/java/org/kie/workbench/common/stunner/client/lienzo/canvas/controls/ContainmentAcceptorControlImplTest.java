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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

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
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateChildNodeCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
    private UpdateChildNodeCommand updateChildNodeCommand;
    private final CommandResult<CanvasViolation> result = CanvasCommandResultBuilder.SUCCESS;

    @Before
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
            final Node candidate1 = (Node) invocationOnMock.getArguments()[1];
            updateChildNodeCommand = new UpdateChildNodeCommand(parent1,
                                                                candidate1);
            return updateChildNodeCommand;
        }).when(canvasCommandFactory).updateChildNode(any(Node.class),
                                                      any(Node.class));
        when(commandManager.allow(eq(canvasHandler),
                                  eq(updateChildNodeCommand))).thenReturn(result);
        when(commandManager.execute(eq(canvasHandler),
                                    eq(updateChildNodeCommand))).thenReturn(result);
        this.tested = new ContainmentAcceptorControlImpl(canvasCommandFactory);
        this.tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testEnable() {
        tested.init(canvasHandler);
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
                               eq(updateChildNodeCommand));
        assertEquals(parent,
                     updateChildNodeCommand.getParent());
        assertEquals(candidate,
                     updateChildNodeCommand.getCandidate());
    }

    @Test
    public void testAccept() {
        tested.init(canvasHandler);
        final boolean accept = tested.accept(parent,
                                             new Node[]{candidate});
        assertTrue(accept);
        verify(commandManager,
               times(1)).execute(eq(canvasHandler),
                                 eq(updateChildNodeCommand));
        assertEquals(parent,
                     updateChildNodeCommand.getParent());
        assertEquals(candidate,
                     updateChildNodeCommand.getCandidate());
    }
}
