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

package org.kie.workbench.common.dmn.client.shape.def;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionService;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.client.resources.DMNDecisionServiceSVGViewFactory;
import org.kie.workbench.common.dmn.client.resources.DMNSVGGlyphFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette.PaletteGlyphConsumer;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kie.workbench.common.dmn.api.property.dimensions.DecisionServiceRectangleDimensionsSet.DEFAULT_HEIGHT;
import static org.kie.workbench.common.dmn.api.property.dimensions.DecisionServiceRectangleDimensionsSet.DEFAULT_WIDTH;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDecisionServiceSVGShapeDefImplTest {

    private static final String DEFINITION_ID = "definition-id";

    @Mock
    private DMNDecisionServiceSVGViewFactory viewFactory;

    @Mock
    private SVGShapeViewResource viewResource;

    private DMNDecisionServiceSVGShapeDefImpl shapeDef;

    @Before
    public void setup() {
        this.shapeDef = new DMNDecisionServiceSVGShapeDefImpl();

        when(viewFactory.decisionService()).thenReturn(viewResource);
    }

    @Test
    public void testNewViewInstance() {
        final DecisionService decisionService = new DecisionService();
        shapeDef.newViewInstance(viewFactory, decisionService);

        verify(viewFactory).decisionService();
        verify(viewResource).build(decisionService.getDimensionsSet().getWidth().getValue(),
                                   decisionService.getDimensionsSet().getHeight().getValue(),
                                   true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNewSizeHandler() {
        final DecisionService decisionService = new DecisionService();
        final View<DecisionService> view = new ViewImpl<>(decisionService, Bounds.create(0,
                                                                                         0,
                                                                                         DEFAULT_WIDTH,
                                                                                         DEFAULT_HEIGHT));
        final SVGShapeView<?> shapeView = mock(SVGShapeView.class);

        final SizeHandler<DecisionService, SVGShapeView> handler = shapeDef.newSizeHandler();
        handler.handle(view, shapeView);

        verify(shapeView).setMinWidth(decisionService.getDimensionsSet().getMinimumWidth());
        verify(shapeView).setMaxWidth(decisionService.getDimensionsSet().getMaximumWidth());
        verify(shapeView).setMinHeight(decisionService.getDividerLineY().getValue() + GeneralRectangleDimensionsSet.DEFAULT_HEIGHT);
        verify(shapeView).setMaxHeight(decisionService.getDimensionsSet().getMaximumHeight());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNewFontHandler() {
        final DecisionService decisionService = new DecisionService();
        final SVGShapeView<?> shapeView = mock(SVGShapeView.class);

        final FontHandler<DecisionService, SVGShapeView> handler = shapeDef.newFontHandler();
        handler.handle(decisionService, shapeView);

        verify(shapeView).setTitlePosition(HasTitle.Position.TOP);
        verify(shapeView).setTitleYOffsetPosition(DMNDecisionServiceSVGShapeDefImpl.Y_OFFSET);
        verify(shapeView).setTextWrapper(TextWrapperStrategy.TRUNCATE);
    }

    @Test
    public void testGetToolboxGlyph() {
        assertThat(shapeDef.getGlyph(DecisionService.class, DEFINITION_ID)).isInstanceOf(ShapeGlyph.class);
    }

    @Test
    public void testGetPaletteGlyphWithConsumer() {
        assertThat(shapeDef.getGlyph(DecisionService.class, PaletteGlyphConsumer.class, DEFINITION_ID)).isEqualTo(DMNSVGGlyphFactory.DECISION_SERVICE_PALETTE);
    }
}