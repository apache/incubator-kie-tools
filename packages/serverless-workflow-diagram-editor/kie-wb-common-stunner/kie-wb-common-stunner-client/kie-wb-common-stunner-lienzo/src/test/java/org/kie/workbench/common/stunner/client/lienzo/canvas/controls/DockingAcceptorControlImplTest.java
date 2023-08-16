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

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateDockNodeCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;
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
public class DockingAcceptorControlImplTest {

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private WiresCanvas canvas;
    @Mock
    private WiresManager wiresManager;
    @Mock
    private Diagram diagram;
    @Mock
    private Node source;
    @Mock
    private Node docked;

    private DockingAcceptorControlImpl tested;
    private UpdateDockNodeCommand updateDockNodeCommand;
    private final CommandResult<CanvasViolation> result = CanvasCommandResultBuilder.SUCCESS;

    @Before
    public void setup() {
        when(canvas.getWiresManager()).thenReturn(wiresManager);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        doAnswer(invocationOnMock -> {
            final Node parent1 = (Node) invocationOnMock.getArguments()[0];
            final Node candidate1 = (Node) invocationOnMock.getArguments()[1];
            updateDockNodeCommand = new UpdateDockNodeCommand(parent1,
                                                              candidate1);
            return updateDockNodeCommand;
        }).when(canvasCommandFactory).updateDockNode(any(Node.class),
                                                     any(Node.class));
        when(commandManager.allow(eq(canvasHandler),
                                  eq(updateDockNodeCommand))).thenReturn(result);
        when(commandManager.execute(eq(canvasHandler),
                                    eq(updateDockNodeCommand))).thenReturn(result);
        this.tested = new DockingAcceptorControlImpl(canvasCommandFactory);
        this.tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testInit() {
        tested.init(canvasHandler);
        assertEquals(canvasHandler,
                     tested.getCanvasHandler());
        verify(wiresManager,
               times(1)).setDockingAcceptor(any(IDockingAcceptor.class));
        verify(wiresManager,
               never()).setContainmentAcceptor(any(IContainmentAcceptor.class));
        verify(wiresManager,
               never()).setConnectionAcceptor(any(IConnectionAcceptor.class));
    }

    @Test
    public void testAllow() {
        tested.init(canvasHandler);
        final boolean allow = tested.allow(source,
                                           docked);
        assertTrue(allow);
        verify(commandManager,
               times(1)).allow(eq(canvasHandler),
                               eq(updateDockNodeCommand));
        assertEquals(source,
                     updateDockNodeCommand.getParent());
        assertEquals(docked,
                     updateDockNodeCommand.getCandidate());
    }

    @Test
    public void testAllowNoParent() {
        tested.init(canvasHandler);
        final boolean allow = tested.allow(null,
                                           docked);
        assertFalse(allow);
        verify(commandManager,
               times(0)).allow(any(AbstractCanvasHandler.class),
                               any(UpdateDockNodeCommand.class));
    }

    @Test
    public void testAccept() {
        tested.init(canvasHandler);
        final boolean accept = tested.accept(source,
                                             docked);
        assertTrue(accept);
        verify(commandManager,
               times(1)).execute(eq(canvasHandler),
                                 eq(updateDockNodeCommand));
        assertEquals(source,
                     updateDockNodeCommand.getParent());
        assertEquals(docked,
                     updateDockNodeCommand.getCandidate());
    }

    @Test
    public void testAceeptNoParent() {
        tested.init(canvasHandler);
        final boolean accept = tested.accept(null,
                                             docked);
        assertFalse(accept);
        verify(commandManager,
               times(0)).execute(any(AbstractCanvasHandler.class),
                                 any(UpdateDockNodeCommand.class));
    }
}
