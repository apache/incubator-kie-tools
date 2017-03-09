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

package org.kie.workbench.common.stunner.cm.client.canvas.controls.containment;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILayoutHandler;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvasHandler;
import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvasView;
import org.kie.workbench.common.stunner.cm.client.command.CaseManagementCanvasCommandFactory;
import org.kie.workbench.common.stunner.cm.client.wires.CaseManagementContainmentStateHolder;
import org.kie.workbench.common.stunner.cm.client.wires.MockCaseManagementShape;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementContainmentAcceptorControlImplTest {

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private CaseManagementCanvasView canvasView;

    @Mock
    private CaseManagementCanvasHandler canvasHandler;

    @Mock
    private Index graphIndex;

    @Mock
    private CaseManagementCanvasCommandFactory canvasCommandFactory;

    @Mock
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Captor
    private ArgumentCaptor<IContainmentAcceptor> containmentAcceptorArgumentCaptor;

    private CaseManagementContainmentStateHolder state;

    private CaseManagementContainmentAcceptorControlImpl control;

    @Before
    public void setup() {
        this.state = new CaseManagementContainmentStateHolder();
        this.control = new CaseManagementContainmentAcceptorControlImpl(canvasCommandFactory,
                                                                        state);
        this.control.setCommandManagerProvider(commandManagerProvider);

        when(commandManagerProvider.getCommandManager()).thenReturn(commandManager);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.getNode(anyString())).thenReturn(mock(Node.class));
        when(canvas.getView()).thenReturn(canvasView);
    }

    @Test
    public void checkDoEnable() {
        control.doEnable(canvasView);

        final IContainmentAcceptor containmentAcceptor = getContainmentAcceptor();
        assertNotNull(containmentAcceptor);
        assertTrue(containmentAcceptor instanceof CaseManagementContainmentAcceptorControlImpl.CanvasManagementContainmentAcceptor);
    }

    private IContainmentAcceptor getContainmentAcceptor() {
        verify(canvasView,
               times(1)).setContainmentAcceptor(containmentAcceptorArgumentCaptor.capture());
        final IContainmentAcceptor containmentAcceptor = containmentAcceptorArgumentCaptor.getValue();
        return containmentAcceptor;
    }

    @Test
    public void checkDoDisable() {
        control.doDisable(canvasView);

        verify(canvasView,
               times(1)).setContainmentAcceptor(eq(IContainmentAcceptor.NONE));
    }

    @Test
    public void isEdgeAcceptedWhenView() {
        final Edge<View, Node> view = new EdgeImpl<>("view");
        view.setContent(mock(View.class));

        assertFalse(control.isEdgeAccepted(view));
    }

    @Test
    public void isEdgeAcceptedWhenChild() {
        final Edge<Child, Node> view = new EdgeImpl<>("view");
        view.setContent(mock(Child.class));

        assertTrue(control.isEdgeAccepted(view));
    }

    @Test
    public void getAddEdgeCommand() {
        final Node parent = mock(Node.class);
        final Node child = mock(Node.class);

        control.getAddEdgeCommand(parent,
                                  child);

        verify(canvasCommandFactory,
               times(1)).setChildNode(eq(parent),
                                      eq(child));
    }

    @Test
    public void getSetEdgeCommand() {
        final Node parent = mock(Node.class);
        final Node child = mock(Node.class);

        control.getSetEdgeCommand(parent,
                                  child,
                                  Optional.empty(),
                                  Optional.empty(),
                                  Optional.empty());

        verify(canvasCommandFactory,
               times(1)).setChildNode(eq(parent),
                                      eq(child),
                                      eq(Optional.empty()),
                                      eq(Optional.empty()),
                                      eq(Optional.empty()));
    }

    @Test
    public void getDeleteEdgeCommand() {
        final Node parent = mock(Node.class);
        final Node child = mock(Node.class);

        control.getDeleteEdgeCommand(parent,
                                     child);

        verify(canvasCommandFactory,
               times(1)).removeChild(eq(parent),
                                     eq(child));
    }

    @Test
    public void checkContainmentAllowed() {
        control.enable(canvasHandler);

        final IContainmentAcceptor containmentAcceptor = getContainmentAcceptor();
        final WiresShape parentShape = makeWiresShape();
        final WiresShape childShape = makeWiresShape();

        assertTrue(containmentAcceptor.containmentAllowed(parentShape,
                                                          childShape));

        verify(canvasCommandFactory,
               times(1)).setChildNode(any(Node.class),
                                      any(Node.class));
    }

    private WiresShape makeWiresShape() {
        final WiresShape shape = new MockCaseManagementShape();
        WiresUtils.assertShapeGroup(shape.getGroup(),
                                    WiresCanvas.WIRES_CANVAS_GROUP_ID);
        return shape;
    }

    @Test
    public void checkAcceptContainment() {
        control.enable(canvasHandler);

        final IContainmentAcceptor containmentAcceptor = getContainmentAcceptor();
        final WiresShape parentShape = makeWiresShape();
        final WiresShape childShape = makeWiresShape();

        assertTrue(containmentAcceptor.acceptContainment(parentShape,
                                                         childShape));

        assertTrue(parentShape.getLayoutHandler() instanceof CaseManagementContainmentAcceptorControlImpl.InterceptingLayoutHandler);
    }

    @Test
    public void checkInterceptingLayoutHandlerAdd() {
        control.enable(canvasHandler);

        final WiresShape parentShape = makeWiresShape();
        final WiresShape childShape = makeWiresShape();
        final ILayoutHandler layoutHandler = getILayoutHandler(parentShape,
                                                               childShape);

        layoutHandler.add(childShape,
                          parentShape,
                          new Point2D());

        verify(canvasCommandFactory,
               times(1)).setChildNode(any(Node.class),
                                      any(Node.class),
                                      eq(Optional.of(0)),
                                      eq(Optional.empty()),
                                      eq(Optional.empty()));

        assertTrue(parentShape.getLayoutHandler() instanceof ILayoutHandler.DefaultLayoutHandler);
    }

    private ILayoutHandler getILayoutHandler(final WiresContainer parentShape,
                                             final WiresShape childShape) {
        final IContainmentAcceptor containmentAcceptor = getContainmentAcceptor();

        containmentAcceptor.acceptContainment(parentShape,
                                              childShape);
        return parentShape.getLayoutHandler();
    }

    @Test
    public void checkInterceptingLayoutHandlerRemove() {
        control.enable(canvasHandler);

        final WiresShape parentShape = makeWiresShape();
        final WiresShape childShape = makeWiresShape();
        final ILayoutHandler layoutHandler = getILayoutHandler(parentShape,
                                                               childShape);

        layoutHandler.remove(childShape,
                             parentShape);

        assertTrue(parentShape.getLayoutHandler() instanceof ILayoutHandler.DefaultLayoutHandler);
    }
}
