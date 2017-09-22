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
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasFillGradient;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ShapeViewHandlerTest {

    private static final String COLOR = "#AABBCC";
    private static final Double DOUBLE_VALUE = 0.2d;

    ShapeView view;
    ShapeViewExtStub viewExt;

    private ShapeViewHandler<ShapeView> tested;
    private ShapeViewHandler<ShapeViewExtStub> testedExt;

    @Before
    public void setup() throws Exception {
        view = spy(new ShapeViewStub());
        viewExt = spy(new ShapeViewExtStub());
        this.tested = new ShapeViewHandler<ShapeView>(view);
        this.testedExt = new ShapeViewHandler<ShapeViewExtStub>(viewExt);
        assertEquals(view,
                     tested.getShapeView());
        assertEquals(viewExt,
                     testedExt.getShapeView());
    }

    @Test
    public void testApplyFillColorNone() {
        tested.applyFillColor("",
                              MutationContext.STATIC);
        verify(view,
               times(0)).setFillColor(anyString());
        verify(viewExt,
               times(0)).setFillGradient(any(HasFillGradient.Type.class),
                                         anyString(),
                                         anyString());
    }

    @Test
    public void testApplyFillColor() {
        tested.applyFillColor(COLOR,
                              MutationContext.STATIC);
        verify(view,
               times(1)).setFillColor(eq(COLOR));
        verify(viewExt,
               times(0)).setFillGradient(any(HasFillGradient.Type.class),
                                         anyString(),
                                         anyString());
    }

    @Test
    public void testApplyFillGradient() {
        testedExt.applyFillColor(COLOR,
                                 MutationContext.STATIC);
        verify(viewExt,
               times(1)).setFillGradient(eq(HasFillGradient.Type.LINEAR),
                                         eq(COLOR),
                                         eq("#FFFFFF"));
        verify(view,
               times(0)).setFillColor(anyString());
    }

    @Test
    public void applyFillAlphaNone() {
        tested.applyFillAlpha(null,
                              MutationContext.STATIC);
        verify(view,
               times(0)).setFillAlpha(anyDouble());
    }

    @Test
    public void applyFillAlpha() {
        tested.applyFillAlpha(DOUBLE_VALUE,
                              MutationContext.STATIC);
        verify(view,
               times(1)).setFillAlpha(eq(DOUBLE_VALUE));
    }

    @Test
    public void applyBorderAlphaNone() {
        tested.applyBorderAlpha(null,
                                MutationContext.STATIC);
        verify(view,
               times(0)).setStrokeAlpha(anyDouble());
    }

    @Test
    public void applyBorderAlpha() {
        tested.applyBorderAlpha(DOUBLE_VALUE,
                                MutationContext.STATIC);
        verify(view,
               times(1)).setStrokeAlpha(eq(DOUBLE_VALUE));
    }

    @Test
    public void applyBordersNone() {
        tested.applyBorders(null,
                            null,
                            MutationContext.STATIC);
        verify(view,
               times(0)).setStrokeColor(anyString());
        verify(view,
               times(0)).setStrokeWidth(anyDouble());
    }

    @Test
    public void applyBorders() {
        tested.applyBorders(COLOR,
                            DOUBLE_VALUE,
                            MutationContext.STATIC);
        verify(view,
               times(1)).setStrokeColor(eq(COLOR));
        verify(view,
               times(1)).setStrokeWidth(eq(DOUBLE_VALUE));
    }

    @Test
    public void applyFontNone() {
        testedExt.applyFont(null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            MutationContext.STATIC);
        verify(viewExt,
               times(0)).setTitleFontFamily(anyString());
        verify(viewExt,
               times(0)).setTitleFontColor(anyString());
        verify(viewExt,
               times(0)).setTitleStrokeColor(anyString());
        verify(viewExt,
               times(0)).setTitleFontSize(anyDouble());
        verify(viewExt,
               times(0)).setTitleStrokeWidth(anyDouble());
        verify(viewExt,
               times(0)).setTitleAlpha(anyDouble());
        verify(viewExt,
               times(0)).setTitlePosition(any(HasTitle.Position.class));
        verify(viewExt,
               times(0)).setTitleRotation(anyDouble());
    }

    @Test
    public void applyFont() {
        testedExt.applyFont("family",
                            "fontColor",
                            COLOR,
                            DOUBLE_VALUE,
                            0.3d,
                            0.4,
                            HasTitle.Position.BOTTOM,
                            90d,
                            MutationContext.STATIC);
        verify(viewExt,
               times(1)).setTitleFontFamily(eq("family"));
        verify(viewExt,
               times(1)).setTitleFontColor(eq("fontColor"));
        verify(viewExt,
               times(1)).setTitleStrokeColor(eq(COLOR));
        verify(viewExt,
               times(1)).setTitleFontSize(eq(DOUBLE_VALUE));
        verify(viewExt,
               times(1)).setTitleStrokeWidth(eq(0.3d));
        verify(viewExt,
               times(1)).setTitleAlpha(eq(0.4d));
        verify(viewExt,
               times(1)).setTitlePosition(eq(HasTitle.Position.BOTTOM));
        verify(viewExt,
               times(1)).setTitleRotation(90d);
    }

    @Test
    public void applySize() {
        testedExt.applySize(100d,
                            200d,
                            MutationContext.STATIC);
        verify(viewExt,
               times(1)).setSize(eq(100d),
                                 eq(200d));
    }

    @Test
    public void applyRadius() {
        testedExt.applyRadius(50d,
                              MutationContext.STATIC);
        verify(viewExt,
               times(1)).setRadius(eq(50d));
    }
}
