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
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
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
            Stream.of(new AbstractMap.SimpleEntry<>(BusinessKnowledgeModel.class, DMNSVGGlyphFactory.BUSINESS_KNOWLEDGE_MODEL_TOOLBOX),
                      new AbstractMap.SimpleEntry<>(Decision.class, DMNSVGGlyphFactory.DECISION_TOOLBOX),
                          new AbstractMap.SimpleEntry<>(DMNDiagram.class, DMNSVGGlyphFactory.DIAGRAM_TOOLBOX),
                          new AbstractMap.SimpleEntry<>(InputData.class, DMNSVGGlyphFactory.INPUT_DATA_TOOLBOX),
                          new AbstractMap.SimpleEntry<>(KnowledgeSource.class, DMNSVGGlyphFactory.KNOWLEDGE_SOURCE_TOOLBOX),
                          new AbstractMap.SimpleEntry<>(TextAnnotation.class, DMNSVGGlyphFactory.TEXT_ANNOTATION_TOOLBOX))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static final Map<Class<? extends DMNDefinition>, Glyph> GLYPHS_PALETTE =
            Stream.of(new AbstractMap.SimpleEntry<>(BusinessKnowledgeModel.class, DMNSVGGlyphFactory.BUSINESS_KNOWLEDGE_MODEL_PALETTE),
                      new AbstractMap.SimpleEntry<>(Decision.class, DMNSVGGlyphFactory.DECISION_PALETTE),
                      new AbstractMap.SimpleEntry<>(InputData.class, DMNSVGGlyphFactory.INPUT_DATA_PALETTE),
                      new AbstractMap.SimpleEntry<>(DecisionService.class, DMNSVGGlyphFactory.DECISION_SERVICE_PALETTE),
                      new AbstractMap.SimpleEntry<>(KnowledgeSource.class, DMNSVGGlyphFactory.KNOWLEDGE_SOURCE_PALETTE),
                      new AbstractMap.SimpleEntry<>(TextAnnotation.class, DMNSVGGlyphFactory.TEXT_ANNOTATION_PALETTE))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

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
        return GLYPHS_TOOLBOX.computeIfAbsent(type, t -> ShapeGlyph.create());
    }

    @Override
    public Glyph getGlyph(final Class<? extends DMNViewDefinition> type,
                          final Class<? extends ShapeFactory.GlyphConsumer> consumer,
                          final String defId) {
        if (org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette.PaletteGlyphConsumer.class.equals(consumer)) {
            return GLYPHS_PALETTE.computeIfAbsent(type, t -> ShapeGlyph.create());
        }
        return getGlyph(type, defId);
    }
}
