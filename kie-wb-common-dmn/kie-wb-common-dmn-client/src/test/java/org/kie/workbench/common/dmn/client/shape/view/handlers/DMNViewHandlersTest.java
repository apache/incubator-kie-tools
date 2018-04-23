/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.shape.view.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.property.dimensions.Height;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.Width;
import org.kie.workbench.common.dmn.client.shape.def.DMNSVGShapeDefImpl;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DMNViewHandlersTest {

    @Mock
    private View<DMNViewDefinition> view;

    @Mock
    private DMNViewDefinition definition;

    @Mock
    private SVGShapeView shape;

    private Bounds bounds = new BoundsImpl(new BoundImpl(0.0, 0.0),
                                           new BoundImpl(100.0, 100.0));

    private RectangleDimensionsSet dimensions = new RectangleDimensionsSet(new Width(50.0),
                                                                           new Height(50.0));

    @Before
    public void setup() {
        doReturn(definition).when(view).getDefinition();
        doReturn(bounds).when(view).getBounds();

        doReturn(dimensions).when(definition).getDimensionsSet();
    }

    @Test
    public void testNewSizeHandler() {
        final SizeHandler<DMNViewDefinition, SVGShapeView> handler = new DMNSVGShapeDefImpl().newSizeHandler();
        handler.handle(view,
                       shape);

        verify(shape).setMinWidth(eq(Width.MIN));
        verify(shape).setMaxWidth(eq(Width.MAX));
        verify(shape).setMinHeight(eq(Height.MIN));
        verify(shape).setMaxHeight(eq(Height.MAX));

    }
}
