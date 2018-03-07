package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.OnEventHandlers;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void testOnlyLeftMouseButtonCanStartSelection()
    {
        when(wiresManager.getLayer()).thenReturn(wiresLayer);
        when(wiresManager.getControlFactory()).thenReturn(factory);
        when(wiresLayer.getLayer()).thenReturn(layer);
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getOnEventHandlers()).thenReturn(onEventHandlers);
        SelectionManager manager = new SelectionManager(wiresManager);

        when(mouseEvent.isButtonLeft()).thenReturn(false);
        manager.onNodeMouseDown(mouseEvent);
        assertFalse("Selection should be started by Left mouse button ONLY", manager.isSelectionCreationInProcess());
        verify(layer, times(0)).draw();

        when(mouseEvent.isButtonLeft()).thenReturn(true);
        manager.onNodeMouseDown(mouseEvent);
        assertTrue("Selection should be started by Left mouse button", manager.isSelectionCreationInProcess());
        verify(layer, times(1)).draw();
    }
}
