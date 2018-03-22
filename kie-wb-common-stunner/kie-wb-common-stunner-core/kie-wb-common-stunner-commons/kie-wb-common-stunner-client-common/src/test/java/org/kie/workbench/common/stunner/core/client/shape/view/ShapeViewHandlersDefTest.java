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

import java.util.Optional;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShapeViewHandlersDefTest {

    @Mock
    private ShapeViewDef<Object, ShapeView> delegate;

    @Mock
    private BiConsumer<View<Object>, ShapeView> sizeHandler;

    @Mock
    private BiConsumer<Object, ShapeView> fontHandler;

    @Mock
    private BiConsumer<Object, ShapeView> viewHandler;

    @Mock
    private BiConsumer<String, ShapeView> titleHandler;

    @Mock
    private Glyph glyph;

    private ShapeViewHandlersDef<Object, ShapeView, ShapeViewDef<Object, ShapeView>> tested;

    @Before
    public void setup() throws Exception {
        when(delegate.sizeHandler()).thenReturn(Optional.of(sizeHandler));
        when(delegate.fontHandler()).thenReturn(Optional.of(fontHandler));
        when(delegate.titleHandler()).thenReturn(Optional.of(titleHandler));
        when(delegate.viewHandler()).thenReturn(viewHandler);
        when(delegate.getGlyph(any(Class.class), anyString())).thenReturn(glyph);
        tested = new ShapeViewHandlersDef<>(delegate);
    }

    @Test
    public void testGettersAndDelegates() {
        assertEquals(delegate, tested.getShapeViewDef());
        assertEquals(glyph, tested.getGlyph(Object.class,
                                            ""));
    }

    @Test
    public void testHandle() {
        assertEquals(fontHandler, tested.fontHandler().get());
        verify(delegate,
               times(1)).fontHandler();
        assertEquals(sizeHandler, tested.sizeHandler().get());
        verify(delegate,
               times(1)).sizeHandler();
        assertEquals(titleHandler, tested.titleHandler().get());
        verify(delegate,
               times(1)).titleHandler();
        assertEquals(viewHandler, tested.viewHandler());
        verify(delegate,
               times(1)).viewHandler();
    }
}
