/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FontHandlerTest {

    private static final double X_OFFSET = 10.0;

    private static final double Y_OFFSET = 20.0;
    public static final HasTitle.Size SIZE_CONSTRAINTS = new HasTitle.Size(50d, 50d, HasTitle.Size.SizeType.PERCENTAGE);

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
                .rotation(o -> 270d)
                .positionXOffset(o -> X_OFFSET)
                .positionYOffset(o -> Y_OFFSET)
                .verticalAlignment(o -> HasTitle.VerticalAlignment.TOP)
                .horizontalAlignment(o -> HasTitle.HorizontalAlignment.LEFT)
                .referencePosition(o -> HasTitle.ReferencePosition.OUTSIDE)
                .orientation(o -> HasTitle.Orientation.VERTICAL)
                .margin(HasTitle.HorizontalAlignment.LEFT, 10d)
                .margin(HasTitle.VerticalAlignment.TOP, 10d)
                .margins(o -> Stream.of(new AbstractMap.SimpleEntry<>(HasTitle.HorizontalAlignment.RIGHT, 50d))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .textSizeConstraints(o -> SIZE_CONSTRAINTS)
                .build();
        final Object bean = mock(Object.class);
        tested.handle(bean, view);
        verify(view).setTitleStrokeColor("strokeColor");
        verify(view).setTitleStrokeWidth(5d);
        verify(view).setTitleStrokeAlpha(0.77d);
        verify(view).setTitleFontColor("fontColor");
        verify(view).setTitleFontFamily("fontFamily");
        verify(view).setTitleFontSize(10.5d);
        verify(view).setTitleAlpha(0.7d);
        verify(view).setTitleRotation(270d);
        verify(view).setTitleXOffsetPosition(X_OFFSET);
        verify(view).setTitleYOffsetPosition(Y_OFFSET);
        verify(view).setTitleWrapper(TextWrapperStrategy.NO_WRAP);
        verify(view).setTitlePosition(HasTitle.VerticalAlignment.TOP, HasTitle.HorizontalAlignment.LEFT,
                                      HasTitle.ReferencePosition.OUTSIDE, HasTitle.Orientation.VERTICAL);
        verify(view).setMargins(Stream.of(new AbstractMap.SimpleEntry<>(HasTitle.VerticalAlignment.TOP, 10d),
                                          new AbstractMap.SimpleEntry<>(HasTitle.HorizontalAlignment.LEFT, 10d),
                                          new AbstractMap.SimpleEntry<>(HasTitle.HorizontalAlignment.RIGHT, 50d))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        verify(view).setTitleSizeConstraints(SIZE_CONSTRAINTS);
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
                .positionXOffset(o -> null)
                .positionYOffset(o -> null)
                .verticalAlignment(o -> null)
                .horizontalAlignment(o -> null)
                .referencePosition(o -> null)
                .orientation(o -> null)
                .margins(o -> null)
                .textSizeConstraints(o -> null)

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
        verify(view, never()).setTitleXOffsetPosition(anyDouble());
        verify(view, never()).setTitleYOffsetPosition(anyDouble());
        verify(view, never()).setTitleWrapper(any());
        verify(view, never()).setTitlePosition(any(), any(), any(), any());
        verify(view, never()).setMargins(any());
        verify(view, never()).setTitleSizeConstraints(any());
    }
}
