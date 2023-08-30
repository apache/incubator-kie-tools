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
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SizeHandlerTest {

    private SizeHandler<Object, ShapeViewExtStub> tested;

    private ShapeViewExtStub view;

    @Before
    public void setup() throws Exception {
        view = spy(new ShapeViewExtStub());
    }

    @Test
    public void testHandleBeanSize() {
        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .width(o -> 10d)
                .height(o -> 20d)
                .radius(o -> 30d)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(new ViewImpl<>(bean, Bounds.create()), view);
        verify(view, times(1)).setSize(eq(10d),
                                       eq(20d));
        verify(view, times(1)).setRadius(eq(30d));
    }

    @Test
    public void testHandleNodeSize() {
        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .width(o -> null)
                .height(o -> null)
                .radius(o -> null)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(new ViewImpl<>(bean, Bounds.create(0d, 0d, 12.5d, 55.2d)), view);
        verify(view, times(1)).setSize(eq(12.5d),
                                       eq(55.2d));
        verify(view, times(1)).setRadius(eq(27.6d));
    }

    @Test
    public void testHandleDefaultSize() {
        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .width(o -> null)
                .height(o -> null)
                .radius(o -> null)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(new ViewImpl<>(bean, Bounds.create()), view);
        verify(view, never()).setSize(anyDouble(), anyDouble());
        verify(view, never()).setRadius(anyDouble());
    }

    @Test
    public void testHandleInvalidSizeConstraints() {
        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .minWidth(o -> 0d)
                .maxWidth(o -> 0d)
                .minHeight(o -> 0d)
                .maxHeight(o -> 0d)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(new ViewImpl<>(bean, Bounds.create()), view);

        verify(view, never()).setMinWidth(anyDouble());
        verify(view, never()).setMaxWidth(anyDouble());
        verify(view, never()).setMinHeight(anyDouble());
        verify(view, never()).setMaxHeight(anyDouble());

        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .minWidth(o -> -10d)
                .maxWidth(o -> -10d)
                .minHeight(o -> -100d)
                .maxHeight(o -> -100d)
                .build();
        tested.handle(new ViewImpl<>(bean, Bounds.create()), view);

        verify(view, never()).setMinWidth(anyDouble());
        verify(view, never()).setMaxWidth(anyDouble());
        verify(view, never()).setMinHeight(anyDouble());
        verify(view, never()).setMaxHeight(anyDouble());
    }

    @Test
    public void testHandleValidSizeConstraints() {
        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .minWidth(o -> null)
                .maxWidth(o -> null)
                .minHeight(o -> null)
                .maxHeight(o -> null)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(new ViewImpl<>(bean, Bounds.create()), view);

        verify(view, times(1)).setMinWidth(isNull(Double.class));
        verify(view, times(1)).setMaxWidth(isNull(Double.class));
        verify(view, times(1)).setMinHeight(isNull(Double.class));
        verify(view, times(1)).setMaxHeight(isNull(Double.class));

        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .minWidth(o -> 10d)
                .maxWidth(o -> 100d)
                .minHeight(o -> 10d)
                .maxHeight(o -> 100d)
                .build();
        tested.handle(new ViewImpl<>(bean, Bounds.create()), view);

        verify(view, times(1)).setMinWidth(10d);
        verify(view, times(1)).setMaxWidth(100d);
        verify(view, times(1)).setMinHeight(10d);
        verify(view, times(1)).setMaxHeight(100d);
    }

    @Test
    public void testHandleInvalidRadiusConstraints() {
        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .minRadius(o -> 0d)
                .maxRadius(o -> 0d)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(new ViewImpl<>(bean, Bounds.create()), view);

        verify(view, never()).setMinRadius(anyDouble());
        verify(view, never()).setMaxRadius(anyDouble());

        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .minRadius(o -> -10d)
                .maxRadius(o -> -100d)
                .build();
        tested.handle(new ViewImpl<>(bean, Bounds.create()), view);

        verify(view, never()).setMinRadius(anyDouble());
        verify(view, never()).setMaxRadius(anyDouble());
    }

    @Test
    public void testHandleValidRadiusConstraints() {
        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .minRadius(o -> null)
                .maxRadius(o -> null)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(new ViewImpl<>(bean, Bounds.create()), view);

        verify(view, times(1)).setMinRadius(isNull(Double.class));
        verify(view, times(1)).setMaxRadius(isNull(Double.class));

        tested = new SizeHandler.Builder<Object, ShapeViewExtStub>()
                .minRadius(o -> 10d)
                .maxRadius(o -> 100d)
                .build();
        tested.handle(new ViewImpl<>(bean, Bounds.create()), view);

        verify(view, times(1)).setMinRadius(10d);
        verify(view, times(1)).setMaxRadius(100d);
    }
}
