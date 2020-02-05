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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactoryStub;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCommand;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GeneralCreateNodeActionTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ClientFactoryManager clientFactoryManager;

    @Mock
    private CanvasLayoutUtils canvasLayoutUtils;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> selectionEvent;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    private ManagedInstanceStub<DefaultCanvasCommandFactory> canvasCommandFactories;
    private DefaultCanvasCommandFactory canvasCommandFactory;

    private GeneralCreateNodeAction createNodeAction;

    @Before
    public void setUp() throws Exception {

        canvasCommandFactory = new CanvasCommandFactoryStub();
        canvasCommandFactories = new ManagedInstanceStub<>(canvasCommandFactory);
        createNodeAction = new GeneralCreateNodeAction(definitionUtils,
                                                       clientFactoryManager,
                                                       canvasLayoutUtils,
                                                       selectionEvent,
                                                       sessionCommandManager,
                                                       canvasCommandFactories) {

        };
    }

    @Test
    public void testExecuteAction() {
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final String sourceNodeId = "src-id";
        final String targetNodeId = "dest-id";
        final String connectorId = "edge-id";

        final AbstractCanvas canvas = mock(AbstractCanvas.class);
        doReturn(canvas).when(canvasHandler).getCanvas();
        doReturn(canvas).when(canvasHandler).getAbstractCanvas();
        final Diagram diagram = mock(Diagram.class);
        doReturn(diagram).when(canvasHandler).getDiagram();
        final Metadata metadata = mock(Metadata.class);
        doReturn(metadata).when(diagram).getMetadata();
        final String shapeSetId = "shape-set-id";
        doReturn(shapeSetId).when(metadata).getShapeSetId();

        final Index graphIndex = mock(Index.class);
        doReturn(graphIndex).when(canvasHandler).getGraphIndex();

        final Element sourceElement = mock(Element.class);
        doReturn(sourceElement).when(graphIndex).get(sourceNodeId);
        final Node sourceNode = mock(Node.class);
        final View sourceElementContent = mock(View.class);
        doReturn(sourceElementContent).when(sourceNode).getContent();
        doReturn(Bounds.create(10d, 0d, 200d, 100d)).when(sourceElementContent).getBounds();
        doReturn(sourceNode).when(sourceElement).asNode();
        doReturn(Collections.emptyList()).when(sourceNode).getInEdges();

        final Element targetNodeElement = mock(Element.class);
        doReturn(targetNodeElement).when(clientFactoryManager).newElement(anyString(), eq(targetNodeId));
        final Node targetNode = mock(Node.class);
        final View targetElementContent = mock(View.class);
        doReturn(targetElementContent).when(targetNode).getContent();
        doReturn(Bounds.create(-100d, 0d, 0d, 100d)).when(targetElementContent).getBounds();
        doReturn(targetNode).when(targetNodeElement).asNode();
        final String targetNodeUuid = "target-uuid";
        doReturn(targetNodeUuid).when(targetNode).getUUID();

        final Element connectorElement = mock(Element.class);
        doReturn(connectorElement).when(clientFactoryManager).newElement(anyString(), eq(connectorId));
        final Edge connectorEdge = mock(Edge.class);
        doReturn(connectorEdge).when(connectorElement).asEdge();

        when(canvasLayoutUtils.getNext(eq(canvasHandler),
                                       eq(sourceNode),
                                       eq(targetNode)))
                .thenReturn(new Point2D(100d,
                                        500d));

        createNodeAction.executeAction(canvasHandler,
                                       sourceNodeId,
                                       targetNodeId,
                                       connectorId);

        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              commandArgumentCaptor.capture());

        final DeferredCompositeCommand command = (DeferredCompositeCommand) commandArgumentCaptor.getValue();
        final DeferredCommand c0 = (DeferredCommand) command.getCommands().get(0);
        final DeferredCommand c1 = (DeferredCommand) command.getCommands().get(1);
        final DeferredCommand c2 = (DeferredCommand) command.getCommands().get(2);
        final DeferredCommand c3 = (DeferredCommand) command.getCommands().get(3);
        final AddNodeCommand addNodeCommand = (AddNodeCommand) c0.getCommand();
        final UpdateElementPositionCommand updateElementPositionCommand = (UpdateElementPositionCommand) c1.getCommand();
        final AddConnectorCommand addConnectorCommand = (AddConnectorCommand) c2.getCommand();
        final SetConnectionTargetNodeCommand setTargetNodeCommand = (SetConnectionTargetNodeCommand) c3.getCommand();
        Assertions.assertThat(targetNode).isEqualTo(addNodeCommand.getCandidate());
        Assertions.assertThat(shapeSetId).isEqualTo(addNodeCommand.getShapeSetId());
        Assertions.assertThat(connectorEdge).isEqualTo(addConnectorCommand.getCandidate());
        Assertions.assertThat(sourceNode).isEqualTo(addConnectorCommand.getSource());
        Assertions.assertThat(shapeSetId).isEqualTo(addConnectorCommand.getShapeSetId());
        Assertions.assertThat(connectorEdge).isEqualTo(setTargetNodeCommand.getEdge());
        Assertions.assertThat(targetNode).isEqualTo(setTargetNodeCommand.getNode());
        Assertions.assertThat(targetNode).isEqualTo(updateElementPositionCommand.getElement());
        Assertions.assertThat(new Point2D(100d, 500d)).isEqualTo(updateElementPositionCommand.getLocation());
        final ArgumentCaptor<CanvasSelectionEvent> eventArgumentCaptor = ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        verify(selectionEvent).fire(eventArgumentCaptor.capture());
        final CanvasSelectionEvent eCaptured = eventArgumentCaptor.getValue();
        Assertions.assertThat(targetNodeUuid).isEqualTo(eCaptured.getIdentifiers().iterator().next());
        Assertions.assertThat(addConnectorCommand.getConnection()).isInstanceOf(MagnetConnection.class);
        Assertions.assertThat(((MagnetConnection) addConnectorCommand.getConnection()).getMagnetIndex().getAsInt())
                .isEqualTo(MagnetConnection.MAGNET_LEFT);
        Assertions.assertThat(setTargetNodeCommand.getConnection()).isInstanceOf(MagnetConnection.class);
        Assertions.assertThat(((MagnetConnection) setTargetNodeCommand.getConnection()).getMagnetIndex().getAsInt())
                .isEqualTo(MagnetConnection.MAGNET_RIGHT);
    }
}
