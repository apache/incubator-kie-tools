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

package org.kie.workbench.common.stunner.core.client.components.views;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasDefinitionTooltipTest {

    @Mock
    private CanvasTooltip<String> textTooltip;

    private CanvasDefinitionTooltip tested;

    @Before
    public void setup() throws Exception {
        this.tested = new CanvasDefinitionTooltip(textTooltip,
                                                  defId -> defId + "theTestTitle");
    }

    @Test
    public void testSetCanvasLocation() {
        final Point2D point = new Point2D(22,
                                          66);
        tested.setCanvasLocation(point);
        verify(textTooltip,
               times(1)).setCanvasLocation(eq(point));
    }

    @Test
    public void testSetTransform() {
        final Transform transform = mock(Transform.class);
        tested.setTransform(transform);
        verify(textTooltip,
               times(1)).setTransform(eq(transform));
    }

    @Test
    public void testConfigure() {
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final AbstractCanvas canvas = mock(AbstractCanvas.class);
        final AbstractCanvas.View canvasView = mock(AbstractCanvas.View.class);
        final Layer layer = mock(Layer.class);
        final Transform transform = mock(Transform.class);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getLayer()).thenReturn(layer);
        when(layer.getTransform()).thenReturn(transform);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getAbsoluteX()).thenReturn(220d);
        when(canvasView.getAbsoluteY()).thenReturn(50.5d);
        final CanvasDefinitionTooltip t = tested.configure(canvasHandler);
        assertEquals(tested,
                     t);
        verify(textTooltip,
               times(1)).setTransform(eq(transform));
        final ArgumentCaptor<Point2D> pointCaptor = ArgumentCaptor.forClass(Point2D.class);
        verify(textTooltip,
               times(1)).setCanvasLocation(pointCaptor.capture());
        final Point2D point = pointCaptor.getValue();
        assertEquals(220d,
                     point.getX(),
                     0);
        assertEquals(50.5d,
                     point.getY(),
                     0);
    }

    @Test
    public void testShow() {
        final Point2D point = new Point2D(55,
                                          6);
        tested.show(new CanvasDefinitionTooltip.DefinitionIdContent("def1"),
                    point);
        verify(textTooltip,
               times(1)).show(eq("def1theTestTitle"),
                              eq(point));
        verify(textTooltip,
               never()).hide();
        verify(textTooltip,
               never()).destroy();
    }

    @Test
    public void testShowById() {
        final Point2D point = new Point2D(55,
                                          6);
        tested.show("def1",
                    point);
        verify(textTooltip,
               times(1)).show(eq("def1theTestTitle"),
                              eq(point));
        verify(textTooltip,
               never()).hide();
        verify(textTooltip,
               never()).destroy();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(textTooltip,
               times(1)).hide();
        verify(textTooltip,
               never()).show(anyString(),
                             any(Point2D.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(textTooltip,
               times(1)).destroy();
        verify(textTooltip,
               never()).show(anyString(),
                             any(Point2D.class));
    }
}
