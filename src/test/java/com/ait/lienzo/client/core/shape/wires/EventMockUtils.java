package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.widget.DragContext;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;

import static org.mockito.Mockito.*;

/**
 * Utils for mocking and firing events on nodes / shapes.
 * Note: Lienzo events cannot be mocked as each one contains the dispatch logic.
 */
public class EventMockUtils {

    @SuppressWarnings( "unchecked" )
    public static void click(final IPrimitive<?> node, final double x, final double y) {
        ClickEvent clickEvent = mock(ClickEvent.class);
        setUpMouseEvent(clickEvent, x, y);
        node.fireEvent(new NodeMouseClickEvent(clickEvent));
    }

    @SuppressWarnings( "unchecked" )
    public static void dblClick(final IPrimitive<?> node,  final double x, final double y) {
        DoubleClickEvent clickEvent = mock(DoubleClickEvent.class);
        setUpMouseEvent(clickEvent, x, y);
        node.fireEvent(new NodeMouseDoubleClickEvent(clickEvent));
    }


    @SuppressWarnings( "unchecked" )
    public static void dragStart(final IPrimitive<?> node,  final double x, final double y) {
        DragContext dragContext = createDragContext(node, x, y);
        node.fireEvent(new NodeDragStartEvent(dragContext));
    }

    @SuppressWarnings( "unchecked" )
    public static void dragMove(final IPrimitive<?> node,  final double x, final double y) {
        DragContext dragContext = createDragContext(node, x, y);
        node.fireEvent(new NodeDragMoveEvent(dragContext));
    }

    @SuppressWarnings( "unchecked" )
    public static void dragEnd(final IPrimitive<?> node,  final double x, final double y) {
        DragContext dragContext = createDragContext(node, x, y);
        node.fireEvent(new NodeDragEndEvent(dragContext));
    }

    @SuppressWarnings( "unchecked" )
    public static void mouseMove(final IPrimitive<?> node,  final double x, final double y) {
        MouseMoveEvent event = mock(MouseMoveEvent.class);
        setUpMouseEvent(event, x ,y);
        node.fireEvent(new NodeMouseMoveEvent(event));
    }

    @SuppressWarnings( "unchecked" )
    public static void mouseEnter(final IPrimitive<?> node,  final double x, final double y) {
        MouseMoveEvent event = mock(MouseMoveEvent.class);
        setUpMouseEvent(event, x ,y);
        node.fireEvent(new NodeMouseEnterEvent(event, (int )x, (int) y));
    }

    @SuppressWarnings( "unchecked" )
    public static void mouseOut(final IPrimitive<?> node,  final double x, final double y) {
        MouseOutEvent event = mock(MouseOutEvent.class);
        setUpMouseEvent(event, x ,y);
        node.fireEvent(new NodeMouseOutEvent(event));
    }

    @SuppressWarnings( "unchecked" )
    public static void mouseDown(final IPrimitive<?> node,  final double x, final double y) {
        MouseDownEvent event = mock(MouseDownEvent.class);
        setUpMouseEvent(event, x ,y);
        node.fireEvent(new NodeMouseDownEvent(event));
    }

    @SuppressWarnings( "unchecked" )
    public static void mouseUp(final IPrimitive<?> node,  final double x, final double y) {
        MouseUpEvent event = mock(MouseUpEvent.class);
        setUpMouseEvent(event, x ,y);
        node.fireEvent(new NodeMouseUpEvent(event));
    }

    @SuppressWarnings( "unchecked" )
    public static void mouseOver(final IPrimitive<?> node,  final double x, final double y) {
        MouseOverEvent event = mock(MouseOverEvent.class);
        setUpMouseEvent(event, x ,y);
        node.fireEvent(new NodeMouseOverEvent(event));
    }

    @SuppressWarnings( "unchecked" )
    public static void mouseExit(final IPrimitive<?> node,  final double x, final double y) {
        MouseOverEvent event = mock(MouseOverEvent.class);
        setUpMouseEvent(event, x ,y);
        node.fireEvent(new NodeMouseExitEvent(event, (int )x, (int) y));
    }
    
    private static void setUpMouseEvent(final MouseEvent mouseEvent, final double x, final double y) {
        int _x  = (int) x;
        int _y  = (int) y;
        when(mouseEvent.getX()).thenReturn(_x);
        when(mouseEvent.getClientX()).thenReturn(_x);
        when(mouseEvent.getRelativeX(any(Element.class))).thenReturn(_x);
        when(mouseEvent.getRelativeY(any(Element.class))).thenReturn(_y);
        when(mouseEvent.getY()).thenReturn(_y);
        when(mouseEvent.getClientY()).thenReturn(_y);
        when(mouseEvent.isAltKeyDown()).thenReturn(false);
        when(mouseEvent.isControlKeyDown()).thenReturn(false);
        when(mouseEvent.isMetaKeyDown()).thenReturn(false);
        when(mouseEvent.isShiftKeyDown()).thenReturn(false);
        
    }
    
    private static DragContext createDragContext(final IPrimitive<?> node,  final double x, final double y) {
        int _x = (int) x;
        int _y = (int) y;

        /*DragConstraintEnforcer dragConstraintEnforcer = mock(DragConstraintEnforcer.class);
        DragContext dragContext = mock(DragContext.class);
        when(dragContext.getDragConstraints()).thenReturn(dragConstraintEnforcer);
        when(dragContext.getNode()).thenReturn(node);
        when(dragContext.getDragStartX()).thenReturn(_x);
        when(dragContext.getDragStartY()).thenReturn(_y);
        when(dragContext.getEventX()).thenReturn(_x);
        when(dragContext.getEventY()).thenReturn(_y);
        when(dragContext.getDx()).thenReturn(_x);
        when(dragContext.getDy()).thenReturn(_y);
        return dragContext;*/

        INodeXYEvent iNodeXYEvent = mock(INodeXYEvent.class);
        when(iNodeXYEvent.getX()).thenReturn(_x);
        when(iNodeXYEvent.getY()).thenReturn(_y);
        when(iNodeXYEvent.isAlive()).thenReturn(true);
        return new DragContext(iNodeXYEvent,node);
        
    }

}
