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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.builder;

import java.util.Collections;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ObserverBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.controls.exceptions.ElementOutOfBoundsException;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.ParameterizedCommand;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ObserverBuilderControlTest {

    @Mock
    ClientDefinitionManager clientDefinitionManager;

    @Mock
    ClientFactoryService clientFactoryServices;

    @Mock
    GraphUtils graphUtils;

    @Mock
    RuleManager ruleManager;

    @Mock
    CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    GraphBoundsIndexer graphBoundsIndexer;

    @Mock
    GraphIndexBuilder graphIndexBuilder;

    @Mock
    ShapeManager shapeManager;

    @Mock
    RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    private View<Object> view;
    private ObserverBuilderControl tested;

    private static final String DEF = "def";
    private static final String DEF_ID = "def_id";

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                ServiceCallback<Node<View<Object>, Edge>> callback = (ServiceCallback<Node<View<Object>, Edge>>) (invocationOnMock.getArguments()[2]);
                view = new ViewImpl<>(new Object(), new BoundsImpl(new BoundImpl(0.0, 0.0), new BoundImpl(10.0, 20.0)));
                Node<View<Object>, Edge> item = new NodeImpl<>("UUID");
                item.setContent(view);
                callback.onSuccess(item);
                return null;
            }
        }).when(clientFactoryServices).newElement(anyString(), anyString(), any(ServiceCallback.class));

        when(graphBoundsIndexer.setRootUUID(anyString())).thenReturn(graphBoundsIndexer);

        AdapterManager adapters = mock(AdapterManager.class);
        DefinitionAdapter definitionAdapter = mock(DefinitionAdapter.class);
        DefinitionSetRuleAdapter rulesAdapter = mock(DefinitionSetRuleAdapter.class);
        when(definitionAdapter.getId(any())).thenReturn("Object");
        when(rulesAdapter.getRuleSet(any())).thenReturn(mock(RuleSet.class));
        when(adapters.forDefinition()).thenReturn(definitionAdapter);
        when(adapters.forRules()).thenReturn(rulesAdapter);
        when(definitionAdapter.getId(DEF)).thenReturn(DEF_ID);

        when(clientDefinitionManager.adapters()).thenReturn(adapters);
        when(clientDefinitionManager.definitionSets()).thenReturn(mock(TypeDefinitionSetRegistry.class));

        when(canvasCommandFactory.addNode(any(Node.class), anyString())).thenAnswer(new Answer<Command>() {

            @Override
            public Command answer(InvocationOnMock invocationOnMock) {
                Node node = (Node) invocationOnMock.getArguments()[0];
                String uid = (String) invocationOnMock.getArguments()[1];
                return new AddCanvasNodeCommand(node, uid);
            }
        });

        when(canvasCommandFactory.updatePosition(any(Node.class), any(Point2D.class))).thenAnswer(new Answer<Command>() {

            @Override
            public Command answer(InvocationOnMock invocationOnMock) {
                Node node = (Node) invocationOnMock.getArguments()[0];
                Point2D location = (Point2D) invocationOnMock.getArguments()[1];
                return new UpdateElementPositionCommand(node, location);
            }
        });

        when(canvasCommandFactory.draw()).thenReturn(mock(CanvasCommand.class));
        ShapeSet shapeSet = mock(ShapeSet.class);
        ShapeFactory shapeFactory = mock(ShapeFactory.class);

        when(shapeFactory.newShape(any())).thenReturn(mock(ElementShape.class));
        when(shapeSet.getShapeFactory()).thenReturn(shapeFactory);

        when(shapeManager.getShapeSet(anyString())).thenReturn(shapeSet);
        when(shapeManager.getDefaultShapeSet(anyString())).thenReturn(shapeSet);

        tested = new ObserverBuilderControl(clientDefinitionManager, clientFactoryServices, ruleManager, canvasCommandFactory, graphBoundsIndexer, mock(Event.class));

        Diagram diagram = mock(Diagram.class);
        Metadata metadata = mock(Metadata.class);

        when(metadata.getCanvasRootUUID()).thenReturn("ID");
        when(diagram.getMetadata()).thenReturn(metadata);
        MutableIndex index = mock(MutableIndex.class);
        Graph graph = mock(Graph.class);
        DefinitionSet graphContent = mock(DefinitionSet.class);
        when(graphContent.getBounds()).thenReturn(new BoundsImpl(new BoundImpl(10d, 10d), new BoundImpl(100d, 100d)));
        when(graph.getContent()).thenReturn(graphContent);
        when(index.getGraph()).thenReturn(graph);

        when(graphIndexBuilder.build(any(Graph.class))).thenReturn(index);
        AbstractCanvasHandler canvasHandler = new CanvasHandlerImpl(clientDefinitionManager,
                                                                    canvasCommandFactory,
                                                                    clientFactoryServices,
                                                                    ruleManager,
                                                                    graphUtils,
                                                                    graphIndexBuilder,
                                                                    shapeManager,
                                                                    mock(TextPropertyProviderFactory.class),
                                                                    mock(Event.class),
                                                                    null,
                                                                    null,
                                                                    null);
        canvasHandler.handle(mock(AbstractCanvas.class));
        canvasHandler.draw(diagram, mock(ParameterizedCommand.class));
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.emptyList());

        CanvasCommandManager commandManager = mock(CanvasCommandManager.class);

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                Command command = (Command) invocationOnMock.getArguments()[1];
                command.execute(invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(commandManager).execute(any(), any(Command.class));
        when(commandManagerProvider.getCommandManager()).thenReturn(commandManager);

        tested.init(canvasHandler);
        tested.setCommandManagerProvider(commandManagerProvider);
    }

    @Test
    public void testAddElement() {
        ElementBuildRequest<AbstractCanvasHandler> request = mock(ElementBuildRequest.class);
        when(request.getX()).thenReturn(5.0);
        when(request.getY()).thenReturn(5.0);
        when(request.getDefinition()).thenReturn(DEF);
        tested.build(request, new BuilderControl.BuildCallback() {

            @Override
            public void onSuccess(String uuid) {
                assertEquals(10.0, view.getBounds().getLowerRight().getX(), 0.00001);
                assertEquals(0.0, view.getBounds().getUpperLeft().getX(), 0.00001);
                assertEquals(20.0, view.getBounds().getLowerRight().getY(), 0.00001);
                assertEquals(0.0, view.getBounds().getUpperLeft().getY(), 0.00001);
            }

            @Override
            public void onError(ClientRuntimeError error) {
                fail(error.getMessage());
            }
        });
    }

    @Test
    public void testAddElementInsideCanvas() {
        ElementBuildRequest<AbstractCanvasHandler> request = mock(ElementBuildRequest.class);
        BuilderControl.BuildCallback buildCallback = mock(BuilderControl.BuildCallback.class);
        ArgumentCaptor<ClientRuntimeError> errorArgumentCaptor = ArgumentCaptor.forClass(ClientRuntimeError.class);

        reset(buildCallback);
        when(request.getX()).thenReturn(5.0);
        when(request.getY()).thenReturn(5.0);
        when(request.getDefinition()).thenReturn(DEF);
        tested.build(request, buildCallback);
        verify(buildCallback, never()).onError(errorArgumentCaptor.capture());
        verify(buildCallback, times(1)).onSuccess(anyString());
    }

    @Test
    public void testAddElementOutsideCanvas() {
        executeOutOfBoundsTest(-5.0, -5.0);
        executeOutOfBoundsTest(5.0, -5.0);
        executeOutOfBoundsTest(-5.0, 5.0);
        executeOutOfBoundsTest(100.0, 0.0);
        executeOutOfBoundsTest(0.0, 100.0);
    }

    public void executeOutOfBoundsTest(double x, double y) {
        ElementBuildRequest<AbstractCanvasHandler> request = mock(ElementBuildRequest.class);
        BuilderControl.BuildCallback buildCallback = mock(BuilderControl.BuildCallback.class);
        ArgumentCaptor<ClientRuntimeError> errorArgumentCaptor = ArgumentCaptor.forClass(ClientRuntimeError.class);

        when(request.getX()).thenReturn(x);
        when(request.getY()).thenReturn(y);
        tested.build(request, buildCallback);
        verify(buildCallback, times(1)).onError(errorArgumentCaptor.capture());
        assertTrue(errorArgumentCaptor.getValue().getThrowable() instanceof ElementOutOfBoundsException);
        verify(buildCallback, never()).onSuccess(anyString());
    }
}
