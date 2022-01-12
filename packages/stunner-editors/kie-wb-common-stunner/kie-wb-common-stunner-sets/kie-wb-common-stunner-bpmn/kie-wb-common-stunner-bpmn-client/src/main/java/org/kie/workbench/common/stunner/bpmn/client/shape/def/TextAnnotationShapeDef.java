/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class TextAnnotationShapeDef extends BaseDimensionedShapeDef
        implements BPMNSvgShapeDef<TextAnnotation> {

    public static final SVGShapeViewResources<TextAnnotation, BPMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<TextAnnotation, BPMNSVGViewFactory>()
                    .put(TextAnnotation.class, BPMNSVGViewFactory::textAnnotation);

    @Override
    public SizeHandler<TextAnnotation, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .width(task -> task.getDimensionsSet().getWidth().getValue())
                .height(task -> task.getDimensionsSet().getHeight().getValue())
                .minWidth(task -> 50d)
                .maxWidth(task -> 400d)
                .minHeight(task -> 50d)
                .maxHeight(task -> 400d)
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final TextAnnotation task) {

        return newViewInstance(Optional.ofNullable(task.getDimensionsSet().getWidth()),
                               Optional.ofNullable(task.getDimensionsSet().getHeight()),
                               VIEW_RESOURCES.getResource(factory, task));
    }

    @Override
    public Glyph getGlyph(Class<? extends TextAnnotation> type, String defId) {
        return BPMNGlyphFactory.TEXT_ANNOTATION;
    }

    @Override
    public FontHandler<TextAnnotation, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .margin(HasTitle.HorizontalAlignment.LEFT, 10d)
                .build();
    }
}