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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.SelectionListener;
import com.ait.lienzo.client.core.shape.wires.SelectionManager;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresShapeViewExt;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MapSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.ShapeLocationsChangedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoMultipleSelectionControlTest {

    private static final String ELEMENT_UUID = "element-uuid1";
    private final static MultiPath PATH = new MultiPath();

    private final static double MIN_WIDTH = 0D;
    private final static double MIN_HEIGHT = 0D;
    private final static double MAX_WIDTH = 100D;
    private final static double MAX_HEIGHT = 100D;
    private final static double PADDING = SelectionManager.SELECTION_PADDING;

    private static ViewEventType[] viewEventTypes = {};

    @Mock
    private EventSourceMock<CanvasSelectionEvent> canvasSelectionEvent;

    @Mock
    private EventSourceMock<CanvasClearSelectionEvent> clearSelectionEvent;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresCanvasView canvasView;

    @Mock
    private com.ait.lienzo.client.core.shape.Layer lienzoLayer;

    @Mock
    private WiresLayer wiresLayer;

    @Mock
    private com.ait.lienzo.client.core.shape.Layer overLayer;

    @Mock
    private WiresManager wiresManager;

    @Mock
    private SelectionManager selectionManager;

    @Mock
    private SelectionManager.SelectedItems selectedItems;

    @Mock
    private Object definition;

    @Mock
    private Element element;

    @Mock
    private Shape<ShapeView> shape;

    @Mock
    private MapSelectionControl<AbstractCanvasHandler> selectionControl;

    @Mock
    private SelectionManager.SelectionShapeProvider delegateShapeProvider;

    @Mock
    private ShapeLocationsChangedEvent shapeLocationsChangedEvent;

    private LienzoMultipleSelectionControl<AbstractCanvasHandler> tested;
    private SelectionListener selectionListener;
    private LienzoMultipleSelectionControl.CursoredSelectionShapeProvider selectionShapeProvider;
    private WiresShapeViewExt shapeView;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {

        when(wiresManager.getLayer()).thenReturn(wiresLayer);
        when(wiresManager.enableSelectionManager()).thenReturn(selectionManager);
        when(selectionManager.setSelectionListener(any(SelectionListener.class))).thenReturn(selectionManager);
        when(selectionManager.setSelectionShapeProvider(any(SelectionManager.SelectionShapeProvider.class))).thenReturn(selectionManager);
        when(wiresLayer.getLayer()).thenReturn(lienzoLayer);
        when(lienzoLayer.getOverLayer()).thenReturn(overLayer);
        when(wiresManager.getSelectionManager()).thenReturn(selectionManager);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                selectionListener = (SelectionListener) invocationOnMock.getArguments()[0];
                return null;
            }
        }).when(selectionManager).setSelectionListener(any(SelectionListener.class));
        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(element.getContent()).thenReturn(new ViewImpl<>(definition,
                                                             Bounds.create(0, 0, 10, 10)));
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvas.getWiresManager()).thenReturn(wiresManager);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(selectionControl.getCanvasHandler()).thenReturn(canvasHandler);
        when(selectionControl.getCanvas()).thenReturn(canvas);
        shapeView = new WiresShapeViewExt<>(viewEventTypes,
                                            PATH);
        shapeView.setUUID(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);
        selectionShapeProvider = spy(new LienzoMultipleSelectionControl.CursoredSelectionShapeProvider(delegateShapeProvider,
                                                                                                       () -> canvasHandler));
        tested = spy(new LienzoMultipleSelectionControl<>(selectionControl,
                                                          canvasSelectionEvent,
                                                          clearSelectionEvent,
                                                          selectionShapeProvider));
    }

    @Test
    public void testInit() {
        tested.init(canvasHandler);
        verify(selectionControl, times(1)).init(eq(canvasHandler));
        verify(selectionManager, times(1)).setSelectionShapeProvider(eq(selectionShapeProvider));
        assertNotNull(selectionListener);
    }

    @Test
    public void testCustomSelectionShapeProvider() {
        final com.ait.lienzo.client.core.shape.Shape shape = mock(com.ait.lienzo.client.core.shape.Shape.class);
        final HandlerRegistration enterHandlerReg = mock(HandlerRegistration.class);
        final HandlerRegistration exitHandlerReg = mock(HandlerRegistration.class);
        when(delegateShapeProvider.getShape()).thenReturn(shape);
        when(shape.addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class))).thenReturn(enterHandlerReg);
        when(shape.addNodeMouseExitHandler(any(NodeMouseExitHandler.class))).thenReturn(exitHandlerReg);
        // Test location.
        final Point2D location = new Point2D(10d, 20d);
        delegateShapeProvider.setLocation(location);
        verify(delegateShapeProvider, times(1)).setLocation(eq(location));
        // Test build & event handlers.
        assertEquals(selectionShapeProvider, selectionShapeProvider.build());
        verify(delegateShapeProvider, times(1)).build();
        ArgumentCaptor<NodeMouseEnterHandler> enterHandlerArgumentCaptor = ArgumentCaptor.forClass(NodeMouseEnterHandler.class);
        ArgumentCaptor<NodeMouseExitHandler> enterExitArgumentCaptor = ArgumentCaptor.forClass(NodeMouseExitHandler.class);
        verify(shape, times(1)).addNodeMouseEnterHandler(enterHandlerArgumentCaptor.capture());
        verify(shape, times(1)).addNodeMouseExitHandler(enterExitArgumentCaptor.capture());
        enterHandlerArgumentCaptor.getValue().onNodeMouseEnter(mock(NodeMouseEnterEvent.class));
        verify(canvasView, times(1)).setCursor(eq(AbstractCanvas.Cursors.MOVE));
        enterExitArgumentCaptor.getValue().onNodeMouseExit(mock(NodeMouseExitEvent.class));
        verify(canvasView, times(1)).setCursor(eq(AbstractCanvas.Cursors.DEFAULT));
        // Test shape move to top.
        selectionShapeProvider.moveShapeToTop();
        verify(shape, times(1)).moveToTop();
        // Test clear.
        selectionShapeProvider.clear();
        verify(delegateShapeProvider, times(1)).clear();
        verify(enterHandlerReg, times(1)).removeHandler();
        verify(exitHandlerReg, times(1)).removeHandler();
    }

    @Test
    public void testRegister() {
        tested.init(canvasHandler);
        tested.register(element);
        verify(selectionControl, times(1)).register(eq(element));
    }

    @Test
    public void testSelect() {
        tested.init(canvasHandler);
        tested.register(element);
        final SelectionManager.SelectedItems selectedItems = new SelectionManager.SelectedItems(selectionManager,
                                                                                                lienzoLayer);
        selectedItems.getChanged().getAddedShapes().add(shapeView);
        selectionListener.onChanged(selectedItems);
        verify(selectionControl, times(1)).select(eq(Collections.singletonList(ELEMENT_UUID)));
    }

    @Test
    public void testOnSelectEvent() {
        final SelectionManager.SelectedItems selectedItems = mock(SelectionManager.SelectedItems.class);
        when(selectionManager.getSelectedItems()).thenReturn(selectedItems);
        tested.init(canvasHandler);
        tested.register(element);
        tested.onSelect(Collections.singletonList(ELEMENT_UUID));
        verify(selectedItems, times(1)).add(eq(shapeView));
        verify(selectionControl, never()).clearSelection();
    }

    @Test
    public void testOnShapeLocationsChanged() {

        //Different canvas
        when(shapeLocationsChangedEvent.getCanvasHandler()).thenReturn(mock(CanvasHandler.class));
        tested.onShapeLocationsChanged(shapeLocationsChangedEvent);

        verify(selectedItems, never()).rebuildBoundingBox();
        verify(selectionManager, never()).drawSelectionShape(eq(MIN_WIDTH),
                                                             eq(MIN_HEIGHT),
                                                             eq(MAX_WIDTH),
                                                             eq(MAX_HEIGHT),
                                                             eq(overLayer));

        //Same canvas no selectedItems

        when(shapeLocationsChangedEvent.getCanvasHandler()).thenReturn(canvasHandler);
        when(shapeLocationsChangedEvent.getUuids()).thenReturn(new ArrayList<>());
        tested.onShapeLocationsChanged(shapeLocationsChangedEvent);

        verify(selectedItems, never()).rebuildBoundingBox();
        verify(selectionManager, never()).drawSelectionShape(eq(MIN_WIDTH),
                                                             eq(MIN_HEIGHT),
                                                             eq(MAX_WIDTH),
                                                             eq(MAX_HEIGHT),
                                                             eq(overLayer));

        //Same canvas with selected items

        when(shapeLocationsChangedEvent.getCanvasHandler()).thenReturn(canvasHandler);

        List<String> selectedUUIds = new ArrayList<>();
        selectedUUIds.add("ELEMENT");

        when(shapeLocationsChangedEvent.getUuids()).thenReturn(selectedUUIds);
        when(tested.getSelectedItems()).thenReturn(selectedUUIds);

        when(delegateShapeProvider.getShape()).thenReturn(mock(com.ait.lienzo.client.core.shape.Shape.class));
        when(selectionManager.getSelectedItems()).thenReturn(selectedItems);
        when(selectedItems.getBoundingBox()).thenReturn(new BoundingBox(MIN_WIDTH, MIN_HEIGHT, MAX_WIDTH, MAX_HEIGHT));
        when(shapeLocationsChangedEvent.getCanvasHandler()).thenReturn(canvasHandler);

        tested.onShapeLocationsChanged(shapeLocationsChangedEvent);

        verify(selectedItems, times(1)).rebuildBoundingBox();
        verify(selectionManager, times(1)).drawSelectionShapeForSelection();
    }

    @Test
    public void testDeselect() {
        tested.init(canvasHandler);
        tested.register(element);
        final SelectionManager.SelectedItems selectedItems = new SelectionManager.SelectedItems(selectionManager,
                                                                                                lienzoLayer);
        selectedItems.getChanged().getRemovedShapes().add(shapeView);
        selectionListener.onChanged(selectedItems);
        verify(selectionControl, times(1)).deselect(eq(Collections.singletonList(ELEMENT_UUID)));
        verify(selectionControl, never()).clearSelection();
    }

    @Test
    public void testClearSelection() {
        final SelectionManager.SelectedItems selectedItems = mock(SelectionManager.SelectedItems.class);
        when(selectionManager.getSelectedItems()).thenReturn(selectedItems);
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element.getUUID());
        tested.clearSelection();
        verify(selectionControl, times(1)).clearSelection();
        verify(selectionManager, times(1)).clearSelection();
    }

    @Test
    public void testOnClearSelectionEvent() {
        tested.init(canvasHandler);
        tested.onClearSelection();
        verify(selectionControl, never()).clearSelection();
        verify(selectionManager, times(1)).clearSelection();
    }

    @Test
    public void testMoveSelectionShapeToTop() {
        final LienzoMultipleSelectionControl.CursoredSelectionShapeProvider ssp =
                mock(LienzoMultipleSelectionControl.CursoredSelectionShapeProvider.class);
        tested = new LienzoMultipleSelectionControl<>(selectionControl,
                                                      canvasSelectionEvent,
                                                      clearSelectionEvent,
                                                      ssp);
        tested.init(canvasHandler);
        tested.onCanvasSelection(new CanvasSelectionEvent(canvasHandler, Collections.emptyList()));
        verify(ssp, times(1)).moveShapeToTop();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeregister() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.deregister(element);
        verify(selectionControl, times(1)).deregister(eq(element));
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(tested).clearSelection();
        verify(selectionControl).clearSelection();
        verify(selectionManager,
               times(1)).clearSelection();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(tested).onDestroy();
        verify(selectionManager).destroy();
        verify(selectionShapeProvider).destroy();
    }
}
