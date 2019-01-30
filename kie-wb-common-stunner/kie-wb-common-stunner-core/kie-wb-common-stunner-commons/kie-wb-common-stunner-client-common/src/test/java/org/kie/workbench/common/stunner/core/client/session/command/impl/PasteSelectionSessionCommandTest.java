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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.CloneConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.CloneNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand.DEFAULT_PADDING;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasteSelectionSessionCommandTest extends BaseSessionCommandKeyboardTest {

    public static final double NODE_SIZE = 10d;

    public static final double X = 20d;

    public static final double Y = 20d;

    private PasteSelectionSessionCommand pasteSelectionSessionCommand;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    private Node node;

    private Node node2;

    private TestingGraphInstanceBuilder.TestGraph2 graphInstance;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CommandRegistry commandRegistry;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl;

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

    @Mock
    private CommandResult commandResultConnector;

    private static final String CANVAS_UUID = UUID.uuid();

    @Mock
    private Node clone;

    @Mock
    private Node clone2;

    @Mock
    private Edge cloneEdge;

    private Map<Node, Node> cloneMap;

    @Mock
    private CloneNodeCommand cloneNodeCommand;

    @Mock
    private CloneNodeCommand cloneNodeCommand2;

    @Mock
    private CloneConnectorCommand cloneConnectorCommand;

    @Mock
    private CopySelectionSessionCommand copySelectionSessionCommand;

    @Mock
    private org.uberfire.mvp.Command statusCallback;

    @Mock
    private DefinitionUtils definitionUtils;

    private final String DEFINITION_SET_ID = "mockDefinitionSetId";

    @Mock
    private Annotation qualifier;

    @Mock
    private ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance;

    private static final String CLONE_UUID = UUID.uuid();

    private static final String CLONE2_UUID = UUID.uuid();

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        clipboardControl = spy(new LocalClipboardControl());

        TestingGraphMockHandler graphMockHandler = new TestingGraphMockHandler();
        this.graphInstance = TestingGraphInstanceBuilder.newGraph2(graphMockHandler);
        node = graphInstance.startNode;
        node.setContent(view);
        node2 = graphInstance.intermNode;
        node2.setContent(view);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getGraphIndex()).thenReturn(graphMockHandler.graphIndex);
        when(view.getBounds()).thenReturn(Bounds.create(X, Y, X + NODE_SIZE, Y + NODE_SIZE));
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(CANVAS_UUID);
        when(sessionCommandManager.execute(eq(canvasHandler), any())).thenReturn(commandResult);
        when(sessionCommandManager.execute(canvasHandler, cloneConnectorCommand)).thenReturn(commandResultConnector);
        when(commandResult.getType()).thenReturn(CommandResult.Type.INFO);
        when(commandResultConnector.getType()).thenReturn(CommandResult.Type.INFO);
        when(clone.getUUID()).thenReturn(CLONE_UUID);
        when(clone2.getUUID()).thenReturn(CLONE2_UUID);
        when(session.getClipboardControl()).thenReturn(clipboardControl);
        when(sessionCommandManager.getRegistry()).thenReturn(commandRegistry);

        cloneMap = new HashMap() {{
            put(node, clone);
            put(node2, clone2);
        }};

        super.setup();
        this.pasteSelectionSessionCommand = getCommand();

        when(metadata.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(definitionUtils.getQualifier(eq(DEFINITION_SET_ID))).thenReturn(qualifier);
        when(canvasCommandFactoryInstance.select(eq(qualifier))).thenReturn(canvasCommandFactoryInstance);
        when(canvasCommandFactoryInstance.isUnsatisfied()).thenReturn(false);
        when(canvasCommandFactoryInstance.get()).thenReturn(canvasCommandFactory);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void execute() {
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
                .cloneNode(eq(node), eq(graphInstance.parentNode.getUUID()), eq(new Point2D(X, DEFAULT_PADDING + Y + NODE_SIZE)), any());

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

    @Test
    @SuppressWarnings("unchecked")
    public void executeWithMultiSelection() {
        pasteSelectionSessionCommand.bind(session);

        //Mock the callback of CloneNodeCommand
        ArgumentCaptor<Consumer> consumerNode = ArgumentCaptor.forClass(Consumer.class);
        ArgumentCaptor<Consumer> consumerNode2 = ArgumentCaptor.forClass(Consumer.class);
        when(cloneNodeCommand.getCandidate()).thenReturn(node);
        when(cloneNodeCommand2.getCandidate()).thenReturn(node2);
        when(canvasCommandFactory.cloneNode(eq(node), any(), any(), consumerNode.capture())).thenReturn(cloneNodeCommand);
        when(canvasCommandFactory.cloneNode(eq(node2), any(), any(), consumerNode2.capture())).thenReturn(cloneNodeCommand2);
        Map<Node, ArgumentCaptor<Consumer>> consumerMap = new HashMap() {{
            put(node, consumerNode);
            put(node2, consumerNode2);
        }};

        //Mock the callback of CloneConnectorCommand
        ArgumentCaptor<Consumer> consumerEdge = ArgumentCaptor.forClass(Consumer.class);
        when(canvasCommandFactory.cloneConnector(any(), anyString(), anyString(), anyString(), consumerEdge.capture())).thenReturn(cloneConnectorCommand);

        //apply callbacks mocks
        when(sessionCommandManager.execute(eq(canvasHandler), any())).thenAnswer(param -> {
            CompositeCommand argument = param.getArgumentAt(1, CompositeCommand.class);
            //callback to nodes
            argument.getCommands().stream().filter(c -> c instanceof CloneNodeCommand).forEach(c -> {
                CloneNodeCommand cloneNodeCommand = (CloneNodeCommand) c;
                Node candidate = cloneNodeCommand.getCandidate();
                consumerMap.get(candidate).getValue().accept(cloneMap.get(candidate));
            });

            //callback to connectors
            argument.getCommands().stream().filter(c -> c instanceof CloneConnectorCommand).forEach(c ->
                                                                                                            consumerEdge.getValue().accept(cloneEdge)
            );
            return commandResult;
        });

        //Executing the command
        clipboardControl.set(graphInstance.startNode, graphInstance.edge1, graphInstance.intermNode);
        when(selectionControl.getSelectedItems()).thenReturn(Arrays.asList(graphInstance.startNode.getUUID(), graphInstance.edge1.getUUID(), graphInstance.intermNode.getUUID()));
        pasteSelectionSessionCommand.execute(callback);
        verify(canvasCommandFactory, times(1))
                .cloneNode(eq(graphInstance.startNode), eq(graphInstance.parentNode.getUUID()), eq(new Point2D(X, DEFAULT_PADDING + Y + NODE_SIZE)), any());

        verify(canvasCommandFactory, times(1))
                .cloneConnector(eq(graphInstance.edge1), anyString(), anyString(), anyString(), any());

        //check command registry update after execution to allow a single undo/redo
        verify(commandRegistry, times(2)).pop();
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandRegistry, times(1)).register(commandArgumentCaptor.capture());
        assertTrue(commandArgumentCaptor.getValue() instanceof CompositeCommand);
        assertEquals(((CompositeCommand) commandArgumentCaptor.getValue()).size(), 2);
    }

    @Override
    protected PasteSelectionSessionCommand getCommand() {
        return new PasteSelectionSessionCommand(sessionCommandManager, canvasCommandFactoryInstance,
                                                selectionEvent, copySelectionSessionCommand,
                                                definitionUtils);
    }

    @Override
    protected KeyboardEvent.Key[] getExpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.CONTROL, KeyboardEvent.Key.V};
    }

    @Override
    protected KeyboardEvent.Key[] getUnexpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.ESC};
    }

    @Test
    public void testOnCopySelectionCommandExecuted() {
        pasteSelectionSessionCommand.bind(session);
        pasteSelectionSessionCommand.listen(statusCallback);
        pasteSelectionSessionCommand.onCopySelectionCommandExecuted(new CopySelectionSessionCommandExecutedEvent(mock(CopySelectionSessionCommand.class),
                                                                                                                 session));
        verify(statusCallback, times(1)).execute();
        assertTrue(command.isEnabled());
    }

    @Test
    public void testOnCutSelectionCommandExecuted() {
        pasteSelectionSessionCommand.bind(session);
        pasteSelectionSessionCommand.listen(statusCallback);
        pasteSelectionSessionCommand.onCutSelectionCommandExecuted(new CutSelectionSessionCommandExecutedEvent(mock(CutSelectionSessionCommand.class),
                                                                                                               session));
        verify(statusCallback, times(1)).execute();
        assertTrue(command.isEnabled());
    }
}