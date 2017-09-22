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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.impl.AnimationShapeStateHelper;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGMutableShapeImplTest {

    private static final String CV1 = "viewChild1";
    private static final String CV2 = "viewChild2";
    private static final String CV3 = "viewChild3";

    @Mock
    Object definition;

    @Mock
    View<Object> content;

    @Mock
    Node<View<Object>, Edge> node;

    @Mock
    SVGShapeViewImpl view;

    @Mock
    SVGBasicShapeView child1;

    @Mock
    SVGBasicShapeView child2;

    @Mock
    SVGBasicShapeView child3;

    private final Collection<SVGBasicShapeView> viewChildren = new LinkedList<>();

    private SVGMutableShapeImpl<Object, SVGMutableShapeDef<Object, ?>> tested;

    @Before
    public void setup() throws Exception {
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);
        when(view.getSVGChildren()).thenReturn(viewChildren);
        when(child1.getName()).thenReturn(CV1);
        when(child2.getName()).thenReturn(CV2);
        when(child3.getName()).thenReturn(CV3);
        viewChildren.add(child1);
        viewChildren.add(child2);
        viewChildren.add(child3);
        this.tested = new SVGMutableShapeImpl<>(shapeDef,
                                                view);
    }

    @Test
    public void testApplyCustomSVGProperties() {
        tested.applyProperties(node,
                               MutationContext.STATIC);
        verify(view,
               times(1)).setSize(eq(100d),
                                 eq(100d));
        verify(child1,
               times(1)).setAlpha(eq(1d));
        verify(child2,
               times(1)).setAlpha(eq(0d));
        verify(child3,
               times(1)).setAlpha(eq(1d));
    }

    @Test
    public void testUseAnimatedStates() {
        assertTrue(tested.getShape().getShapeStateHelper() instanceof AnimationShapeStateHelper);
    }

    private SVGMutableShapeDef<Object, ?> shapeDef = new SVGMutableShapeDef<Object, Object>() {
        @Override
        public double getWidth(final Object element) {
            return 100d;
        }

        @Override
        public double getHeight(final Object element) {
            return 100d;
        }

        @Override
        public boolean isSVGViewVisible(final String viewName,
                                        final Object element) {
            switch (viewName) {
                case CV1:
                    return true;
                case CV2:
                    return false;
                case CV3:
                    return true;
            }
            return false;
        }

        @Override
        public double getAlpha(final Object element) {
            return 0.1;
        }

        @Override
        public String getBackgroundColor(final Object element) {
            return "color1";
        }

        @Override
        public double getBackgroundAlpha(final Object element) {
            return 0;
        }

        @Override
        public String getBorderColor(final Object element) {
            return null;
        }

        @Override
        public double getBorderSize(final Object element) {
            return 0;
        }

        @Override
        public double getBorderAlpha(final Object element) {
            return 0;
        }

        @Override
        public String getFontFamily(final Object element) {
            return null;
        }

        @Override
        public String getFontColor(final Object element) {
            return null;
        }

        @Override
        public String getFontBorderColor(final Object element) {
            return null;
        }

        @Override
        public double getFontSize(final Object element) {
            return 0;
        }

        @Override
        public double getFontBorderSize(final Object element) {
            return 0;
        }

        @Override
        public HasTitle.Position getFontPosition(final Object element) {
            return null;
        }

        @Override
        public double getFontRotation(final Object element) {
            return 0;
        }

        @Override
        public Class<Object> getViewFactoryType() {
            return null;
        }

        @Override
        public SVGShapeView<?> newViewInstance(final Object factory,
                                               final Object element) {
            return null;
        }
    };
}
