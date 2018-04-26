package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.OnEventHandlers;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SelectionManagerTest
{

    @Mock
    private WiresManager wiresManager;

    @Mock
    private NodeMouseDownEvent mouseEvent;

    @Mock
    private WiresLayer wiresLayer;

    @Mock
    private Layer layer;

    @Mock
    private Viewport viewport;

    @Mock
    private OnEventHandlers onEventHandlers;

    @Mock
    private WiresControlFactory factory;

    @Mock
    private Layer overLayer;

    @Mock
    private Transform transform;

    @Captor
    private ArgumentCaptor<SelectionManager.OnMouseXEventHandler> onMouseXEventHandlerArgumentCaptor;

    private SelectionManager.OnMouseXEventHandler onMouseXEventHandler;

    private SelectionManager manager;

    @Before
    public void setup()
    {
        when(wiresManager.getLayer()).thenReturn(wiresLayer);
        when(wiresManager.getControlFactory()).thenReturn(factory);
        when(wiresLayer.getLayer()).thenReturn(layer);
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getOnEventHandlers()).thenReturn(onEventHandlers);
        when(viewport.getOverLayer()).thenReturn(overLayer);
        when(viewport.getTransform()).thenReturn(transform);

        manager = spy(new SelectionManager(wiresManager));

        verify(onEventHandlers).setOnMouseMoveEventHandle(onMouseXEventHandlerArgumentCaptor.capture());

        onMouseXEventHandler = spy(onMouseXEventHandlerArgumentCaptor.getValue());
    }

    @Test
    public void testOnlyLeftMouseButtonCanStartSelection()
    {
        when(mouseEvent.isButtonLeft()).thenReturn(false);
        manager.onNodeMouseDown(mouseEvent);
        assertFalse("Selection should be started by Left mouse button ONLY", manager.isSelectionCreationInProcess());
        verify(layer, times(0)).draw();

        when(mouseEvent.isButtonLeft()).thenReturn(true);
        manager.onNodeMouseDown(mouseEvent);
        assertTrue("Selection should be started by Left mouse button", manager.isSelectionCreationInProcess());
        verify(layer, times(1)).draw();
    }

    @Test
    public void testDrawSelectionShape()
    {
        final MouseMoveEvent mouseEvent = mock(MouseMoveEvent.class);
        final double x = 10;
        final double y = 20;
        final double translateX = 40;
        final double translateY = 80;
        final double scaleX = 2;
        final double scaleY = 2;
        final double expectedX = 10;
        final double expectedY = 20;
        final double expectedWidth = -30;
        final double expectedHeight = -60;

        when(transform.getTranslateX()).thenReturn(translateX);
        when(transform.getTranslateY()).thenReturn(translateY);
        when(transform.getScaleX()).thenReturn(scaleX);
        when(transform.getScaleY()).thenReturn(scaleY);
        doReturn(x).when(manager).relativeStartX();
        doReturn(y).when(manager).relativeStartY();
        doReturn(manager).when(onMouseXEventHandler).getSelectionManager();
        doNothing().when(manager).drawSelectionShape(anyInt(), anyInt(), anyInt(), anyInt(), any(Layer.class));

        onMouseXEventHandler.drawSelectionShape(mouseEvent);

        verify(manager).drawSelectionShape(eq(expectedX), eq(expectedY), eq(expectedWidth), eq(expectedHeight), eq(overLayer));
        verify(overLayer).draw();
    }

    @Test
    public void testDrawSelectionShapeWhenHeightAndWidthAreZero()
    {
        final MouseMoveEvent mouseEvent = mock(MouseMoveEvent.class);
        final double x = 10;
        final double y = 20;
        final double translateX = -10;
        final double translateY = -20;
        final double scaleX = 1;
        final double scaleY = 1;
        final double expectedX = 10;
        final double expectedY = 20;
        final double expectedWidth = 1;
        final double expectedHeight = 1;

        when(transform.getTranslateX()).thenReturn(translateX);
        when(transform.getTranslateY()).thenReturn(translateY);
        when(transform.getScaleX()).thenReturn(scaleX);
        when(transform.getScaleY()).thenReturn(scaleY);
        doReturn(x).when(manager).relativeStartX();
        doReturn(y).when(manager).relativeStartY();
        doReturn(manager).when(onMouseXEventHandler).getSelectionManager();
        doNothing().when(manager).drawSelectionShape(anyInt(), anyInt(), anyInt(), anyInt(), any(Layer.class));

        onMouseXEventHandler.drawSelectionShape(mouseEvent);

        verify(manager).drawSelectionShape(eq(expectedX), eq(expectedY), eq(expectedWidth), eq(expectedHeight), eq(overLayer));
        verify(overLayer).draw();
    }

    @Test
    public void testRelativeStartX()
    {
        final double startX = 20;
        final double startY = 25;
        final Point2D start = new Point2D(startX, startY);
        final double translateX = 10d;
        final double scaleX = 2d;

        doReturn(start).when(manager).getStart();
        when(transform.getTranslateX()).thenReturn(translateX);
        when(transform.getScaleX()).thenReturn(scaleX);

        final Double relativeStartX = manager.relativeStartX();

        assertEquals(5d, relativeStartX, 0);
    }

    @Test
    public void testRelativeStartY()
    {
        final double startX = 25;
        final double startY = 20;
        final Point2D start = new Point2D(startX, startY);
        final double translateY = 10d;
        final double scaleY = 2d;

        doReturn(start).when(manager).getStart();
        when(transform.getTranslateY()).thenReturn(translateY);
        when(transform.getScaleY()).thenReturn(scaleY);

        final Double relativeStartY = manager.relativeStartY();

        assertEquals(5d, relativeStartY, 0);
    }
}
