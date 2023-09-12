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


package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CopySelectionSessionCommandTest extends BaseSessionCommandKeyboardSelectionAwareTest {

    private CopySelectionSessionCommand copySelectionSessionCommand;

    @Mock
    private EventSourceMock<CopySelectionSessionCommandExecutedEvent> commandExecutedEvent;

    @Mock
    private SessionManager sessionManager;

    private ArgumentCaptor<CopySelectionSessionCommandExecutedEvent> eventArgumentCaptor;

    private ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl;

    @Mock
    private SelectionControl selectionControl;

    @Mock
    private ClientSessionCommand.Callback callback;

    private Node node;

    private TestingGraphInstanceBuilder.TestGraph2 graphInstance;

    @Override
    public void setup() {
        super.setup();
        when(sessionManager.getCurrentSession()).thenReturn(session);
        eventArgumentCaptor = ArgumentCaptor.forClass(CopySelectionSessionCommandExecutedEvent.class);
        clipboardControl = spy(new LocalClipboardControl());
        TestingGraphMockHandler graphMockHandler = new TestingGraphMockHandler();
        this.graphInstance = TestingGraphInstanceBuilder.newGraph2(graphMockHandler);
        this.copySelectionSessionCommand = getCommand();
        node = graphInstance.startNode;
        when(session.getSelectionControl()).thenReturn(selectionControl);
        when(selectionControl.getSelectedItems()).thenReturn(Arrays.asList(node.getUUID()));
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getGraphIndex()).thenReturn(graphMockHandler.graphIndex);
        when(session.getClipboardControl()).thenReturn(clipboardControl);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        copySelectionSessionCommand.bind(session);

        //success
        copySelectionSessionCommand.execute(callback);
        verify(selectionControl, atLeastOnce()).getSelectedItems();
        verify(clipboardControl, times(1)).set(node);
        verify(callback, times(1)).onSuccess();
        verify(commandExecutedEvent, times(1)).fire(eventArgumentCaptor.capture());
        assertEquals(session, eventArgumentCaptor.getValue().getClientSession());
        assertEquals(copySelectionSessionCommand, eventArgumentCaptor.getValue().getExecutedCommand());

        //error
        reset(callback);
        when(selectionControl.getSelectedItems()).thenThrow(new RuntimeException());
        copySelectionSessionCommand.execute(callback);
        verify(callback, never()).onSuccess();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteMultiSelection() {
        copySelectionSessionCommand.bind(session);

        // 1) test copying node - edge - node
        when(selectionControl.getSelectedItems()).thenReturn(Arrays.asList(
                graphInstance.startNode.getUUID(),
                graphInstance.edge1.getUUID(),
                graphInstance.intermNode.getUUID())
        );
        graphInstance.edge1.setSourceNode(graphInstance.startNode);
        graphInstance.edge1.setTargetNode(graphInstance.intermNode);

        copySelectionSessionCommand.execute(callback);
        verify(clipboardControl, times(1))
                .set(graphInstance.startNode, graphInstance.edge1, graphInstance.intermNode);
        assertEquals(1, clipboardControl.getEdgeMap().size());
        assertEquals(clipboardControl.getEdgeMap().get(graphInstance.edge1.getUUID()).getSource(), graphInstance.startNode.getUUID());
        assertEquals(clipboardControl.getEdgeMap().get(graphInstance.edge1.getUUID()).getTarget(), graphInstance.intermNode.getUUID());

        // 2) test copying edge - node - edge - node
        when(selectionControl.getSelectedItems()).thenReturn(Arrays.asList(
                graphInstance.edge2.getUUID(),
                graphInstance.startNode.getUUID(),
                graphInstance.edge1.getUUID(),
                graphInstance.intermNode.getUUID())
        );

        graphInstance.edge2.setSourceNode(graphInstance.endNode);
        graphInstance.edge2.setTargetNode(graphInstance.startNode);

        graphInstance.edge1.setSourceNode(graphInstance.startNode);
        graphInstance.edge1.setTargetNode(graphInstance.intermNode);

        copySelectionSessionCommand.execute(callback);
        verify(clipboardControl, times(1))
                .set(graphInstance.edge2, graphInstance.startNode, graphInstance.edge1, graphInstance.intermNode);
        // edge map should contain only node - edge - node and discard edge that has no source being copied
        assertEquals(1, clipboardControl.getEdgeMap().size());
        assertEquals(clipboardControl.getEdgeMap().get(graphInstance.edge1.getUUID()).getSource(), graphInstance.startNode.getUUID());
        assertEquals(clipboardControl.getEdgeMap().get(graphInstance.edge1.getUUID()).getTarget(), graphInstance.intermNode.getUUID());

        // 3) test copying node - edge - node - edge

        when(selectionControl.getSelectedItems()).thenReturn(Arrays.asList(
                graphInstance.startNode.getUUID(),
                graphInstance.edge1.getUUID(),
                graphInstance.intermNode.getUUID(),
                graphInstance.edge2.getUUID())
        );

        graphInstance.edge1.setSourceNode(graphInstance.startNode);
        graphInstance.edge1.setTargetNode(graphInstance.intermNode);

        graphInstance.edge2.setSourceNode(graphInstance.intermNode);
        graphInstance.edge2.setTargetNode(graphInstance.endNode);

        copySelectionSessionCommand.execute(callback);
        verify(clipboardControl, times(1))
                .set(graphInstance.startNode, graphInstance.edge1, graphInstance.intermNode, graphInstance.edge2);
        // edge map should contain only node - edge - node and discard edge that has no target being copied
        assertEquals(1, clipboardControl.getEdgeMap().size());
        assertEquals(clipboardControl.getEdgeMap().get(graphInstance.edge1.getUUID()).getSource(), graphInstance.startNode.getUUID());
        assertEquals(clipboardControl.getEdgeMap().get(graphInstance.edge1.getUUID()).getTarget(), graphInstance.intermNode.getUUID());

        // 4) test copying node - node

        when(selectionControl.getSelectedItems()).thenReturn(Arrays.asList(
                graphInstance.startNode.getUUID(),
                graphInstance.intermNode.getUUID(),
                graphInstance.edge1.getUUID())
        );

        graphInstance.edge1.setSourceNode(graphInstance.endNode);
        graphInstance.edge1.setTargetNode(graphInstance.parentNode);

        copySelectionSessionCommand.execute(callback);
        verify(clipboardControl, times(1))
                .set(graphInstance.startNode, graphInstance.intermNode, graphInstance.edge1);
        // edge map should not contain only node - node 
        assertEquals(0, clipboardControl.getEdgeMap().size());
    }

    @Test
    public void testExecuteNullSessionAndNullSelectionControl() {
        copySelectionSessionCommand.execute(callback);
        // if session null, then it should never copy items
        verify(clipboardControl, never()).set(any(), any(), any());

        copySelectionSessionCommand.bind(session);
        when(session.getSelectionControl()).thenReturn(null);
        copySelectionSessionCommand.execute(callback);
        // if session null, then it should never copy items
        verify(clipboardControl, never()).set(any(), any(), any());
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testEmptyConstructor() {
        CopySelectionSessionCommand copy = new CopySelectionSessionCommand(null, null);
    }

    @Override
    protected CopySelectionSessionCommand getCommand() {
        return CopySelectionSessionCommand.getInstance(commandExecutedEvent, sessionManager);
    }

    @Override
    protected Key[] getExpectedKeys() {
        return new Key[]{Key.CONTROL, Key.C};
    }

    @Override
    protected Key[] getUnexpectedKeys() {
        return new Key[]{Key.ESC};
    }

    @Override
    protected int getExpectedKeyBoardControlRegistrationCalls() {
        return 2;
    }
}