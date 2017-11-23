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

package org.kie.workbench.common.stunner.svg.client.shape.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitivePolicy;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeStateHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGMutableShapeImplTest {

    private static final String CV1 = "viewChild1";
    private static final String CV2 = "viewChild2";

    @Mock
    private Object definition;

    @Mock
    private View<Object> content;

    @Mock
    private Node<View<Object>, Edge> node;

    @Mock
    private SVGShapeViewImpl view;

    @Mock
    private SVGShapeStateHandler svgShapeStateHandler;

    @Mock
    private SVGShapeViewDef<Object, ?> shapeDef;

    @Mock
    private BiConsumer<Object, SVGShapeView> viewHandler;

    @Mock
    private SVGBasicShapeView child1;

    @Mock
    private SVGPrimitiveShape childPrim1;

    @Mock
    private Shape childShape1;

    @Mock
    private SVGPrimitivePolicy childPolicy1;

    @Mock
    private SVGBasicShapeView child2;

    @Mock
    private SVGPrimitiveShape childPrim2;

    @Mock
    private Shape childShape2;

    @Mock
    private SVGPrimitivePolicy childPolicy2;

    @Mock
    private SVGPrimitiveShape prim1;

    @Mock
    private Shape primShape1;

    @Mock
    private SVGPrimitivePolicy primPolicy1;

    @Mock
    private SVGPrimitiveShape prim2;

    @Mock
    private Shape primShape2;

    @Mock
    private SVGPrimitivePolicy primPolicy2;

    private final Collection<SVGPrimitive<?>> primChildren = new LinkedList<>();

    private final Collection<SVGBasicShapeView> viewChildren = new LinkedList<>();

    private SVGMutableShapeImpl<Object, SVGShapeViewDef<Object, ?>> tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);
        when(view.getSVGChildren()).thenReturn(viewChildren);
        when(view.getChildren()).thenReturn(primChildren);
        when(view.getShapeStateHandler()).thenReturn(svgShapeStateHandler);
        when(shapeDef.titleHandler()).thenReturn(Optional.empty());
        when(shapeDef.fontHandler()).thenReturn(Optional.empty());
        when(shapeDef.sizeHandler()).thenReturn(Optional.empty());
        when(shapeDef.viewHandler()).thenReturn(viewHandler);
        when(svgShapeStateHandler.shapeUpdated()).thenReturn(svgShapeStateHandler);
        when(child1.getName()).thenReturn(CV1);
        when(child1.getPrimitive()).thenReturn(childPrim1);
        when(childPrim1.get()).thenReturn(childShape1);
        when(childPrim1.getPolicy()).thenReturn(childPolicy1);
        when(child2.getName()).thenReturn(CV2);
        when(child2.getPrimitive()).thenReturn(childPrim2);
        when(childPrim2.get()).thenReturn(childShape2);
        when(childPrim2.getPolicy()).thenReturn(childPolicy2);
        when(prim1.get()).thenReturn(primShape1);
        when(prim1.getPolicy()).thenReturn(primPolicy1);
        when(prim2.get()).thenReturn(primShape2);
        when(prim2.getPolicy()).thenReturn(primPolicy2);
        primChildren.add(prim1);
        primChildren.add(prim2);
        viewChildren.add(child1);
        viewChildren.add(child2);
        this.tested = new SVGMutableShapeImpl<>(shapeDef,
                                                view);
    }

    @Test
    public void testApplyCustomSVGProperties() {
        tested.applyProperties(node,
                               MutationContext.STATIC);
        verify(childPolicy1, times(1)).accept(view, childShape1);
        verify(childPolicy2, times(1)).accept(view, childShape2);
        verify(primPolicy1, times(1)).accept(view, primShape1);
        verify(primPolicy2, times(1)).accept(view, primShape2);
    }

    @Test
    public void testUseCustomStateHandler() {
        assertTrue(tested.getShape().getShapeStateHandler() instanceof SVGShapeStateHandler);
    }
}
