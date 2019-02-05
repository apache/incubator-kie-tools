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
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FontHandlerTest {

    private static final double X_OFFSET = 10.0;

    private static final double Y_OFFSET = 20.0;

    private FontHandler<Object, ShapeViewExtStub> tested;

    private ShapeViewExtStub view;

    @Before
    public void setup() throws Exception {
        view = spy(new ShapeViewExtStub());
    }

    @Test
    public void testHandle() {
        tested = new FontHandler.Builder<Object, ShapeViewExtStub>()
                .strokeColor(o -> "strokeColor")
                .strokeSize(o -> 5d)
                .strokeAlpha(o -> 0.77d)
                .fontColor(o -> "fontColor")
                .fontFamily(o -> "fontFamily")
                .textWrapperStrategy(o -> TextWrapperStrategy.NO_WRAP)
                .fontSize(o -> 10.5d)
                .alpha(o -> 0.7d)
                .rotation(o -> 180d)
                .position(o -> HasTitle.Position.TOP)
                .positionXOffset(o -> X_OFFSET)
                .positionYOffset(o -> Y_OFFSET)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(bean, view);
        verify(view).setTitleStrokeColor(eq("strokeColor"));
        verify(view).setTitleStrokeWidth(eq(5d));
        verify(view).setTitleStrokeAlpha(eq(0.77d));
        verify(view).setTitleFontColor(eq("fontColor"));
        verify(view).setTitleFontFamily(eq("fontFamily"));
        verify(view).setTitleFontSize(eq(10.5d));
        verify(view).setTitleAlpha(eq(0.7d));
        verify(view).setTitleRotation(eq(180d));
        verify(view).setTitlePosition(eq(HasTitle.Position.TOP));
        verify(view).setTitleXOffsetPosition(eq(X_OFFSET));
        verify(view).setTitleYOffsetPosition(eq(Y_OFFSET));
        verify(view).setTextWrapper(TextWrapperStrategy.NO_WRAP);
    }

    @Test
    public void testHandleDefaultSize() {
        tested = new FontHandler.Builder<Object, ShapeViewExtStub>()
                .strokeColor(o -> null)
                .strokeSize(o -> null)
                .strokeAlpha(o -> null)
                .fontColor(o -> null)
                .fontFamily(o -> null)
                .fontSize(o -> null)
                .textWrapperStrategy(o -> null)
                .alpha(o -> null)
                .rotation(o -> null)
                .position(o -> null)
                .positionXOffset(o -> null)
                .positionYOffset(o -> null)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(bean, view);
        verify(view, never()).setTitleStrokeColor(anyString());
        verify(view, never()).setTitleStrokeWidth(anyDouble());
        verify(view, never()).setStrokeAlpha(anyDouble());
        verify(view, never()).setTitleFontColor(anyString());
        verify(view, never()).setTitleFontFamily(anyString());
        verify(view, never()).setTitleFontSize(anyDouble());
        verify(view, never()).setTitleAlpha(anyDouble());
        verify(view, never()).setTitleRotation(anyDouble());
        verify(view, never()).setTitlePosition(any(HasTitle.Position.class));
        verify(view, never()).setTitleXOffsetPosition(anyDouble());
        verify(view, never()).setTitleYOffsetPosition(anyDouble());
        verify(view, never()).setTextWrapper(any());
    }
}
