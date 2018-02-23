package com.ait.lienzo.client.widget;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.event.OnEventHandlers;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoHandlerManagerTest {

    @Mock
    private Mediators mediators;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private LienzoPanel lienzoPanel;

    @Mock
    private MouseDownEvent mouseDownEvent;

    @Mock
    private MouseMoveEvent mouseMoveEvent;

    @Mock
    private MouseUpEvent mouseUpEvent;

    @Mock
    private Shape shape;

    @Mock
    private IPrimitive iPrimitive;

    @Mock
    private Node node;

    @Mock
    private Node parent;

    @Mock
    private Layer dragLayer;

    @Mock
    private Layer layer;

    @Mock
    private Context2D context2D;

    @Captor
    private ArgumentCaptor<MouseDownHandler> mouseDownHandler;

    @Captor
    private ArgumentCaptor<MouseMoveHandler> mouseMoveHandler;

    @Captor
    private ArgumentCaptor<MouseUpHandler> mouseUpHandler;

    private LienzoHandlerManager manager;

    @Before
    public void setUp() throws Exception {
        doReturn(NativeEvent.BUTTON_LEFT).when(mouseDownEvent).getNativeButton();

        doReturn(context2D).when(dragLayer).getContext();

        doReturn(new Transform()).when(parent).getAbsoluteTransform();

        doReturn(node).when(iPrimitive).asNode();
        doReturn(layer).when(iPrimitive).getLayer();
        doReturn(DragMode.DRAG_LAYER).when(iPrimitive).getDragMode();
        doReturn(EventPropagationMode.NO_ANCESTORS).when(iPrimitive).getEventPropagationMode();
        doReturn(true).when(iPrimitive).isDraggable();
        doReturn(parent).when(iPrimitive).getParent();

        doReturn(iPrimitive).when(shape).asPrimitive();

        doReturn(shape).when(viewport).findShapeAtPoint(anyInt(), anyInt());
        doReturn(new OnEventHandlers()).when(viewport).getOnEventHandlers();
        doReturn(mediators).when(viewport).getMediators();

        doReturn(dragLayer).when(lienzoPanel).getDragLayer();
        doReturn(DragMouseControl.LEFT_MOUSE_ONLY).when(lienzoPanel).getDragMouseButtons();
        doReturn(viewport).when(lienzoPanel).getViewport();
        doReturn(transform).when(viewport).getTransform();

        manager = spy(new LienzoHandlerManager(lienzoPanel));
    }

    @Test
    public void testDragLayer() throws Exception {
        verify(lienzoPanel).addMouseDownHandler(mouseDownHandler.capture());
        verify(lienzoPanel).addMouseMoveHandler(mouseMoveHandler.capture());
        verify(lienzoPanel).addMouseUpHandler(mouseUpHandler.capture());

        // dragging Has Not started, missing mouse down
        mouseMoveHandler.getValue().onMouseMove(mouseMoveEvent);
        verify(dragLayer, never()).draw();

        mouseUpHandler.getValue().onMouseUp(mouseUpEvent);
        verify(lienzoPanel, never()).setCursor(Style.Cursor.DEFAULT);

        // dragging Has started
        mouseDownHandler.getValue().onMouseDown(mouseDownEvent);

        mouseMoveHandler.getValue().onMouseMove(mouseMoveEvent);
        verify(dragLayer).draw();

        mouseUpHandler.getValue().onMouseUp(mouseUpEvent);
        verify(lienzoPanel).setCursor(Style.Cursor.DEFAULT);

        verify(mediators, times(5)).handleEvent(any(GwtEvent.class));
    }
}
