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

package org.kie.workbench.common.stunner.svg.client.shape.view;

import java.util.Arrays;
import java.util.Optional;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGViewUtilsTest {

    @Mock
    private ShapeView<?> shape1;

    @Mock
    private ShapeView<?> shape2;

    @Mock
    private SVGShapeView<?> svgShape;

    @Mock
    private SVGPrimitive primitive1;

    @Mock
    private IPrimitive<?> p1;

    @Mock
    private SVGPrimitive primitive2;

    @Mock
    private IPrimitive<?> p2;

    @Before
    public void setup() throws Exception {
        when(primitive1.getId()).thenReturn("p1");
        when(primitive2.getId()).thenReturn("p2");
        when(primitive1.get()).thenReturn(p1);
        when(primitive2.get()).thenReturn(p2);
        when(svgShape.getChildren()).thenReturn(Arrays.asList(primitive1, primitive2));
    }

    @Test
    public void testSwitchVisibilityP1P2() {
        SVGViewUtils.switchVisibility(svgShape,
                                      "p1",
                                      "p2");
        verify(p1, times(1)).setAlpha(1d);
        verify(p2, times(1)).setAlpha(0d);
    }

    @Test
    public void testSwitchVisibilityP2P1() {
        SVGViewUtils.switchVisibility(svgShape,
                                      "p2",
                                      "p1");
        verify(p1, times(1)).setAlpha(0d);
        verify(p2, times(1)).setAlpha(1d);
    }

    @Test
    public void testGetPrimitive() {
        Optional<SVGPrimitive> r1 = SVGViewUtils.getPrimitive(svgShape,
                                                              "p1");
        assertTrue(r1.isPresent());
        assertEquals(primitive1, r1.get());
        assertEquals(p1, r1.get().get());

        Optional<SVGPrimitive> r2 = SVGViewUtils.getPrimitive(svgShape,
                                                              "p2");
        assertTrue(r2.isPresent());
        assertEquals(primitive2, r2.get());
        assertEquals(p2, r2.get().get());
    }

    @Test
    public void testGetVisibleShape() {
        when(shape1.getAlpha()).thenReturn(0d);
        when(shape2.getAlpha()).thenReturn(0.1d);
        ShapeView<?> visibleShape = SVGViewUtils.getVisibleShape(shape1, shape2);
        assertEquals(shape2, visibleShape);
        when(shape1.getAlpha()).thenReturn(0.1d);
        when(shape2.getAlpha()).thenReturn(0d);
        visibleShape = SVGViewUtils.getVisibleShape(shape1, shape2);
        assertEquals(shape1, visibleShape);
    }
}
