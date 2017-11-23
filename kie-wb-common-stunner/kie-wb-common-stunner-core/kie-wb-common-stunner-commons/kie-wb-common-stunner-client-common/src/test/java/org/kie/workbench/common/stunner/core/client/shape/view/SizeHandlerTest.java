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

package org.kie.workbench.common.stunner.core.client.shape.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
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
        tested.handle(new ViewImpl<>(bean, BoundsImpl.build()), view);
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
        tested.handle(new ViewImpl<>(bean, BoundsImpl.build(0d, 0d, 12.5d, 55.2d)), view);
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
        tested.handle(new ViewImpl<>(bean, BoundsImpl.build()), view);
        verify(view, never()).setSize(anyDouble(), anyDouble());
        verify(view, never()).setRadius(anyDouble());
    }
}
