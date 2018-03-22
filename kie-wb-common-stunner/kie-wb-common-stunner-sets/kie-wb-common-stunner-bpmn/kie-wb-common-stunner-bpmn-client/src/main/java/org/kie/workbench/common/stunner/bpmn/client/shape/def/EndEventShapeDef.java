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

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
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
                         BPMNSVGViewFactory::endErrorEvent);

    public static final Map<Class<? extends BaseEndEvent>, SvgDataUriGlyph> GLYPHS =
            new HashMap<Class<? extends BaseEndEvent>, SvgDataUriGlyph>() {{
                put(EndNoneEvent.class,
                    BPMNSVGGlyphFactory.END_NONE_EVENT_GLYPH);
                put(EndSignalEvent.class,
                    BPMNSVGGlyphFactory.END_SIGNAL_EVENT_GLYPH);
                put(EndMessageEvent.class,
                    BPMNSVGGlyphFactory.END_MESSAGE_EVENT_GLYPH);
                put(EndTerminateEvent.class,
                    BPMNSVGGlyphFactory.END_TERMINATE_EVENT_GLYPH);
                put(EndErrorEvent.class,
                    BPMNSVGGlyphFactory.END_ERROR_EVENT_GLYPH);
            }};

    @Override
    public FontHandler<BaseEndEvent, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .positon(event -> HasTitle.Position.BOTTOM)
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
