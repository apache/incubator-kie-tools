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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.EventInterruptingViewHandler;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.CompositeShapeViewHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class StartEventShapeDef
        implements BPMNSvgShapeDef<BaseStartEvent> {

    public static final SVGShapeViewResources<BaseStartEvent, BPMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<BaseStartEvent, BPMNSVGViewFactory>()

                    .put(StartNoneEvent.class,
                         BPMNSVGViewFactory::startNoneEvent)
                    .put(StartTimerEvent.class,
                         BPMNSVGViewFactory::startTimerEvent)
                    .put(StartSignalEvent.class,
                         BPMNSVGViewFactory::startSignalEvent)
                    .put(StartMessageEvent.class,
                         BPMNSVGViewFactory::startMessageEvent)
                    .put(StartErrorEvent.class,
                         BPMNSVGViewFactory::startErrorEvent);

    public static final Map<Class<? extends BaseStartEvent>, SvgDataUriGlyph> GLYPHS =
            new HashMap<Class<? extends BaseStartEvent>, SvgDataUriGlyph>() {{
                put(StartNoneEvent.class,
                    BPMNSVGGlyphFactory.START_NONE_EVENT_GLYPH);
                put(StartTimerEvent.class,
                    BPMNSVGGlyphFactory.START_TIMER_EVENT_GLYPH);
                put(StartSignalEvent.class,
                    BPMNSVGGlyphFactory.START_SIGNAL_EVENT_GLYPH);
                put(StartMessageEvent.class,
                    BPMNSVGGlyphFactory.START_MESSAGE_EVENT_GLYPH);
                put(StartErrorEvent.class,
                    BPMNSVGGlyphFactory.START_ERROR_EVENT_GLYPH);
            }};

    @Override
    public FontHandler<BaseStartEvent, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .positon(event -> HasTitle.Position.BOTTOM)
                .build();
    }

    @Override
    public SizeHandler<BaseStartEvent, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .radius(task -> task.getDimensionsSet().getRadius().getValue())
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public BiConsumer<BaseStartEvent, SVGShapeView> viewHandler() {
        return new CompositeShapeViewHandler<BaseStartEvent, SVGShapeView>()
                .register(newViewAttributesHandler())
                .register(new EventInterruptingViewHandler())::handle;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseStartEvent task) {
        return VIEW_RESOURCES
                .getResource(factory,
                             task)
                .build(false);
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseStartEvent> type,
                          final String defId) {
        return GLYPHS.get(type);
    }
}
