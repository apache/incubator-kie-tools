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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CopySelectionSessionCommandTest extends BaseSessionCommandKeyboardTest {

    private CopySelectionSessionCommand copySelectionSessionCommand;

    private ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl;

    @Mock
    private SelectionControl selectionControl;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private ClientSessionCommand.Callback callback;

    private Node node;

    private TestingGraphInstanceBuilder.TestGraph2 graphInstance;

    @Before
    public void setUp() throws Exception {
        clipboardControl = spy(new LocalClipboardControl());

        super.setup();

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
    public void testExecute() {
        copySelectionSessionCommand.bind(session);

        //success
        copySelectionSessionCommand.execute(callback);
        verify(selectionControl, atLeastOnce()).getSelectedItems();
        verify(clipboardControl, times(1)).set(node);
        verify(callback, times(1)).onSuccess();

        //error
        reset(callback);
        when(selectionControl.getSelectedItems()).thenThrow(new RuntimeException());
        copySelectionSessionCommand.execute(callback);
        verify(callback, never()).onSuccess();
    }

    @Test
    public void testExecuteMultiSelection() {
        copySelectionSessionCommand.bind(session);

        when(selectionControl.getSelectedItems()).thenReturn(Arrays.asList(graphInstance.startNode.getUUID(),
                                                                           graphInstance.edge1.getUUID(),
                                                                           graphInstance.intermNode.getUUID()));
        copySelectionSessionCommand.execute(callback);
        verify(clipboardControl, times(1))
                .set(graphInstance.startNode, graphInstance.edge1, graphInstance.intermNode);
    }

    @Override
    protected CopySelectionSessionCommand getCommand() {
        return new CopySelectionSessionCommand();
    }

    @Override
    protected Key[] getExpectedKeys() {
        return new Key[]{Key.CONTROL, Key.C};
    }

    @Override
    protected Key[] getUnexpectedKeys() {
        return new Key[]{Key.ESC};
    }
}