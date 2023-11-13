/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.shape.def;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
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
            Stream.of(new AbstractMap.SimpleEntry<>(DecisionService.class, DMNSVGGlyphFactory.DECISION_SERVICE_PALETTE))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

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
                .fontFamily(bean -> bean.getStylingSet().getFontFamily().getValue())
                .fontColor(bean -> bean.getStylingSet().getFontColour().getValue())
                .fontSize(bean -> bean.getStylingSet().getFontSize().getValue())
                .textWrapperStrategy(bean -> TextWrapperStrategy.TRUNCATE)
                .strokeAlpha(bean -> 0.0d)
                .verticalAlignment(bean -> HasTitle.VerticalAlignment.TOP)
                .horizontalAlignment(bean -> HasTitle.HorizontalAlignment.CENTER)
                .referencePosition(bean -> HasTitle.ReferencePosition.INSIDE)
                .orientation(bean -> HasTitle.Orientation.HORIZONTAL)
                .margin(HasTitle.VerticalAlignment.TOP, Y_OFFSET)
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
            return GLYPHS_PALETTE.computeIfAbsent(type, t -> ShapeGlyph.create());
        }
        return getGlyph(type, defId);
    }
}
