/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.canvas;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.shape.ActivityShape;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.ActivityView;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.impl.AbstractElementShape;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.graph.GraphRulesManager;
import org.kie.workbench.common.stunner.core.rule.model.ModelRulesManager;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementCanvasHandlerTest {

    @Mock
    private ClientDefinitionManager clientDefinitionManager;

    @Mock
    private ClientFactoryService clientFactoryServices;

    @Mock
    private GraphRulesManager graphRulesManager;

    @Mock
    private ModelRulesManager modelRulesManager;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionAdapter definitionAdapter;

    @Mock
    private PropertyAdapter propertyAdapter;

    @Mock
    private GraphUtils graphUtils;

    @Mock
    private GraphIndexBuilder<? extends MutableIndex<Node, Edge>> indexBuilder;

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private EventSourceMock<CanvasElementAddedEvent> canvasElementAddedEvent;

    @Mock
    private EventSourceMock<CanvasElementRemovedEvent> canvasElementRemovedEvent;

    @Mock
    private EventSourceMock<CanvasElementUpdatedEvent> canvasElementUpdatedEvent;

    @Mock
    private EventSourceMock<CanvasElementsClearEvent> canvasElementsClearEvent;

    @Mock
    private CanvasCommandFactory canvasCommandFactory;

    @Mock
    private CaseManagementCanvasPresenter canvas;

    @Mock
    private Layer layer;

    private CaseManagementCanvasHandler handler;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.handler = new CaseManagementCanvasHandler(clientDefinitionManager,
                                                       clientFactoryServices,
                                                       graphRulesManager,
                                                       modelRulesManager,
                                                       graphUtils,
                                                       indexBuilder,
                                                       shapeManager,
                                                       canvasElementAddedEvent,
                                                       canvasElementRemovedEvent,
                                                       canvasElementUpdatedEvent,
                                                       canvasElementsClearEvent,
                                                       canvasCommandFactory);
        this.handler.handle(canvas);
        when(canvas.getLayer()).thenReturn(layer);
    }

    @Test
    public void checkCanvasRoot() {
        assertFalse(handler.isCanvasRoot(mock(Element.class)));
    }

    @Test
    public void checkCanvasRootUUID() {
        assertFalse(handler.isCanvasRoot("any-uuid"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRegisterRenderableShapes() {
        final ActivityShape shape = makeShape();
        final Node<View<?>, Edge> node = makeNode("uuid",
                                                  shape);

        handler.register(shape,
                         node,
                         true);

        verify(canvas,
               times(1)).addShape(eq(shape));
        verify(canvas,
               times(1)).draw();
    }

    @SuppressWarnings("unchecked")
    private ActivityShape makeShape() {
        final RectangleShapeDef shapeDef = new CaseManagementTaskShapeDef();
        final ActivityView shapeView = new ActivityView(10.0,
                                                        20.0);
        final ActivityShape shape = new ActivityShape(shapeDef,
                                                      shapeView);
        return shape;
    }

    @SuppressWarnings("unchecked")
    private Node<View<?>, Edge> makeNode(final String uuid,
                                         final AbstractElementShape shape) {
        final Node<View<?>, Edge> node = new NodeImpl<>(uuid);
        node.setContent(new ViewImpl(shape.getShapeDefinition(),
                                     new BoundsImpl(new BoundImpl(0.0,
                                                                  0.0),
                                                    new BoundImpl(10.0,
                                                                  20.0))));
        when(canvas.getShape(eq(uuid))).thenReturn(shape);
        when(clientDefinitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(definitionAdapter.getMetaProperty(eq(PropertyMetaTypes.NAME),
                                               anyObject())).thenReturn(PropertyMetaTypes.NAME);
        when(propertyAdapter.getValue(eq(PropertyMetaTypes.NAME))).thenReturn("name");
        return node;
    }

    @Test
    public void checkDeregisterRenderableShapes() {
        final ActivityShape shape = makeShape();
        final Node<View<?>, Edge> node = makeNode("uuid",
                                                  shape);

        handler.deregister(shape,
                           node,
                           true);

        verify(canvas,
               times(1)).deleteShape(eq(shape));
        verify(canvas,
               times(1)).draw();
    }

    @Test
    public void checkAddShapeRenderableShapes() {
        final ActivityShape shape = makeShape();

        handler.addShape(shape);

        verify(canvas,
               times(1)).addShape(eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkAddChildRenderableShapes() {
        final ActivityShape parentShape = makeShape();
        final ActivityShape childShape = makeShape();
        final Node<View<?>, Edge> parentNode = makeNode("parent",
                                                        parentShape);
        final Node<View<?>, Edge> childNode = makeNode("child",
                                                       childShape);

        handler.register(parentShape,
                         parentNode,
                         true);
        handler.register(childShape,
                         childNode,
                         true);

        handler.addChild(parentNode,
                         childNode);

        verify(canvas,
               times(1)).addChildShape(eq(parentShape),
                                       eq(childShape));
        verify(layer,
               never()).addShape(eq(childShape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkAddChildRenderableShapesAtIndex() {
        final ActivityShape parentShape = makeShape();
        final ActivityShape childShape = makeShape();
        final Node<View<?>, Edge> parentNode = makeNode("parent",
                                                        parentShape);
        final Node<View<?>, Edge> childNode = makeNode("child",
                                                       childShape);

        handler.register(parentShape,
                         parentNode,
                         true);
        handler.register(childShape,
                         childNode,
                         true);

        handler.addChild(parentNode,
                         childNode,
                         0);

        verify(canvas,
               times(1)).addChildShape(eq(parentShape),
                                       eq(childShape),
                                       eq(0));
        verify(layer,
               never()).addShape(eq(childShape));
    }

    @Test
    public void checkRemoveShapeRenderableShapes() {
        final ActivityShape shape = makeShape();

        handler.removeShape(shape);

        verify(canvas,
               times(1)).deleteShape(eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRemoveChildRenderableShapes() {
        final ActivityShape parentShape = makeShape();
        final ActivityShape childShape = makeShape();
        final Node<View<?>, Edge> parentNode = makeNode("parent",
                                                        parentShape);
        final Node<View<?>, Edge> childNode = makeNode("child",
                                                       childShape);

        handler.register(parentShape,
                         parentNode,
                         true);
        handler.register(childShape,
                         childNode,
                         true);
        handler.addChild(parentNode,
                         childNode);

        handler.removeChild(parentNode,
                            childNode);

        verify(canvas,
               times(1)).deleteChildShape(parentShape,
                                          childShape);
        verify(layer,
               never()).removeShape(childShape);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkApplyElementMutationRenderableShapes() {
        final ActivityShape shape = spy(makeShape());
        final Node<View<?>, Edge> node = makeNode("uuid",
                                                  shape);
        final MutationContext mutationContext = mock(MutationContext.class);
        doNothing().when(shape).applyPosition(eq(node),
                                              eq(mutationContext));
        doNothing().when(shape).applyProperties(eq(node),
                                                eq(mutationContext));
        doNothing().when(shape).applyTitle(anyString(),
                                           any(Node.class),
                                           eq(mutationContext));

        handler.applyElementMutation(shape,
                                     node,
                                     true,
                                     true,
                                     mutationContext);

        verify(shape,
               times(1)).applyPosition(eq(node),
                                       eq(mutationContext));
        verify(shape,
               times(1)).applyProperties(eq(node),
                                         eq(mutationContext));
        verify(canvas,
               times(1)).draw();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkApplyShapeElementMutationRenderableShapes() {
        final ActivityShape shape = spy(makeShape());
        final Node<View<?>, Edge> node = makeNode("uuid",
                                                  shape);
        final MutationContext mutationContext = mock(MutationContext.class);
        doNothing().when(shape).applyPosition(eq(node),
                                              eq(mutationContext));
        doNothing().when(shape).applyProperties(eq(node),
                                                eq(mutationContext));
        doNothing().when(shape).applyTitle(anyString(),
                                           any(Node.class),
                                           eq(mutationContext));

        handler.register(shape,
                         node,
                         true);
        verify(canvas,
               times(1)).draw();

        handler.applyElementMutation(node,
                                     true,
                                     true,
                                     mutationContext);

        verify(shape,
               times(1)).applyPosition(eq(node),
                                       eq(mutationContext));
        verify(shape,
               times(1)).applyProperties(eq(node),
                                         eq(mutationContext));
        verify(canvas,
               times(2)).draw();
    }
}
