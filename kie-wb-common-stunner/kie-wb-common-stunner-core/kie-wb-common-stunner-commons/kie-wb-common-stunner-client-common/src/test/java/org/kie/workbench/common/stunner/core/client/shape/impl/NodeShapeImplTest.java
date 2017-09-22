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
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasFillGradient;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeShapeImplTest {

    private final static String COLOR = "c1";
    private final static String COLOR2 = "c2";

    @Mock
    MutableShapeDef<Object> def;
    @Mock
    Node<View<Object>, Edge> element;
    @Mock
    Object definition;
    @Mock
    View<Object> content;
    @Mock
    Bounds bounds;

    private ShapeViewExtStub view;
    private NodeShapeImpl<Object, MutableShapeDef<Object>, ShapeView<?>> tested;

    @Before
    public void setup() throws Exception {
        when(def.getBackgroundColor(any(Object.class))).thenReturn(COLOR);
        when(def.getBackgroundAlpha(any(Object.class))).thenReturn(1d);
        when(def.getFontFamily(any(Object.class))).thenReturn(COLOR);
        when(def.getFontColor(any(Object.class))).thenReturn(COLOR);
        when(def.getFontSize(any(Object.class))).thenReturn(1d);
        when(def.getFontBorderSize(any(Object.class))).thenReturn(1d);
        when(def.getFontPosition(any(Object.class))).thenReturn(HasTitle.Position.BOTTOM);
        when(def.getFontRotation(any(Object.class))).thenReturn(1d);
        when(element.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);
        when(content.getBounds()).thenReturn(bounds);
        when(bounds.getUpperLeft()).thenReturn(new BoundImpl(10d,
                                                             20d));
        when(bounds.getLowerRight()).thenReturn(new BoundImpl(50d,
                                                              60d));

        this.view = spy(new ShapeViewExtStub());

        mockBorderProperties(COLOR,
                             2d,
                             1d);

        this.tested = new NodeShapeImpl<Object, MutableShapeDef<Object>, ShapeView<?>>(def,
                                                                                       view);
    }

    private void mockBorderProperties(final String borderColour,
                                      final double borderWidth,
                                      final double borderAlpha) {
        when(def.getBorderColor(any(Object.class))).thenReturn(borderColour);
        when(def.getBorderSize(any(Object.class))).thenReturn(borderWidth);
        when(def.getBorderAlpha(any(Object.class))).thenReturn(borderAlpha);
        when(view.getStrokeColor()).thenReturn(borderColour);
        when(view.getStrokeWidth()).thenReturn(borderWidth);
        when(view.getStrokeAlpha()).thenReturn(borderAlpha);
    }

    @Test
    public void testApplyPosition() {
        tested.applyPosition(element,
                             MutationContext.STATIC);
        verify(view,
               times(1)).setShapeX(10d);
        verify(view,
               times(1)).setShapeY(20d);
    }

    @Test
    public void testApplyProperties() {
        tested.applyProperties(element,
                               MutationContext.STATIC);
        verify(view,
               times(1)).setFillGradient(any(HasFillGradient.Type.class),
                                         eq(COLOR),
                                         anyString());
        verify(view,
               times(1)).setFillAlpha(eq(1d));
        verify(view,
               times(1)).setStrokeColor(eq(COLOR));
        verify(view,
               times(1)).setStrokeWidth(eq(2d));
        verify(view,
               times(1)).setStrokeAlpha(eq(1d));
    }

    @Test
    public void testApplyState() {
        tested.applyProperties(element,
                               MutationContext.STATIC);
        tested.applyState(ShapeState.INVALID);
        verify(view,
               times(1)).setStrokeColor(eq(ShapeState.INVALID.getColor()));
        verify(view,
               times(2)).setStrokeWidth(anyDouble());
        verify(view,
               times(2)).setStrokeAlpha(eq(1d));
    }

    @Test
    public void testChangeState() {
        tested.applyProperties(element,
                               MutationContext.STATIC);
        tested.applyState(ShapeState.INVALID);
        tested.applyState(ShapeState.NONE);

        verify(view,
               times(1)).setStrokeColor(eq(ShapeState.INVALID.getColor()));
        verify(view,
               times(2)).setStrokeColor(eq(COLOR));
        verify(view,
               times(3)).setStrokeWidth(anyDouble());
        verify(view,
               times(3)).setStrokeAlpha(eq(1d));
    }

    @Test
    public void statePropertiesUpdatedForSelectedShape() {
        //Default mocked values for selection
        tested.applyState(ShapeState.SELECTED);

        verify(view,
               times(1)).setStrokeColor(eq(ShapeState.SELECTED.getColor()));
        //Selected border width = shapeDef stroke width * 2
        verify(view,
               times(1)).setStrokeWidth(eq(4d));
        verify(view,
               atLeast(1)).setStrokeAlpha(eq(1d));

        //New mocked values emulating a change to the Shapes properties
        mockBorderProperties(COLOR2,
                             5d,
                             0.5d);
        tested.applyProperties(element,
                               MutationContext.STATIC);

        verify(view,
               times(1)).setStrokeColor(eq(COLOR2));
        verify(view,
               times(1)).setStrokeWidth(eq(5d));
        verify(view,
               times(1)).setStrokeAlpha(eq(0.5d));

        //Mock deselection
        tested.applyState(ShapeState.NONE);

        verify(view,
               times(2)).setStrokeColor(eq(COLOR2));
        verify(view,
               times(2)).setStrokeWidth(eq(5d));
        verify(view,
               times(2)).setStrokeAlpha(eq(0.5d));

        //Mock re-selection
        tested.applyState(ShapeState.SELECTED);

        verify(view,
               times(2)).setStrokeColor(eq(ShapeState.SELECTED.getColor()));
        //Selected border width = shapeDef stroke width * 2
        verify(view,
               times(1)).setStrokeWidth(eq(10d));
        verify(view,
               times(2)).setStrokeAlpha(eq(0.5d));
    }
}
