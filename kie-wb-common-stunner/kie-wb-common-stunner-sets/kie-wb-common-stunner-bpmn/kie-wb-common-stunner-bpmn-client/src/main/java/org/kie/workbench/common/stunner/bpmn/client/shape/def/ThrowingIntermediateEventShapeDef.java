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

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class ThrowingIntermediateEventShapeDef
        implements BPMNSvgShapeDef<BaseThrowingIntermediateEvent> {

    public static final SVGShapeViewResources<BaseThrowingIntermediateEvent, BPMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<BaseThrowingIntermediateEvent, BPMNSVGViewFactory>()
                    .put(IntermediateSignalEventThrowing.class, BPMNSVGViewFactory::intermediateSignalThrowingEvent)
                    .put(IntermediateMessageEventThrowing.class, BPMNSVGViewFactory::intermediateMessageThrowingEvent)
                    .put(IntermediateEscalationEventThrowing.class, BPMNSVGViewFactory::intermediateEscalationThrowingEvent)
                    .put(IntermediateCompensationEventThrowing.class, BPMNSVGViewFactory::intermediateCompensationThrowingEvent);

    public static final Map<Class<? extends BaseThrowingIntermediateEvent>, Glyph> GLYPHS =
            new Maps.Builder<Class<? extends BaseThrowingIntermediateEvent>, Glyph>()
                    .put(IntermediateSignalEventThrowing.class, BPMNGlyphFactory.EVENT_INTERMEDIATE_THROWING_SIGNAL)
                    .put(IntermediateMessageEventThrowing.class, BPMNGlyphFactory.EVENT_INTERMEDIATE_THROWING_MESSAGE)
                    .put(IntermediateEscalationEventThrowing.class, BPMNGlyphFactory.EVENT_INTERMEDIATE_THROWING_ESCALATION)
                    .put(IntermediateCompensationEventThrowing.class, BPMNGlyphFactory.EVENT_INTERMEDIATE_THROWING_COMPENSATION)
                    .build();

    @Override
    public FontHandler<BaseThrowingIntermediateEvent, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .position(event -> HasTitle.Position.BOTTOM)
                .build();
    }

    @Override
    public SizeHandler<BaseThrowingIntermediateEvent, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .radius(task -> task.getDimensionsSet().getRadius().getValue())
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseThrowingIntermediateEvent intermediateTimerEvent) {
        return VIEW_RESOURCES
                .getResource(factory, intermediateTimerEvent)
                .build(false);
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseThrowingIntermediateEvent> type,
                          final String defId) {
        return GLYPHS.get(type);
    }
}
