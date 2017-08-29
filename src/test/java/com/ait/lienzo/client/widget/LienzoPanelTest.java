package com.ait.lienzo.client.widget;

import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.ui.Widget;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoPanelTest {

    private LienzoPanel lienzoPanel;

    @Test
    public void testNoParamConstructor() {
        lienzoPanel = new LienzoPanel();
        assertNotNull(lienzoPanel.getViewport());
    }

    @Test
    public void testViewPortConstructor() {
        lienzoPanel = new LienzoPanel(new Viewport());
        assertNotNull(lienzoPanel.getViewport());
    }

    @Test
    public void testWideHeighConstructor() {
        lienzoPanel = new LienzoPanel(999, 999);
        assertNotNull(lienzoPanel.getViewport());
    }

    @Test
    public void testSceneWideHeighConstructor() {
        lienzoPanel = new LienzoPanel(new Scene(), 999, 999);
        assertNotNull(lienzoPanel.getViewport());
    }

    @Test
    public void testViewportWideHeighConstructor() {
        lienzoPanel = new LienzoPanel(new Viewport(), 999, 999);
        assertNotNull(lienzoPanel.getViewport());
    }

    @Test
    public void testOnResizeNullParent() {
        Viewport viewport = spy(new Viewport());
        lienzoPanel = spy(new LienzoPanel(viewport));

        doReturn(null).when(lienzoPanel).getParent();

        lienzoPanel.onResize();
        verify(lienzoPanel, never()).setPixelSize(anyInt(), anyInt());
        verify(viewport).setPixelSize(anyInt(), anyInt());
        verify(viewport).draw();
    }

    @Test
    public void testOnResizeWideZero() {
        Viewport viewport = spy(new Viewport());
        Widget parent = mock(Widget.class);
        lienzoPanel = spy(new LienzoPanel(viewport));

        doReturn(parent).when(lienzoPanel).getParent();
        doReturn(0).when(parent).getOffsetWidth();
        doReturn(999).when(parent).getOffsetHeight();

        lienzoPanel.onResize();
        verify(lienzoPanel, never()).setPixelSize(anyInt(), anyInt());
        verify(viewport).setPixelSize(anyInt(), anyInt());
        verify(viewport).draw();
    }

    @Test
    public void testOnResizeHeighZero() {
        Viewport viewport = spy(new Viewport());
        Widget parent = mock(Widget.class);
        lienzoPanel = spy(new LienzoPanel(viewport));

        doReturn(parent).when(lienzoPanel).getParent();
        doReturn(999).when(parent).getOffsetWidth();
        doReturn(0).when(parent).getOffsetHeight();

        lienzoPanel.onResize();
        verify(lienzoPanel, never()).setPixelSize(anyInt(), anyInt());
        verify(viewport).setPixelSize(anyInt(), anyInt());
        verify(viewport).draw();
    }

    @Test
    public void testOnResize() {
        Viewport viewport = spy(new Viewport());
        Widget parent = mock(Widget.class);
        lienzoPanel = spy(new LienzoPanel(viewport));

        doReturn(parent).when(lienzoPanel).getParent();
        doReturn(999).when(parent).getOffsetWidth();
        doReturn(999).when(parent).getOffsetHeight();

        lienzoPanel.onResize();
        verify(lienzoPanel).setPixelSize(999, 999);
        verify(viewport).setPixelSize(999, 999);
        verify(viewport, times(2)).draw();
    }
}
