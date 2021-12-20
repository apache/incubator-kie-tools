package com.ait.lienzo.client.widget.panel.impl;

import java.util.EnumSet;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelDragLimitEventDetail.LimitDirections;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static com.ait.lienzo.client.widget.panel.impl.LienzoPanelEvents.LIENZO_PANEL_BOUNDS_CHANGED_EVENT;
import static com.ait.lienzo.client.widget.panel.impl.LienzoPanelEvents.LIENZO_PANEL_DRAG_LIMITS_OUT_EVENT;
import static com.ait.lienzo.client.widget.panel.impl.LienzoPanelEvents.LIENZO_PANEL_DRAG_LIMITS_OVER_EVENT;
import static com.ait.lienzo.client.widget.panel.impl.LienzoPanelEvents.LIENZO_PANEL_PRIMITIVE_DRAG_END_EVENT;
import static com.ait.lienzo.client.widget.panel.impl.LienzoPanelEvents.LIENZO_PANEL_PRIMITIVE_DRAG_MOVE_UPDATE_EVENT;
import static com.ait.lienzo.client.widget.panel.impl.LienzoPanelEvents.LIENZO_PANEL_PRIMITIVE_DRAG_OFFSET_UPDATE_EVENT;
import static com.ait.lienzo.client.widget.panel.impl.LienzoPanelEvents.LIENZO_PANEL_PRIMITIVE_DRAG_START_EVENT;
import static com.ait.lienzo.client.widget.panel.impl.LienzoPanelEvents.LIENZO_PANEL_RESIZE_EVENT;
import static com.ait.lienzo.client.widget.panel.impl.LienzoPanelEvents.LIENZO_PANEL_SCALE_EVENT;
import static com.ait.lienzo.client.widget.panel.impl.LienzoPanelEvents.LIENZO_PANEL_SCROLL_EVENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoPanelEventsTest {

    @Mock
    private LienzoPanel<? extends LienzoPanel> lienzoPanel;

    @Mock
    private HTMLDivElement htmlDivElement;

    @Before
    public void setup() {
        when(lienzoPanel.getElement()).thenReturn(htmlDivElement);
    }

    @Test
    public void testAddBoundsChangedEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addBoundsChangedEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).addEventListener(LIENZO_PANEL_BOUNDS_CHANGED_EVENT, eventListener);
    }

    @Test
    public void testRemoveBoundsChangedEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.removeBoundsChangedEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).removeEventListener(LIENZO_PANEL_BOUNDS_CHANGED_EVENT, eventListener);
    }

    @Test
    public void testFireBoundsChangedEvent() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addBoundsChangedEventListener(lienzoPanel, eventListener);
        LienzoPanelEvents.fireBoundsChangedEvent(lienzoPanel);
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }

    @Test
    public void testAddResizeEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addResizeEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).addEventListener(LIENZO_PANEL_RESIZE_EVENT, eventListener);
    }

    @Test
    public void testRemoveResizeEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.removeResizeEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).removeEventListener(LIENZO_PANEL_RESIZE_EVENT, eventListener);
    }

    @Test
    public void testFireResizeEvent() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addBoundsChangedEventListener(lienzoPanel, eventListener);
        LienzoPanelEvents.fireResizeEvent(lienzoPanel);
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }

    @Test
    public void testAddScaleEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addScaleEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).addEventListener(LIENZO_PANEL_SCALE_EVENT, eventListener);
    }

    @Test
    public void testRemoveScaleEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.removeScaleEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).removeEventListener(LIENZO_PANEL_SCALE_EVENT, eventListener);
    }

    @Test
    public void testFireScaleEvent() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addBoundsChangedEventListener(lienzoPanel, eventListener);
        LienzoPanelEvents.fireScaleEvent(lienzoPanel);
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }

    @Test
    public void testAddScrollEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addScrollEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).addEventListener(LIENZO_PANEL_SCROLL_EVENT, eventListener);
    }

    @Test
    public void testRemoveScrollEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.removeScrollEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).removeEventListener(LIENZO_PANEL_SCROLL_EVENT, eventListener);
    }

    @Test
    public void testFireScrollEvent() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addBoundsChangedEventListener(lienzoPanel, eventListener);
        LienzoPanelEvents.fireScrollEvent(lienzoPanel, 0, 0);
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }

    @Test
    public void testAddPrimitiveDragStartEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addPrimitiveDragStartEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).addEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_START_EVENT, eventListener);
    }

    @Test
    public void testRemovePrimitiveDragStartEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.removePrimitiveDragStartEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).removeEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_START_EVENT, eventListener);
    }

    @Test
    public void testFirePrimitiveDragStartEvent() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addBoundsChangedEventListener(lienzoPanel, eventListener);
        LienzoPanelEvents.firePrimitiveDragStartEvent(lienzoPanel, mock(IPrimitive.class), 0, 0);
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }

    @Test
    public void testAddPrimitiveDragMoveUpdateEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addPrimitiveDragMoveUpdateEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).addEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_MOVE_UPDATE_EVENT, eventListener);
    }

    @Test
    public void testRemovePrimitiveDragMoveUpdateEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.removePrimitiveDragMoveUpdateEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).removeEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_MOVE_UPDATE_EVENT, eventListener);
    }

    @Test
    public void testFirePrimitiveDragMoveUpdateEvent() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addBoundsChangedEventListener(lienzoPanel, eventListener);
        LienzoPanelEvents.firePrimitiveDragMoveUpdateEvent(lienzoPanel, mock(IPrimitive.class), 0, 0);
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }

    @Test
    public void testAddPrimitiveDragOffsetUpdateEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addPrimitiveDragOffsetUpdateEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).addEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_OFFSET_UPDATE_EVENT, eventListener);
    }

    @Test
    public void testRemovePrimitiveDragOffsetUpdateEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.removePrimitiveDragOffsetUpdateEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).removeEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_OFFSET_UPDATE_EVENT, eventListener);
    }

    @Test
    public void testFirePrimitiveDragOffsetUpdateEvent() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addBoundsChangedEventListener(lienzoPanel, eventListener);
        LienzoPanelEvents.firePrimitiveDragOffsetUpdateEvent(lienzoPanel, mock(IPrimitive.class), 0, 0);
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }

    @Test
    public void testAddPrimitiveDragEndEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addPrimitiveDragEndEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).addEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_END_EVENT, eventListener);
    }

    @Test
    public void testRemovePrimitiveDragEndEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.removePrimitiveDragEndEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).removeEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_END_EVENT, eventListener);
    }

    @Test
    public void testFirePrimitiveDragEndEvent() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addBoundsChangedEventListener(lienzoPanel, eventListener);
        LienzoPanelEvents.firePrimitiveDragEndEvent(lienzoPanel, mock(IPrimitive.class), 0, 0);
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }

    @Test
    public void testAddDragLimitsOverEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addDragLimitsOverEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).addEventListener(LIENZO_PANEL_DRAG_LIMITS_OVER_EVENT, eventListener);
    }

    @Test
    public void testRemoveDragLimitsOverEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.removeDragLimitsOverEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).removeEventListener(LIENZO_PANEL_DRAG_LIMITS_OVER_EVENT, eventListener);
    }

    @Test
    public void testFireDragLimitsOverEvent() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addDragLimitsOverEventListener(lienzoPanel, eventListener);
        LienzoPanelEvents.fireDragLimitsOverEvent(lienzoPanel, EnumSet.allOf(LimitDirections.class));
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }

    @Test
    public void testAddDragLimitsOutEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addDragLimitsOutEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).addEventListener(LIENZO_PANEL_DRAG_LIMITS_OUT_EVENT, eventListener);
    }

    @Test
    public void testRemoveDragLimitsOutEventListener() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.removeDragLimitsOutEventListener(lienzoPanel, eventListener);
        verify(htmlDivElement).removeEventListener(LIENZO_PANEL_DRAG_LIMITS_OUT_EVENT, eventListener);
    }

    @Test
    public void testFireDragLimitsOutEvent() {
        EventListener eventListener = mock(EventListener.class);
        LienzoPanelEvents.addDragLimitsOutEventListener(lienzoPanel, eventListener);
        LienzoPanelEvents.fireDragLimitsOutEvent(lienzoPanel);
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }

    @Test
    public void testFireCustomEvent() {
        LienzoPanelEvents.fireCustomEvent("CUSTOM_EVENT_TYPE", lienzoPanel, mock(LienzoPanelEventDetail.class));
        verify(htmlDivElement).dispatchEvent(any(Event.class));
    }
}