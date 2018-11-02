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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.Map;
import java.util.Optional;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class SubprocessShapeDef extends BaseDimensionedShapeDef
        implements BPMNSvgShapeDef<BaseSubprocess> {

    public static final SVGShapeViewResources<BaseSubprocess, BPMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<BaseSubprocess, BPMNSVGViewFactory>()
                    .put(ReusableSubprocess.class, BPMNSVGViewFactory::reusableSubProcess)
                    .put(EmbeddedSubprocess.class, BPMNSVGViewFactory::embeddedSubProcess)
                    .put(EventSubprocess.class, BPMNSVGViewFactory::eventSubProcess)
                    .put(AdHocSubprocess.class, BPMNSVGViewFactory::adHocSubProcess)
                    .put(MultipleInstanceSubprocess.class, BPMNSVGViewFactory::multipleInstanceSubProcess);

    public static final Map<Class<? extends BaseSubprocess>, Glyph> GLYPHS =
            new Maps.Builder<Class<? extends BaseSubprocess>, Glyph>()
                    .put(ReusableSubprocess.class, BPMNGlyphFactory.SUBPROCESS_RESUABLE)
                    .put(EmbeddedSubprocess.class, BPMNGlyphFactory.SUBPROCESS_EMBEDDED)
                    .put(EventSubprocess.class, BPMNGlyphFactory.SUBPROCESS_EVENT)
                    .put(AdHocSubprocess.class, BPMNGlyphFactory.SUBPROCESS_ADHOC)
                    .put(MultipleInstanceSubprocess.class, BPMNGlyphFactory.SUBPROCESS_MULTIPLE_INSTANCE)
                    .build();

    private static HasTitle.Position getSubprocessTextPosition(final BaseSubprocess bean) {
        if ((bean instanceof EmbeddedSubprocess) || (bean instanceof MultipleInstanceSubprocess) || (bean instanceof EventSubprocess) || (bean instanceof AdHocSubprocess)) {
            return HasTitle.Position.TOP;
        } else {
            return HasTitle.Position.CENTER;
        }
    }

    @Override
    public FontHandler<BaseSubprocess, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .position(SubprocessShapeDef::getSubprocessTextPosition)
                .build();
    }

    @Override
    public SizeHandler<BaseSubprocess, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .width(task -> task.getDimensionsSet().getWidth().getValue())
                .height(task -> task.getDimensionsSet().getHeight().getValue())
                .minWidth(task -> 50d)
                .minHeight(task -> 50d)
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseSubprocess task) {
        return newViewInstance(Optional.ofNullable(task.getDimensionsSet().getWidth()),
                               Optional.ofNullable(task.getDimensionsSet().getHeight()),
                               VIEW_RESOURCES.getResource(factory,
                                                          task));
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseSubprocess> type,
                          final String defId) {
        return GLYPHS.get(type);
    }
}
