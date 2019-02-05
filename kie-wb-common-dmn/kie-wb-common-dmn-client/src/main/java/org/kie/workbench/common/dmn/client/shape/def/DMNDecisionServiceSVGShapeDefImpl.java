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

package org.kie.workbench.common.dmn.client.shape.def;

import java.util.Map;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionService;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.client.resources.DMNDecisionServiceSVGViewFactory;
import org.kie.workbench.common.dmn.client.resources.DMNSVGGlyphFactory;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class DMNDecisionServiceSVGShapeDefImpl implements DMNDecisionServiceSVGShapeDef {

    static final double Y_OFFSET = 20.0;

    public static final SVGShapeViewResources<DMNDefinition, DMNDecisionServiceSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<DMNDefinition, DMNDecisionServiceSVGViewFactory>()
                    .put(DecisionService.class,
                         DMNDecisionServiceSVGViewFactory::decisionService);

    public static final Map<Class<? extends DMNDefinition>, Glyph> GLYPHS_PALETTE =
            new Maps.Builder<Class<? extends DMNDefinition>, Glyph>()
                    .put(DecisionService.class,
                         DMNSVGGlyphFactory.DECISION_SERVICE_PALETTE)
                    .build();

    @Override
    public Class<DMNDecisionServiceSVGViewFactory> getViewFactoryType() {
        return DMNDecisionServiceSVGViewFactory.class;
    }

    @Override
    public SizeHandler<DecisionService, SVGShapeView> newSizeHandler() {
        return new SizeHandler.Builder<DecisionService, SVGShapeView>().width(e -> e.getDimensionsSet().getWidth().getValue())
                .height(e -> e.getDimensionsSet().getHeight().getValue())
                .minWidth(p -> p.getDimensionsSet().getMinimumWidth())
                .maxWidth(p -> p.getDimensionsSet().getMaximumWidth())
                .minHeight(p -> p.getDividerLineY().getValue() + GeneralRectangleDimensionsSet.DEFAULT_HEIGHT)
                .maxHeight(p -> p.getDimensionsSet().getMaximumHeight())
                .build();
    }

    @Override
    public FontHandler<DecisionService, SVGShapeView> newFontHandler() {
        return new FontHandler.Builder<DecisionService, SVGShapeView>()
                .fontFamily(bean -> bean.getFontSet().getFontFamily().getValue())
                .fontColor(bean -> bean.getFontSet().getFontColour().getValue())
                .fontSize(bean -> bean.getFontSet().getFontSize().getValue())
                .textWrapperStrategy(bean -> TextWrapperStrategy.TRUNCATE)
                .strokeAlpha(bean -> 0.0d)
                .position(bean -> HasTitle.Position.TOP)
                .positionYOffset(bean -> Y_OFFSET)
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final DMNDecisionServiceSVGViewFactory factory,
                                           final DecisionService bean) {
        final double width = bean.getDimensionsSet().getWidth().getValue();
        final double height = bean.getDimensionsSet().getHeight().getValue();
        return VIEW_RESOURCES
                .getResource(factory, bean)
                .build(width, height, true);
    }

    @Override
    public Glyph getGlyph(final Class<? extends DecisionService> type,
                          final Class<? extends ShapeFactory.GlyphConsumer> consumer,
                          final String defId) {
        if (org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette.PaletteGlyphConsumer.class.equals(consumer)) {
            return GLYPHS_PALETTE.computeIfAbsent(type, (t) -> ShapeGlyph.create());
        }
        return getGlyph(type, defId);
    }
}
