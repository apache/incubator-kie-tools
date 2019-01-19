/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasUtilsTest {

    private static Bounds PANEL_BOUNDS = Bounds.create(0d, 0d, 600d, 600d);

    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private AbstractCanvas canvas;
    @Mock
    private CanvasPanel canvasPanel;
    @Mock
    private AbstractCanvas.CanvasView canvasView;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getPanel()).thenReturn(canvasPanel);
        when(canvasPanel.getLocationConstraints()).thenReturn(PANEL_BOUNDS);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAreBoundsExceeded() {
        assertFalse(CanvasUtils.areBoundsExceeded(canvasHandler,
                                                  Bounds.create(0d, 0d, 100d, 100d)));
        assertTrue(CanvasUtils.areBoundsExceeded(canvasHandler,
                                                 Bounds.create(0d, 0d, 601d, 601d)));
        assertTrue(CanvasUtils.areBoundsExceeded(canvasHandler,
                                                 Bounds.create(-1d, -0.1d, 2d, 4d)));
    }
}
