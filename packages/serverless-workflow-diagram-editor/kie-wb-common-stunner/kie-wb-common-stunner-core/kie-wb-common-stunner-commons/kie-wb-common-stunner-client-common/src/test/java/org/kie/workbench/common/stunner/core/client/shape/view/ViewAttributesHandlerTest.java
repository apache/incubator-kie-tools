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


package org.kie.workbench.common.stunner.core.client.shape.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.ViewAttributesHandler;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ViewAttributesHandlerTest {

    private ViewAttributesHandler<Object, ShapeViewExtStub> tested;

    private ShapeViewExtStub view;

    @Before
    public void setup() throws Exception {
        view = spy(new ShapeViewExtStub());
    }

    @Test
    public void testHandle() {
        tested = new ViewAttributesHandler.Builder<Object, ShapeViewExtStub>()
                .strokeColor(o -> "strokeColor")
                .strokeWidth(o -> 5d)
                .strokeAlpha(o -> 0.1d)
                .fillColor(o -> "fillColor")
                .fillAlpha(o -> 0.3d)
                .alpha(o -> 0.7d)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(bean, view);
        verify(view, times(1)).setStrokeColor(eq("strokeColor"));
        verify(view, times(1)).setStrokeWidth(eq(5d));
        verify(view, times(1)).setStrokeAlpha(eq(0.1d));
        verify(view, times(1)).setFillGradient(any(HasFillGradient.Type.class),
                                               eq("fillColor"),
                                               anyString());
        verify(view, times(1)).setFillAlpha(eq(0.3d));
        verify(view, times(1)).setAlpha(eq(0.7d));
    }

    @Test
    public void testHandleDefaultSize() {
        tested = new ViewAttributesHandler.Builder<Object, ShapeViewExtStub>()
                .strokeColor(o -> null)
                .strokeWidth(o -> null)
                .strokeAlpha(o -> null)
                .fillColor(o -> null)
                .fillAlpha(o -> null)
                .alpha(o -> null)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(bean, view);
        verify(view, never()).setStrokeColor(anyString());
        verify(view, never()).setStrokeWidth(anyDouble());
        verify(view, never()).setStrokeAlpha(anyDouble());
        verify(view, never()).setFillColor(anyString());
        verify(view, never()).setFillGradient(any(HasFillGradient.Type.class),
                                              anyString(),
                                              anyString());
        verify(view, never()).setFillAlpha(anyDouble());
        verify(view, never()).setAlpha(anyDouble());
    }
}
