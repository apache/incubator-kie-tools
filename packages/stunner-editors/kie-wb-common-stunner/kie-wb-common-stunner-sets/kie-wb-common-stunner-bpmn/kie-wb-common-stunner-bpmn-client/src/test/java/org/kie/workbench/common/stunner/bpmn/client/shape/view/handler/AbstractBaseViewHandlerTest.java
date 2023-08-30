/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public abstract class AbstractBaseViewHandlerTest<B, T extends ShapeViewHandler<B, SVGShapeView<?>>> {

    @Mock
    protected SVGShapeView<?> svgShapeView;

    protected List<SVGPrimitive<?>> shapeChildren;

    protected T viewHandler;

    @Before
    public void setUp() {
        shapeChildren = new ArrayList<>();
        when(svgShapeView.getChildren()).thenReturn(shapeChildren);
        viewHandler = createViewHandler();
    }

    protected abstract T createViewHandler();

    protected static SVGPrimitive mockSVGPrimitive(String id) {
        SVGPrimitive primitive = mock(SVGPrimitive.class);
        IPrimitive iPrimitive = mock(IPrimitive.class);
        when(primitive.getPrimitiveId()).thenReturn(id);
        when(primitive.get()).thenReturn(iPrimitive);
        when(iPrimitive.setFillAlpha(anyDouble())).thenReturn(iPrimitive);
        when(iPrimitive.setStrokeAlpha(anyDouble())).thenReturn(iPrimitive);
        return primitive;
    }

    protected static void verifyFillAndStroke(SVGPrimitive primitive, int times, double fill, double stroke) {
        verify(primitive.get(), times(times)).setFillAlpha(fill);
        verify(primitive.get(), times(times)).setStrokeAlpha(stroke);
    }
}
