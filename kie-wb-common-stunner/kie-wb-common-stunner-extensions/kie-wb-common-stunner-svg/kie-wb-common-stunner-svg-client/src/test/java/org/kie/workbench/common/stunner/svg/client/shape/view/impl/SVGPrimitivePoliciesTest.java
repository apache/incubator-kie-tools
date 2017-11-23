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

package org.kie.workbench.common.stunner.svg.client.shape.view.impl;

import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGPrimitivePoliciesTest {

    @Mock
    private SVGBasicShapeView svgView;

    @Mock
    private Shape<?> shape;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(svgView.getStrokeColor()).thenReturn("strokeColor1");
        when(svgView.getFillColor()).thenReturn("fillColor1");
    }

    @Test
    public void testNonePolicy() {
        SVGPrimitivePolicies.Builder
                .buildNonePolicy()
                .accept(svgView, shape);
        verify(shape, never()).setStrokeColor(anyString());
        verify(shape, never()).setFillColor(anyString());
    }

    @Test
    public void testStrokePolicy() {
        SVGPrimitivePolicies.Builder
                .buildStrokeColorPolicy()
                .accept(svgView, shape);
        verify(shape, times(1)).setStrokeColor("strokeColor1");
        verify(shape, times(1)).setFillColor("strokeColor1");
    }

    @Test
    public void testColorPolicy() {
        SVGPrimitivePolicies.Builder
                .buildSameColorPolicy()
                .accept(svgView, shape);
        verify(shape, times(1)).setStrokeColor("strokeColor1");
        verify(shape, times(1)).setFillColor("fillColor1");
    }
}
