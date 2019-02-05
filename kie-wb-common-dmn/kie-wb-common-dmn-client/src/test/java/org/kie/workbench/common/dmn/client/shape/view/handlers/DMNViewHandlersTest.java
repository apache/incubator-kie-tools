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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.Height;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.Width;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.client.shape.def.DMNSVGShapeDefImpl;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNViewHandlersTest {

    @Mock
    private View<DMNViewDefinition> view;

    @Mock
    private DMNViewDefinition definition;

    @Mock
    private SVGShapeView shape;

    private Bounds bounds = Bounds.create(0.0, 0.0, 100.0, 100.0);

    private RectangleDimensionsSet dimensions = new GeneralRectangleDimensionsSet(new Width(50.0),
                                                                                  new Height(50.0));

    @Before
    public void setup() {
        doReturn(definition).when(view).getDefinition();
        doReturn(bounds).when(view).getBounds();

        doReturn(dimensions).when(definition).getDimensionsSet();

        doReturn(new FontSet()).when(definition).getFontSet();
    }

    @Test
    public void testNewSizeHandler() {
        final SizeHandler<DMNViewDefinition, SVGShapeView> handler = new DMNSVGShapeDefImpl().newSizeHandler();
        handler.handle(view,
                       shape);

        verify(shape).setMinWidth(eq(dimensions.getMinimumWidth()));
        verify(shape).setMaxWidth(eq(dimensions.getMaximumWidth()));
        verify(shape).setMinHeight(eq(dimensions.getMinimumHeight()));
        verify(shape).setMaxHeight(eq(dimensions.getMaximumHeight()));
    }

    @Test
    public void testNewFontHandler() {

        final FontHandler<DMNViewDefinition, SVGShapeView> handler = new DMNSVGShapeDefImpl().newFontHandler();
        handler.handle(definition,
                       shape);

        verify(shape).setTextWrapper(TextWrapperStrategy.TRUNCATE);
    }
}
