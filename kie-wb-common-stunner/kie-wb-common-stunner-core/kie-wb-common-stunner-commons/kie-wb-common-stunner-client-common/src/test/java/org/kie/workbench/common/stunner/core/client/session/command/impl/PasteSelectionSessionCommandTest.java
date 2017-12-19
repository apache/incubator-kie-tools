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
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.CloneNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand.DEFAULT_PADDING;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasteSelectionSessionCommandTest extends BaseSessionCommandKeyboardTest {

    private PasteSelectionSessionCommand pasteSelectionSessionCommand;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    private Node node;

    private TestingGraphInstanceBuilder.TestGraph2 graphInstance;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl;

    @Mock
    private SessionCommandFactory sessionCommandFactory;

    @Mock
    private Event<CanvasSelectionEvent> selectionEvent;

    @Mock
    private ClientSessionCommand.Callback callback;

    @Mock
    private View view;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private CommandResult commandResult;

    private static final String CANVAS_UUID = UUID.uuid();

    @Mock
    private Node clone;

    @Mock
    private CloneNodeCommand cloneNodeCommand;

    @Mock
    private CopySelectionSessionCommand copySelectionSessionCommand;

    private static final String CLONE_UUID = UUID.uuid();

    @Before
    public void setUp() throws Exception {
        clipboardControl = spy(new LocalClipboardControl());

        TestingGraphMockHandler graphMockHandler = new TestingGraphMockHandler();
        this.graphInstance = TestingGraphInstanceBuilder.newGraph2(graphMockHandler);
        node = graphInstance.startNode;
        node.setContent(view);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getGraphIndex()).thenReturn(graphMockHandler.graphIndex);
        when(view.getBounds()).thenReturn(new BoundsImpl(new BoundImpl(20d, 20d), new BoundImpl(30d, 30d)));
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(CANVAS_UUID);
        when(sessionCommandManager.execute(eq(canvasHandler), any())).thenReturn(commandResult);
        when(commandResult.getType()).thenReturn(CommandResult.Type.INFO);
        when(clone.getUUID()).thenReturn(CLONE_UUID);
        when(session.getClipboardControl()).thenReturn(clipboardControl);
        when(sessionCommandFactory.newCopySelectionCommand()).thenReturn(copySelectionSessionCommand);

        super.setup();
        this.pasteSelectionSessionCommand = getCommand();
    }

    @Test
    public void execute() throws Exception {
        pasteSelectionSessionCommand.bind(session);

        //Mock the callback of CloneNodeCommand
        ArgumentCaptor<Consumer> consumerArgumentCaptor
                = ArgumentCaptor.forClass(Consumer.class);
        when(canvasCommandFactory.cloneNode(any(), any(), any(), consumerArgumentCaptor.capture())).thenReturn(cloneNodeCommand);
        when(commandResult.getType()).thenAnswer(param -> {
            consumerArgumentCaptor.getValue().accept(clone);
            return CommandResult.Type.INFO;
        });

        //same parent
        clipboardControl.set(graphInstance.startNode);
        when(selectionControl.getSelectedItems()).thenReturn(Arrays.asList(node.getUUID()));
        pasteSelectionSessionCommand.execute(callback);
        verify(canvasCommandFactory, times(1))
                .cloneNode(eq(node), eq(graphInstance.parentNode.getUUID()), eq(new Point2D(35d, 35d)), any());

        //different parent
        clipboardControl.set(graphInstance.startNode);
        when(selectionControl.getSelectedItems()).thenReturn(Arrays.asList(graphInstance.intermNode.getUUID()));
        pasteSelectionSessionCommand.execute(callback);
        verify(canvasCommandFactory, times(1))
                .cloneNode(eq(node), eq(graphInstance.intermNode.getUUID()), eq(new Point2D(DEFAULT_PADDING, DEFAULT_PADDING)), any());

        //no parent selected -> canvas
        clipboardControl.set(graphInstance.startNode);
        when(selectionControl.getSelectedItems()).thenReturn(Collections.emptyList());
        pasteSelectionSessionCommand.execute(callback);
        verify(canvasCommandFactory, times(1))
                .cloneNode(eq(node), eq(CANVAS_UUID), eq(new Point2D(DEFAULT_PADDING, DEFAULT_PADDING)), any());

        //success
        verify(callback, times(3)).onSuccess();
        ArgumentCaptor<CanvasSelectionEvent> canvasElementSelectedEventArgumentCaptor
                = ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        verify(selectionEvent, times(3)).fire(canvasElementSelectedEventArgumentCaptor.capture());
        assertTrue(canvasElementSelectedEventArgumentCaptor.getAllValues().stream()
                           .allMatch(event -> Objects.equals(event.getIdentifiers().iterator().next(), clone.getUUID())));

        //error
        clipboardControl.set(graphInstance.startNode);
        reset(selectionEvent, canvasCommandFactory, callback, commandResult);
        when(commandResult.getType()).thenReturn(CommandResult.Type.ERROR);
        pasteSelectionSessionCommand.execute(callback);
        verify(canvasCommandFactory, times(1))
                .cloneNode(eq(node), eq(CANVAS_UUID), eq(new Point2D(DEFAULT_PADDING, DEFAULT_PADDING)), any());
        verify(callback, never()).onSuccess();
        verify(selectionEvent, never()).fire(canvasElementSelectedEventArgumentCaptor.capture());
    }

    @Override
    protected PasteSelectionSessionCommand getCommand() {
        return new PasteSelectionSessionCommand(sessionCommandManager, canvasCommandFactory,
                                                selectionEvent, sessionCommandFactory);
    }

    @Override
    protected KeyboardEvent.Key[] getExpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.CONTROL, KeyboardEvent.Key.V};
    }

    @Override
    protected KeyboardEvent.Key[] getUnexpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.ESC};
    }
}