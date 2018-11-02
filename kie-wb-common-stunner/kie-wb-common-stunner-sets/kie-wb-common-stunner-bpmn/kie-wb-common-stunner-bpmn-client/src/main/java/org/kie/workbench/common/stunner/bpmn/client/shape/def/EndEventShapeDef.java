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
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class EndEventShapeDef
        implements BPMNSvgShapeDef<BaseEndEvent> {

    public static final SVGShapeViewResources<BaseEndEvent, BPMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<BaseEndEvent, BPMNSVGViewFactory>()
                    .put(EndNoneEvent.class,
                         BPMNSVGViewFactory::endNoneEvent)
                    .put(EndSignalEvent.class,
                         BPMNSVGViewFactory::endSignalEvent)
                    .put(EndMessageEvent.class,
                         BPMNSVGViewFactory::endMessageEvent)
                    .put(EndTerminateEvent.class,
                         BPMNSVGViewFactory::endTerminateEvent)
                    .put(EndErrorEvent.class,
                         BPMNSVGViewFactory::endErrorEvent)
                    .put(EndEscalationEvent.class,
                         BPMNSVGViewFactory::endEscalationEvent)
                    .put(EndCompensationEvent.class,
                         BPMNSVGViewFactory::endCompensationEvent);

    public static final Map<Class<? extends BaseEndEvent>, Glyph> GLYPHS =
            new Maps.Builder<Class<? extends BaseEndEvent>, Glyph>()
                    .put(EndNoneEvent.class,
                         BPMNGlyphFactory.EVENT_END_NONE)
                    .put(EndSignalEvent.class,
                         BPMNGlyphFactory.EVENT_END_SIGNAL)
                    .put(EndMessageEvent.class,
                         BPMNGlyphFactory.EVENT_END_MESSAGE)
                    .put(EndTerminateEvent.class,
                         BPMNGlyphFactory.EVENT_END_TERMINATE)
                    .put(EndErrorEvent.class,
                         BPMNGlyphFactory.EVENT_END_ERROR)
                    .put(EndEscalationEvent.class,
                         BPMNGlyphFactory.EVENT_END_ESCALATION)
                    .put(EndCompensationEvent.class, BPMNGlyphFactory.EVENT_END_COMPENSATION)
                    .build();

    @Override
    public FontHandler<BaseEndEvent, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .position(event -> HasTitle.Position.BOTTOM)
                .build();
    }

    @Override
    public SizeHandler<BaseEndEvent, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .radius(task -> task.getDimensionsSet().getRadius().getValue())
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseEndEvent task) {
        return VIEW_RESOURCES
                .getResource(factory,
                             task)
                .build(false);
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseEndEvent> type,
                          final String defId) {
        return GLYPHS.get(type);
    }
}
