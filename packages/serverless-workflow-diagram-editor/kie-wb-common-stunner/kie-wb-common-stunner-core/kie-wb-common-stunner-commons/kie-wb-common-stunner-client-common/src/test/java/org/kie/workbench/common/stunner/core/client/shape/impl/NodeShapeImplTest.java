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

import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NodeShapeImplTest {

    @Mock
    private Node<View<Object>, Edge> element;

    @Mock
    private Object definition;

    @Mock
    private View<Object> content;

    @Mock
    private Bounds bounds;

    @Mock
    private Function<ShapeState, ShapeStateAttributeHandler.ShapeStateAttributes> stateAttributesProvider;

    private ShapeViewExtStub view;
    private NodeShapeImpl<Object, ShapeView> tested;
    private ShapeImpl<ShapeView> shape;
    private ShapeStateAttributeHandler shapeStateHandler;

    @Before
    public void setup() throws Exception {
        shapeStateHandler = spy(new ShapeStateAttributeHandler());
        doNothing().when(shapeStateHandler).saveState();
        ShapeStateAttributeHandler.ShapeStateAttributes attributes = new ShapeStateAttributeHandler.ShapeStateAttributes();
        when(stateAttributesProvider.apply(any())).thenReturn(attributes);
        shapeStateHandler.useAttributes(stateAttributesProvider);
        when(shapeStateHandler.shapeAttributesChanged()).thenReturn(shapeStateHandler);
        when(element.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);
        when(content.getBounds()).thenReturn(bounds);
        when(bounds.getUpperLeft()).thenReturn(Bound.create(10d,
                                                            20d));
        when(bounds.getLowerRight()).thenReturn(Bound.create(50d,
                                                             60d));

        this.view = spy(new ShapeViewExtStub());

        shape = new ShapeImpl<>(view, shapeStateHandler);
        this.tested = new NodeShapeImpl<>(shape);
    }

    @Test
    public void testApplyPosition() {
        tested.applyPosition(element,
                             MutationContext.STATIC);
        verify(view,
               times(1)).setShapeLocation(new Point2D(10d, 20d));
    }

    @Test
    public void testApplyProperties() {
        when(shapeStateHandler.reset()).thenReturn(ShapeState.NONE);
        clearInvocations(shapeStateHandler);
        tested.applyProperties(element,
                               MutationContext.STATIC);
        verify(shapeStateHandler,
               times(1)).reset();
        verify(shapeStateHandler,
               times(1)).shapeAttributesChanged();
        verify(shapeStateHandler,
               times(1)).applyState(eq(ShapeState.NONE));
    }

    @Test
    public void testApplyState() {
        tested.applyState(ShapeState.INVALID);
        verify(shapeStateHandler,
               times(1)).applyState(eq(ShapeState.INVALID));
    }

    @Test
    public void testApplyStateHideControls() {
        tested.applyState(ShapeState.NONE);
        HasControlPoints hasControlPoints = (HasControlPoints) shape.getShapeView();
        verify(hasControlPoints).hideControlPoints();
    }

    @Test
    public void testDoNotHideControls() {
        tested.applyState(ShapeState.SELECTED);
        HasControlPoints hasControlPoints = (HasControlPoints) shape.getShapeView();
        verify(hasControlPoints, never()).hideControlPoints();
    }
}