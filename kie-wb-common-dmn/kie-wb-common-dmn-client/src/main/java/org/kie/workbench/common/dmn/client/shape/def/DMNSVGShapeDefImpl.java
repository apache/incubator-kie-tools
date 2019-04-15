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
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionService;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.dmn.client.resources.DMNSVGGlyphFactory;
import org.kie.workbench.common.dmn.client.resources.DMNSVGViewFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class DMNSVGShapeDefImpl implements DMNSVGShapeDef<DMNViewDefinition, DMNSVGViewFactory> {

    public static final SVGShapeViewResources<DMNDefinition, DMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<DMNDefinition, DMNSVGViewFactory>()
                    .put(BusinessKnowledgeModel.class,
                         DMNSVGViewFactory::businessKnowledgeModel)
                    .put(Decision.class,
                         DMNSVGViewFactory::decision)
                    .put(DMNDiagram.class,
                         DMNSVGViewFactory::diagram)
                    .put(InputData.class,
                         DMNSVGViewFactory::inputData)
                    .put(KnowledgeSource.class,
                         DMNSVGViewFactory::knowledgeSource)
                    .put(TextAnnotation.class,
                         DMNSVGViewFactory::textAnnotation);

    public static final Map<Class<? extends DMNDefinition>, Glyph> GLYPHS_TOOLBOX =
            new Maps.Builder<Class<? extends DMNDefinition>, Glyph>()
                    .put(BusinessKnowledgeModel.class,
                         DMNSVGGlyphFactory.BUSINESS_KNOWLEDGE_MODEL_TOOLBOX)
                    .put(Decision.class,
                         DMNSVGGlyphFactory.DECISION_TOOLBOX)
                    .put(DMNDiagram.class,
                         DMNSVGGlyphFactory.DIAGRAM_TOOLBOX)
                    .put(InputData.class,
                         DMNSVGGlyphFactory.INPUT_DATA_TOOLBOX)
                    .put(KnowledgeSource.class,
                         DMNSVGGlyphFactory.KNOWLEDGE_SOURCE_TOOLBOX)
                    .put(TextAnnotation.class,
                         DMNSVGGlyphFactory.TEXT_ANNOTATION_TOOLBOX)
                    .build();

    public static final Map<Class<? extends DMNDefinition>, Glyph> GLYPHS_PALETTE =
            new Maps.Builder<Class<? extends DMNDefinition>, Glyph>()
                    .put(BusinessKnowledgeModel.class,
                         DMNSVGGlyphFactory.BUSINESS_KNOWLEDGE_MODEL_PALETTE)
                    .put(Decision.class,
                         DMNSVGGlyphFactory.DECISION_PALETTE)
                    .put(InputData.class,
                         DMNSVGGlyphFactory.INPUT_DATA_PALETTE)
                    .put(DecisionService.class,
                         DMNSVGGlyphFactory.DECISION_SERVICE_PALETTE)
                    .put(KnowledgeSource.class,
                         DMNSVGGlyphFactory.KNOWLEDGE_SOURCE_PALETTE)
                    .put(TextAnnotation.class,
                         DMNSVGGlyphFactory.TEXT_ANNOTATION_PALETTE)
                    .build();

    @Override
    public Class<DMNSVGViewFactory> getViewFactoryType() {
        return DMNSVGViewFactory.class;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final DMNSVGViewFactory factory,
                                           final DMNViewDefinition bean) {
        final double width = bean.getDimensionsSet().getWidth().getValue();
        final double height = bean.getDimensionsSet().getHeight().getValue();
        return VIEW_RESOURCES
                .getResource(factory, bean)
                .build(width, height, true);
    }

    @Override
    public Glyph getGlyph(final Class<? extends DMNViewDefinition> type,
                          final String defId) {
        return GLYPHS_TOOLBOX.computeIfAbsent(type, (t) -> ShapeGlyph.create());
    }

    @Override
    public Glyph getGlyph(final Class<? extends DMNViewDefinition> type,
                          final Class<? extends ShapeFactory.GlyphConsumer> consumer,
                          final String defId) {
        if (org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette.PaletteGlyphConsumer.class.equals(consumer)) {
            return GLYPHS_PALETTE.computeIfAbsent(type, (t) -> ShapeGlyph.create());
        }
        return getGlyph(type, defId);
    }
}
