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

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.client.canvas.TransformImpl;
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
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
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
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.ParameterizedCommand;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ObserverBuilderControlTest {

    private static final String DEF = "def";
    private static final String DEF_ID = "def_id";

    @Mock
    private ClientDefinitionManager clientDefinitionManager;

    @Mock
    private ClientFactoryService clientFactoryServices;

    @Mock
    private GraphUtils graphUtils;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private GraphBoundsIndexer graphBoundsIndexer;

    @Mock
    private GraphIndexBuilder graphIndexBuilder;

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    private ObserverBuilderControl tested;
    private AbstractCanvasHandler canvasHandler;
    private AbstractCanvas canvas;
    private Transform canvasTransform;
    private AbstractCanvas.CanvasView canvasView;
    private CanvasPanel canvasPanel;
    private Widget canvasWidget;
    private com.google.gwt.user.client.Element canvasElement;
    private Document document;
    private View<Object> view;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                ServiceCallback<Node<View<Object>, Edge>> callback = (ServiceCallback<Node<View<Object>, Edge>>) (invocationOnMock.getArguments()[2]);
                view = new ViewImpl<>(new Object(), Bounds.create(0.0, 0.0, 10.0, 20.0));
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
        when(definitionAdapter.getId(any())).thenReturn(DefinitionId.build("Object"));
        when(rulesAdapter.getRuleSet(any())).thenReturn(mock(RuleSet.class));
        when(adapters.forDefinition()).thenReturn(definitionAdapter);
        when(adapters.forRules()).thenReturn(rulesAdapter);
        when(definitionAdapter.getId(DEF)).thenReturn(DefinitionId.build(DEF_ID));

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

        tested = new ObserverBuilderControl(clientDefinitionManager,
                                            clientFactoryServices,
                                            ruleManager,
                                            canvasCommandFactory,
                                            mock(ClientTranslationMessages.class),
                                            graphBoundsIndexer,
                                            mock(EventSourceMock.class));

        Diagram diagram = mock(Diagram.class);
        Metadata metadata = mock(Metadata.class);

        when(metadata.getCanvasRootUUID()).thenReturn("ID");
        when(diagram.getMetadata()).thenReturn(metadata);
        MutableIndex index = mock(MutableIndex.class);
        Graph graph = mock(Graph.class);
        DefinitionSet graphContent = mock(DefinitionSet.class);
        when(graph.getContent()).thenReturn(graphContent);
        when(index.getGraph()).thenReturn(graph);

        when(graphIndexBuilder.build(any(Graph.class))).thenReturn(index);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.emptyList());

        CanvasCommandManager commandManager = mock(CanvasCommandManager.class);
        doAnswer(invocationOnMock -> {
            Command command = (Command) invocationOnMock.getArguments()[1];
            command.execute(invocationOnMock.getArguments()[0]);
            return null;
        }).when(commandManager).execute(any(), any(Command.class));
        when(commandManagerProvider.getCommandManager()).thenReturn(commandManager);

        canvas = mock(AbstractCanvas.class);
        canvasTransform = spy(new TransformImpl(new Point2D(0, 0), new Point2D(1, 1)));
        canvasView = mock(AbstractCanvas.CanvasView.class);
        canvasWidget = mock(Widget.class);
        canvasElement = mock(com.google.gwt.user.client.Element.class);
        document = mock(Document.class);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvas.getTransform()).thenReturn(canvasTransform);
        when(canvasView.asWidget()).thenReturn(canvasWidget);
        when(canvasWidget.getElement()).thenReturn(canvasElement);
        when(canvasElement.getOwnerDocument()).thenReturn(document);
        canvasPanel = mock(CanvasPanel.class);
        when(canvasView.getPanel()).thenReturn(canvasPanel);
        canvasHandler = new CanvasHandlerImpl(clientDefinitionManager,
                                              canvasCommandFactory,
                                              clientFactoryServices,
                                              ruleManager,
                                              graphUtils,
                                              graphIndexBuilder,
                                              shapeManager,
                                              mock(TextPropertyProviderFactory.class),
                                              mock(EventSourceMock.class),
                                              null,
                                              null,
                                              null);
        canvasHandler.handle(canvas);
        canvasHandler.draw(diagram, mock(ParameterizedCommand.class));

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
                assertEquals(15.0, view.getBounds().getLowerRight().getX(), 0.00001);
                assertEquals(5.0, view.getBounds().getUpperLeft().getX(), 0.00001);
                assertEquals(25.0, view.getBounds().getLowerRight().getY(), 0.00001);
                assertEquals(5.0, view.getBounds().getUpperLeft().getY(), 0.00001);
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
    public void testTransformedLocation() {
        Point2D location1 = tested.getTransformedLocation(33.45d, 554.6d);
        assertEquals(33.45d, location1.getX(), 0d);
        assertEquals(554.6d, location1.getY(), 0d);
    }

    @Test
    public void testTransformedLocationLeftScrolled() {
        when(canvasElement.getScrollLeft()).thenReturn(43);
        when(document.getScrollLeft()).thenReturn(11);
        Point2D location1 = tested.getTransformedLocation(33.45d, 554.6d);
        assertEquals(87.45d, location1.getX(), 0d);
        assertEquals(554.6d, location1.getY(), 0d);
    }

    @Test
    public void testTransformedLocationTopScrolled() {
        when(canvasElement.getScrollTop()).thenReturn(4);
        when(document.getScrollTop()).thenReturn(11);
        Point2D location1 = tested.getTransformedLocation(33.45d, 554.6d);
        assertEquals(33.45d, location1.getX(), 0d);
        assertEquals(569.6d, location1.getY(), 0d);
    }

    @Test
    public void testTransformedLocationWithPan() {
        when(canvasTransform.inverse(anyDouble(), anyDouble())).thenAnswer((invocation -> new Point2D(invocation.getArgumentAt(0, Double.class) + 5, invocation.getArgumentAt(1, Double.class) - 10)));
        Point2D location1 = tested.getTransformedLocation(0d, 0d);
        assertEquals(5d, location1.getX(), 0d);
        assertEquals(-10d, location1.getY(), 0d);
        Point2D location2 = tested.getTransformedLocation(5d, 15d);
        assertEquals(10d, location2.getX(), 0d);
        assertEquals(5d, location2.getY(), 0d);
    }

    @Test
    public void testTransformedLocationWithZoom() {
        when(canvasTransform.inverse(anyDouble(), anyDouble())).thenAnswer((invocation -> new Point2D(invocation.getArgumentAt(0, Double.class) * 0.5, invocation.getArgumentAt(1, Double.class) * 2)));
        Point2D location1 = tested.getTransformedLocation(0d, 0d);
        assertEquals(0d, location1.getX(), 0d);
        assertEquals(0d, location1.getY(), 0d);
        Point2D location2 = tested.getTransformedLocation(5d, 15d);
        assertEquals(2.5d, location2.getX(), 0d);
        assertEquals(30d, location2.getY(), 0d);
    }

    @Test
    public void testTransformedLocationWithPanAndZoom() {
        when(canvasTransform.inverse(anyDouble(), anyDouble())).thenAnswer((invocation -> new Point2D((invocation.getArgumentAt(0, Double.class) + 5) * 0.5, (invocation.getArgumentAt(1, Double.class) - 5) * 2)));
        Point2D location1 = tested.getTransformedLocation(0d, 0d);
        assertEquals(2.5d, location1.getX(), 0d);
        assertEquals(-10d, location1.getY(), 0d);
        Point2D location2 = tested.getTransformedLocation(5d, 15d);
        assertEquals(5d, location2.getX(), 0d);
        assertEquals(20d, location2.getY(), 0d);
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
