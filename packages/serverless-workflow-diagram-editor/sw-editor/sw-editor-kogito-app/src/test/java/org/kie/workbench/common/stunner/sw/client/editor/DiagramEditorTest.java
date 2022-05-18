/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.editor;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.PostResizeCallback;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DiagramEditorTest {

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresCanvasView canvasView;

    @Mock
    private ScrollableLienzoPanel scrollableLienzoPanel;

    @Mock
    private ScrollablePanel lienzoPanel;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private StunnerEditor stunnerEditor;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Before
    public void setUp() {
        when(stunnerEditor.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getLienzoPanel()).thenReturn(scrollableLienzoPanel);
        when(scrollableLienzoPanel.getView()).thenReturn(lienzoPanel);
        when(lienzoPanel.getViewport()).thenReturn(viewport);
        when(transform.getTranslateX()).thenReturn(200d);
        when(transform.getTranslateY()).thenReturn(200d);
        doCallRealMethod().when(lienzoPanel).setPostResizeCallback(any(PostResizeCallback.class));
        doCallRealMethod().when(lienzoPanel).getPostResizeCallback();
        doCallRealMethod().when(viewport).setTransform(any(Transform.class));
        doCallRealMethod().when(viewport).getTransform();
        viewport.setTransform(transform);
    }

    @Test
    public void testScaleToFitWorkflow() {
        when(lienzoPanel.getWidePx()).thenReturn(500);
        when(lienzoPanel.getHighPx()).thenReturn(500);
        when(lienzoPanel.getLayerBounds()).thenReturn(Bounds.build(0d, 0d, 1000d, 1000d));

        DiagramEditor.scaleToFitWorkflow(stunnerEditor);

        verify(lienzoPanel, times(1)).setPostResizeCallback(any(PostResizeCallback.class));
        assertNotNull(lienzoPanel.getPostResizeCallback());
        // Run callback
        lienzoPanel.getPostResizeCallback().execute(lienzoPanel);
        // New transform is created
        assertNotEquals(transform, viewport.getTransform());
        verify(lienzoPanel, times(1)).setPostResizeCallback(null);
    }

    @Test
    public void testScaleToFitWorkflowScaleLessThanZero() {
        when(lienzoPanel.getWidePx()).thenReturn(0);
        when(lienzoPanel.getHighPx()).thenReturn(0);
        when(lienzoPanel.getLayerBounds()).thenReturn(Bounds.build(0d, 0d, 0d, 0d));

        DiagramEditor.scaleToFitWorkflow(stunnerEditor);

        verify(lienzoPanel, times(1)).setPostResizeCallback(any(PostResizeCallback.class));
        assertNotNull(lienzoPanel.getPostResizeCallback());
        // Run callback
        lienzoPanel.getPostResizeCallback().execute(lienzoPanel);
        // Same transform
        assertEquals(transform, viewport.getTransform());
        verify(lienzoPanel, times(1)).setPostResizeCallback(null);
    }
}
