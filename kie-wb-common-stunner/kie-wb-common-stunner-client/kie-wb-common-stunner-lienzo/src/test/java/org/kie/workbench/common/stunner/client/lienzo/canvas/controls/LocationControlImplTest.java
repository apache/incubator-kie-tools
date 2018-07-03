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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.SelectionManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateControlPointPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.ShapeLocationsChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseEnterHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPointImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LocationControlImplTest {

    private static final String ROOT_UUID = "root-uuid1";
    private static final String ELEMENT_UUID = "element-uuid1";
    private static final String DEF_ID = "def-id";
    private static final BoundsImpl GRAPH_BOUNDS = new BoundsImpl(
            new BoundImpl(1d,
                          2d),
            new BoundImpl(3000d,
                          4000d)
    );
    private static final BoundsImpl ELEMENT_BOUNDS = new BoundsImpl(
            new BoundImpl(10d,
                          20d),
            new BoundImpl(30d,
                          40d)
    );

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private WiresCanvas canvas;

    @Mock
    private Layer layer;

    @Mock
    private WiresManager wiresManager;

    @Mock
    private SelectionManager selectionManager;

    @Mock
    private WiresCompositeControl wiresCompositeControl;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Index graphIndex;

    @Mock
    private DefinitionSet graphContent;

    @Mock
    private Metadata metadata;

    @Mock
    private Node element;

    @Mock
    private View elementContent;

    @Mock
    private Shape<ShapeView> shape;

    @Mock
    private HasEventHandlers<ShapeViewExtStub, Object> shapeEventHandler;

    @Mock
    private HasControlPoints<ShapeViewExtStub> hasControlPoints;

    @Mock
    private Object definition;

    @Mock
    private EventSourceMock<ShapeLocationsChangedEvent> shapeLocationsChangedEvent;

    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private ShapeViewExtStub shapeView;
    private LocationControlImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.canvasCommandFactory = new DefaultCanvasCommandFactory(null, null);

        this.shapeView = spy(new ShapeViewExtStub(shapeEventHandler,
                                                  hasControlPoints));

        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(element.asNode()).thenReturn(element);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.get(eq(ELEMENT_UUID))).thenReturn(element);
        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(definition);
        when(elementContent.getBounds()).thenReturn(ELEMENT_BOUNDS);
        when(graph.getContent()).thenReturn(graphContent);
        when(graphContent.getBounds()).thenReturn(GRAPH_BOUNDS);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getLayer()).thenReturn(layer);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(canvas.getWiresManager()).thenReturn(wiresManager);
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);
        when(shapeEventHandler.supports(any(ViewEventType.class))).thenReturn(true);
        when(wiresManager.getSelectionManager()).thenReturn(selectionManager);
        when(selectionManager.getControl()).thenReturn(wiresCompositeControl);

        this.tested = new LocationControlImpl(canvasCommandFactory, shapeLocationsChangedEvent);
        tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testInit() {
        tested.init(canvasHandler);
        ArgumentCaptor<ILocationAcceptor> locationAcceptorArgumentCaptor = ArgumentCaptor.forClass(ILocationAcceptor.class);
        verify(wiresManager, times(1)).setLocationAcceptor(locationAcceptorArgumentCaptor.capture());
        assertEquals(tested.LOCATION_ACCEPTOR, locationAcceptorArgumentCaptor.getValue());
    }

    @Test
    public void testRegisterAndSetBounds() {
        tested.init(canvasHandler);
        assertFalse(tested.isRegistered(element));
        tested.register(element);
        assertTrue(tested.isRegistered(element));
        verify(shapeView, times(1)).setDragEnabled(eq(true));
        ArgumentCaptor<BoundingBox> bbCaptor = ArgumentCaptor.forClass(BoundingBox.class);
        verify(wiresCompositeControl, times(1)).setBoundsConstraint(bbCaptor.capture());
        BoundingBox bb = bbCaptor.getValue();
        assertEquals(1.0d, bb.getMinX(), 0d);
        assertEquals(2.0d, bb.getMinY(), 0d);
        assertEquals(3000.0d, bb.getMaxX(), 0d);
        assertEquals(4000.0d, bb.getMaxY(), 0d);
        verify(shapeView, times(1)).setDragBounds(1.0d, 2.0d, 3000d, 4000d);
        verify(shapeEventHandler,
               times(1)).supports(eq(ViewEventType.MOUSE_ENTER));
        verify(shapeEventHandler,
               times(1)).addHandler(eq(ViewEventType.MOUSE_ENTER),
                                    any(MouseEnterHandler.class));
        verify(shapeEventHandler,
               times(1)).supports(eq(ViewEventType.MOUSE_EXIT));
        verify(shapeEventHandler,
               times(1)).addHandler(eq(ViewEventType.MOUSE_EXIT),
                                    any(MouseEnterHandler.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMove() throws Exception {
        final WiresShapeView wiresShape = mock(WiresShapeView.class);
        final WiresShapeView childWiresShape = mock(WiresShapeView.class);
        final MagnetManager.Magnets magnets = mock(MagnetManager.Magnets.class);
        final WiresMagnet magnet = mock(WiresMagnet.class);
        final WiresConnection connection = mock(WiresConnection.class);
        final NFastArrayList<WiresConnection> connections = new NFastArrayList<>(connection);
        final WiresConnector connector = mock(WiresConnector.class);
        final String connectorUUID = UUID.uuid();
        final NFastArrayList<WiresShape> children = new NFastArrayList<>(childWiresShape);
        final Group connectorGroup = mock(Group.class);
        final Edge connectorEdge = mock(Edge.class);
        final com.ait.lienzo.client.core.types.Point2D controlPointLienzo = new com.ait.lienzo.client.core.types.Point2D(100, 100);
        final Point2DArray controlPointsLienzo = new Point2DArray(controlPointLienzo);
        final ControlPoint controlPoint = new ControlPointImpl(new Point2D(0, 0), 0);
        final List<ControlPoint> controlPoints = Arrays.asList(controlPoint);
        final ViewConnector viewConnector = mock(ViewConnector.class);
        Group parentGroup = mock(Group.class);
        BoundingBox parentBB = new BoundingBox(0, 0, 200, 200);
        MultiPath head = mock(MultiPath.class);
        MultiPath tail = mock(MultiPath.class);

        when(childWiresShape.getMagnets()).thenReturn(magnets);
        when(childWiresShape.getParent()).thenReturn(wiresShape);
        when(wiresShape.getGroup()).thenReturn(parentGroup);
        when(parentGroup.getBoundingBox()).thenReturn(parentBB);
        when(wiresShape.getX()).thenReturn(0d);
        when(wiresShape.getY()).thenReturn(0d);
        when(magnets.size()).thenReturn(1);
        when(magnets.getMagnet(0)).thenReturn(magnet);
        when(magnet.getConnectionsSize()).thenReturn(connections.size());
        when(magnet.getConnections()).thenReturn(connections);
        when(connection.getConnector()).thenReturn(connector);
        when(connector.getGroup()).thenReturn(connectorGroup);
        when(connectorGroup.uuid()).thenReturn(connectorUUID);
        when(connector.getControlPoints()).thenReturn(controlPointsLienzo);
        when(connector.getHead()).thenReturn(head);
        when(connector.getTail()).thenReturn(tail);
        when(head.getLocation()).thenReturn(new com.ait.lienzo.client.core.types.Point2D(1, 1));
        when(tail.getLocation()).thenReturn(new com.ait.lienzo.client.core.types.Point2D(2, 2));
        when(wiresShape.getChildShapes()).thenReturn(children);
        when(shape.getShapeView()).thenReturn(wiresShape);
        when(graphIndex.getEdge(connectorUUID)).thenReturn(connectorEdge);
        when(connectorEdge.getContent()).thenReturn(viewConnector);
        when(viewConnector.getControlPoints()).thenReturn(controlPoints);
        when(connectorGroup.getUserData()).thenReturn(new WiresUtils.UserData(connectorUUID, ""));

        tested.init(canvasHandler);
        tested.register(element);
        Point2D location = new Point2D(45d, 65.5d);
        tested.move(new Element[]{element}, new Point2D[]{location});
        ArgumentCaptor<CanvasCommand> commandArgumentCaptor = ArgumentCaptor.forClass(CanvasCommand.class);
        verify(commandManager, times(1)).execute(eq(canvasHandler),
                                                 commandArgumentCaptor.capture());

        ArgumentCaptor<ShapeLocationsChangedEvent> shapeLocationsChangedEventCaptor = ArgumentCaptor.forClass(ShapeLocationsChangedEvent.class);
        verify(shapeLocationsChangedEvent, times(1)).fire(shapeLocationsChangedEventCaptor.capture());
        assertTrue(shapeLocationsChangedEventCaptor.getValue() instanceof ShapeLocationsChangedEvent);

        //assert parent node move
        final CompositeCommand command = (CompositeCommand) commandArgumentCaptor.getValue();
        UpdateElementPositionCommand updateElementPositionCommand = (UpdateElementPositionCommand) command.getCommands().get(0);
        assertEquals(element, updateElementPositionCommand.getElement());
        assertEquals(location, updateElementPositionCommand.getLocation());

        //assert child connector control point move
        final UpdateControlPointPositionCommand controlPointCommand = (UpdateControlPointPositionCommand) command.getCommands().get(1);
        assertEquals(controlPoint, controlPointCommand.getControlPoint());
        assertEquals(new Point2D(controlPointLienzo.getX(), controlPointLienzo.getY()), controlPointCommand.getPosition());
        assertEquals(connectorEdge, controlPointCommand.getCandidate());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLocationAcceptor() {
        tested.init(canvasHandler);
        tested.register(element);
        ArgumentCaptor<ILocationAcceptor> locationAcceptorArgumentCaptor = ArgumentCaptor.forClass(ILocationAcceptor.class);
        verify(wiresManager, times(1)).setLocationAcceptor(locationAcceptorArgumentCaptor.capture());
        final ILocationAcceptor locationAcceptor = locationAcceptorArgumentCaptor.getValue();
        final WiresShapeView wiresContainer = mock(WiresShapeView.class);
        when(wiresContainer.getUUID()).thenReturn(ELEMENT_UUID);
        final com.ait.lienzo.client.core.types.Point2D point = new com.ait.lienzo.client.core.types.Point2D(40d, 50d);
        locationAcceptor.accept(new WiresContainer[]{wiresContainer},
                                new com.ait.lienzo.client.core.types.Point2D[]{point});
        ArgumentCaptor<CanvasCommand> commandArgumentCaptor = ArgumentCaptor.forClass(CanvasCommand.class);
        verify(commandManager, times(1)).execute(eq(canvasHandler),
                                                 commandArgumentCaptor.capture());

        final CompositeCommand command = (CompositeCommand) commandArgumentCaptor.getValue();
        UpdateElementPositionCommand updateElementPositionCommand = (UpdateElementPositionCommand) command.getCommands().get(0);
        assertEquals(element, updateElementPositionCommand.getElement());
        assertEquals(new Point2D(40d, 50d), updateElementPositionCommand.getLocation());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeregister() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.deregister(element);
        verify(shapeEventHandler,
               times(1)).removeHandler(any(ViewHandler.class));
        assertFalse(tested.isRegistered(element));
    }

    @Test
    public void testClear() {
        tested.init(canvasHandler);
        tested.clear();
        verify(selectionManager).getControl();
        verify(wiresCompositeControl).setBoundsConstraint(null);
    }

    @Test
    public void testDestroy() {
        tested.init(canvasHandler);
        tested.destroy();
        verify(selectionManager,
               times(2)).getControl();
        verify(wiresCompositeControl,
               times(2)).setBoundsConstraint(null);
        verify(wiresManager).setLocationAcceptor(ILocationAcceptor.ALL);
    }
}