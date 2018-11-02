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

package org.kie.workbench.common.stunner.cm.client.canvas;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.cm.client.shape.CaseManagementShape;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
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
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementCanvasHandlerTest {

    @Mock
    private ClientDefinitionManager clientDefinitionManager;

    @Mock
    private ClientFactoryService clientFactoryServices;

    @Mock
    private RuleManager ruleManager;

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
    private TextPropertyProviderFactory textPropertyProviderFactory;

    @Mock
    private TextPropertyProvider textPropertyProvider;

    @Mock
    private EventSourceMock<CanvasElementAddedEvent> canvasElementAddedEvent;

    @Mock
    private EventSourceMock<CanvasElementRemovedEvent> canvasElementRemovedEvent;

    @Mock
    private EventSourceMock<CanvasElementUpdatedEvent> canvasElementUpdatedEvent;

    @Mock
    private EventSourceMock<CanvasElementsClearEvent> canvasElementsClearEvent;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

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
                                                       ruleManager,
                                                       graphUtils,
                                                       indexBuilder,
                                                       shapeManager,
                                                       textPropertyProviderFactory,
                                                       canvasElementAddedEvent,
                                                       canvasElementRemovedEvent,
                                                       canvasElementUpdatedEvent,
                                                       canvasElementsClearEvent,
                                                       canvasCommandFactory);
        this.handler.handle(canvas);
        when(canvas.getLayer()).thenReturn(layer);
        when(textPropertyProviderFactory.getProvider(any(Element.class))).thenReturn(textPropertyProvider);
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
        final CaseManagementShape shape = makeShape();
        final Node<View<BPMNViewDefinition>, Edge> node = makeNode("uuid",
                                                                   shape);

        handler.register(shape,
                         node,
                         true);

        verify(canvas,
               times(1)).addShape(eq(shape));
    }

    @SuppressWarnings("unchecked")
    private CaseManagementShape makeShape() {
        final CaseManagementShapeView shapeView = mock(CaseManagementShapeView.class);
        return new CaseManagementShape(shapeView);
    }

    @SuppressWarnings("unchecked")
    private Node<View<BPMNViewDefinition>, Edge> makeNode(final String uuid,
                                                          final CaseManagementShape shape) {
        final Node<View<BPMNViewDefinition>, Edge> node = new NodeImpl<>(uuid);
        node.setContent(new ViewImpl(new CaseManagementDiagram(),
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
        final CaseManagementShape shape = makeShape();
        final Node<View<BPMNViewDefinition>, Edge> node = makeNode("uuid",
                                                                   shape);

        handler.deregister(shape,
                           node,
                           true);

        verify(canvas,
               times(1)).deleteShape(eq(shape));
    }

    @Test
    public void checkAddShapeRenderableShapes() {
        final CaseManagementShape shape = makeShape();

        handler.addShape(shape);

        verify(canvas,
               times(1)).addShape(eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkAddChildRenderableShapes() {
        final CaseManagementShape parentShape = makeShape();
        final CaseManagementShape childShape = makeShape();
        final Node<View<BPMNViewDefinition>, Edge> parentNode = makeNode("parent",
                                                                         parentShape);
        final Node<View<BPMNViewDefinition>, Edge> childNode = makeNode("child",
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
        final CaseManagementShape parentShape = makeShape();
        final CaseManagementShape childShape = makeShape();
        final Node<View<BPMNViewDefinition>, Edge> parentNode = makeNode("parent",
                                                                         parentShape);
        final Node<View<BPMNViewDefinition>, Edge> childNode = makeNode("child",
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
        final CaseManagementShape shape = makeShape();

        handler.removeShape(shape);

        verify(canvas,
               times(1)).deleteShape(eq(shape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRemoveChildRenderableShapes() {
        final CaseManagementShape parentShape = makeShape();
        final CaseManagementShape childShape = makeShape();
        final Node<View<BPMNViewDefinition>, Edge> parentNode = makeNode("parent",
                                                                         parentShape);
        final Node<View<BPMNViewDefinition>, Edge> childNode = makeNode("child",
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
        final CaseManagementShape shape = spy(makeShape());
        final Node<View<BPMNViewDefinition>, Edge> node = makeNode("uuid",
                                                                   shape);
        final MutationContext mutationContext = mock(MutationContext.class);

        final String name = "mockName";
        when(textPropertyProvider.getText(eq(node))).thenReturn(name);

        handler.applyElementMutation(shape,
                                     node,
                                     true,
                                     true,
                                     mutationContext);

        verify(shape.getShapeView(),
               times(1)).refresh();

        verify((CaseManagementShapeView) shape.getShapeView(),
               times(1)).setLabel(eq(name));

        verify(shape,
               times(1)).beforeDraw();

        verify(shape,
               times(1)).afterDraw();

        verify(canvasElementUpdatedEvent,
               times(1)).fire(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkApplyShapeElementMutationRenderableShapes() {
        final CaseManagementShape shape = spy(makeShape());
        final Node<View<BPMNViewDefinition>, Edge> node = makeNode("uuid",
                                                                   shape);
        final MutationContext mutationContext = mock(MutationContext.class);

        final String name = "mockName";
        when(textPropertyProvider.getText(eq(node))).thenReturn(name);

        handler.register(shape,
                         node,
                         true);
        handler.applyElementMutation(node,
                                     true,
                                     true,
                                     mutationContext);

        verify(shape.getShapeView(),
               times(1)).refresh();

        verify((CaseManagementShapeView) shape.getShapeView(),
               times(1)).setLabel(eq(name));

        verify(shape,
               times(1)).beforeDraw();

        verify(shape,
               times(1)).afterDraw();

        verify(canvasElementUpdatedEvent,
               times(1)).fire(any());
    }
}
