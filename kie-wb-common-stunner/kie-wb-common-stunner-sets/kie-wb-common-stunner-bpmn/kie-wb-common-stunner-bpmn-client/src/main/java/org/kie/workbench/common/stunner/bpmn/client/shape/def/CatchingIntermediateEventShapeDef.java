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
import java.util.function.BiConsumer;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.EventCancelActivityViewHandler;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.CompositeShapeViewHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class CatchingIntermediateEventShapeDef
        implements BPMNSvgShapeDef<BaseCatchingIntermediateEvent> {

    public static final SVGShapeViewResources<BaseCatchingIntermediateEvent, BPMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<BaseCatchingIntermediateEvent, BPMNSVGViewFactory>()
                    .put(IntermediateTimerEvent.class,
                         BPMNSVGViewFactory::intermediateTimerEvent)
                    .put(IntermediateSignalEventCatching.class,
                         BPMNSVGViewFactory::intermediateSignalCatchingEvent)
                    .put(IntermediateErrorEventCatching.class,
                         BPMNSVGViewFactory::intermediateErrorCatchingEvent)
                    .put(IntermediateMessageEventCatching.class,
                         BPMNSVGViewFactory::intermediateMessageCatchingEvent)
                    .put(IntermediateConditionalEvent.class,
                         BPMNSVGViewFactory::intermediateConditionalEvent)
                    .put(IntermediateEscalationEvent.class,
                         BPMNSVGViewFactory::intermediateEscalationCatchingEvent)
                    .put(IntermediateCompensationEvent.class,
                         BPMNSVGViewFactory::intermediateCompensationCatchingEvent);

    public static final Map<Class<? extends BaseCatchingIntermediateEvent>, Glyph> GLYPHS =
            new Maps.Builder<Class<? extends BaseCatchingIntermediateEvent>, Glyph>()
                    .put(IntermediateTimerEvent.class,
                         BPMNGlyphFactory.EVENT_INTERMEDIATE_TIMER)
                    .put(IntermediateSignalEventCatching.class,
                         BPMNGlyphFactory.EVENT_INTERMEDIATE_SIGNAL)
                    .put(IntermediateErrorEventCatching.class,
                         BPMNGlyphFactory.EVENT_INTERMEDIATE_ERROR)
                    .put(IntermediateMessageEventCatching.class,
                         BPMNGlyphFactory.EVENT_INTERMEDIATE_MESSAGE)
                    .put(IntermediateConditionalEvent.class,
                         BPMNGlyphFactory.EVENT_INTERMEDIATE_CONDITIONAL)
                    .put(IntermediateEscalationEvent.class,
                         BPMNGlyphFactory.EVENT_INTERMEDIATE_ESCALATION)
                    .put(IntermediateCompensationEvent.class,
                         BPMNGlyphFactory.EVENT_INTERMEDIATE_COMPENSATION)
                    .build();

    @Override
    public FontHandler<BaseCatchingIntermediateEvent, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .position(event -> HasTitle.Position.BOTTOM)
                .build();
    }

    @Override
    public SizeHandler<BaseCatchingIntermediateEvent, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .radius(task -> task.getDimensionsSet().getRadius().getValue())
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public BiConsumer<BaseCatchingIntermediateEvent, SVGShapeView> viewHandler() {
        return new CompositeShapeViewHandler<BaseCatchingIntermediateEvent, SVGShapeView>()
                .register(newViewAttributesHandler())
                .register(new EventCancelActivityViewHandler())::handle;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseCatchingIntermediateEvent task) {
        return VIEW_RESOURCES
                .getResource(factory,
                             task)
                .build(false);
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseCatchingIntermediateEvent> type,
                          final String defId) {
        return GLYPHS.get(type);
    }
}
