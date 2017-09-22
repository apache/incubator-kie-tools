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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShapeDefViewHandlerTest {

    private static final String COLOR = "#AABBCC";
    private static final String COLOR2 = "#AABBCD";
    private static final Double DOUBLE_VALUE = 0.2d;

    @Mock
    MutableShapeDef<Object> def;

    @Mock
    ShapeViewHandler<ShapeViewExtStub> viewHandler;

    private ShapeDefViewHandler<Object, ShapeViewExtStub, MutableShapeDef<Object>> tested;

    @Before
    public void setup() throws Exception {
        when(def.getBackgroundColor(any(Object.class))).thenReturn(COLOR);
        when(def.getBackgroundAlpha(any(Object.class))).thenReturn(DOUBLE_VALUE);
        when(def.getBorderColor(any(Object.class))).thenReturn(COLOR);
        when(def.getBorderSize(any(Object.class))).thenReturn(DOUBLE_VALUE);
        when(def.getBorderAlpha(any(Object.class))).thenReturn(DOUBLE_VALUE);
        when(def.getFontFamily(any(Object.class))).thenReturn(COLOR);
        when(def.getFontColor(any(Object.class))).thenReturn(COLOR);
        when(def.getFontSize(any(Object.class))).thenReturn(DOUBLE_VALUE);
        when(def.getFontBorderSize(any(Object.class))).thenReturn(DOUBLE_VALUE);
        when(def.getFontBorderColor(any(Object.class))).thenReturn(COLOR2);
        when(def.getFontPosition(any(Object.class))).thenReturn(HasTitle.Position.BOTTOM);
        when(def.getFontRotation(any(Object.class))).thenReturn(DOUBLE_VALUE);
        this.tested = new ShapeDefViewHandler<Object, ShapeViewExtStub, MutableShapeDef<Object>>(def,
                                                                                                 viewHandler);
    }

    @Test
    public void testApplyProperties() {
        tested.applyProperties(mock(Object.class),
                               MutationContext.STATIC);
        tested.applyTitle("newTitle",
                          mock(Object.class),
                          MutationContext.STATIC);
        verify(viewHandler,
               times(1)).applyFillColor(eq(COLOR),
                                        eq(MutationContext.STATIC));
        verify(viewHandler,
               times(1)).applyFillAlpha(eq(DOUBLE_VALUE),
                                        eq(MutationContext.STATIC));
        verify(viewHandler,
               times(1)).applyBorders(eq(COLOR),
                                      eq(DOUBLE_VALUE),
                                      eq(MutationContext.STATIC));
        verify(viewHandler,
               times(1)).applyBorderAlpha(eq(DOUBLE_VALUE),
                                          eq(MutationContext.STATIC));
        verify(viewHandler,
               times(1)).applyTitle(eq("newTitle"),
                                    eq(MutationContext.STATIC));
        verify(viewHandler,
               times(1)).applyFont(eq(COLOR),
                                   eq(COLOR),
                                   eq(COLOR2),
                                   eq(DOUBLE_VALUE),
                                   eq(DOUBLE_VALUE),
                                   eq(1d),
                                   eq(HasTitle.Position.BOTTOM),
                                   eq(DOUBLE_VALUE),
                                   eq(MutationContext.STATIC));
    }
}
