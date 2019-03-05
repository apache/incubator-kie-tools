/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.canvas;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.OnLayerAfterDraw;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoLayerTest {

    @Mock
    private Layer layer;
    @Mock
    private Scene scene;
    @Mock
    private Viewport viewport;
    @Mock
    private Layer topLayer;
    @Mock
    private IPrimitive<?> shape;

    private LienzoLayer tested;
    private Transform transform;

    @Before
    public void setUp() throws Exception {
        transform = new Transform();
        when(layer.getChildNodes()).thenReturn(new NFastArrayList<>());
        when(layer.getScene()).thenReturn(scene);
        when(layer.getViewport()).thenReturn(viewport);
        when(scene.getTopLayer()).thenReturn(topLayer);
        when(layer.getAbsoluteTransform()).thenReturn(transform);
        when(viewport.getTransform()).thenReturn(transform);
        this.tested = new LienzoLayer(layer);
    }

    @Test
    public void testAdd() {
        tested.add(shape);
        verify(layer, times(1)).add(eq(shape));
    }

    @Test
    public void testAddTwice() {
        when(layer.getChildNodes()).thenReturn(new NFastArrayList<>(shape));
        tested.add(shape);
        verify(layer, never()).add(eq(shape));
    }

    @Test
    public void testDelete() {
        tested.delete(shape);
        verify(layer, times(1)).remove(eq(shape));
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(layer, times(1)).clear();
    }

    @Test
    public void testOnAfterDraw() {
        tested.onAfterDraw(mock(Command.class));
        verify(layer, times(1)).setOnLayerAfterDraw(any(OnLayerAfterDraw.class));
    }

    @Test
    public void testGetTopLayer() {
        assertEquals(topLayer, tested.getTopLayer());
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(layer, times(1)).removeAll();
        verify(layer, times(1)).removeFromParent();
    }

    @Test
    public void testGetTranslate() {
        transform.translate(11, 33);
        Point2D translate = tested.getTranslate();
        assertEquals(11d, translate.getX(), 0d);
        assertEquals(33d, translate.getY(), 0d);
    }

    @Test
    public void testGetScale() {
        transform.scale(0.11, 0.666);
        Point2D translate = tested.getScale();
        assertEquals(0.11d, translate.getX(), 0d);
        assertEquals(0.666d, translate.getY(), 0d);
    }

    @Test
    public void testTranslate() {
        tested.translate(11, 33);
        assertEquals(11d, transform.getTranslateX(), 0d);
        assertEquals(33d, transform.getTranslateY(), 0d);
    }

    @Test
    public void testScaleUnit() {
        tested.scale(0.22d);
        assertEquals(0.22d, transform.getScaleX(), 0d);
        assertEquals(0.22d, transform.getScaleY(), 0d);
    }

    @Test
    public void testScaleUnits() {
        tested.scale(0.22d, 0.33d);
        assertEquals(0.22d, transform.getScaleX(), 0d);
        assertEquals(0.33d, transform.getScaleY(), 0d);
    }
}
