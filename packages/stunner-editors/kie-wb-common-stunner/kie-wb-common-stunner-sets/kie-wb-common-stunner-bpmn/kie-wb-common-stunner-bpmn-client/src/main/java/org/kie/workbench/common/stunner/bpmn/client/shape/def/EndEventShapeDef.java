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


package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.HorizontalAlignment;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.ReferencePosition;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.Size.SizeType;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.VerticalAlignment;
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
            Stream.of(new AbstractMap.SimpleEntry<>(EndNoneEvent.class, BPMNGlyphFactory.EVENT_END_NONE),
                      new AbstractMap.SimpleEntry<>(EndSignalEvent.class, BPMNGlyphFactory.EVENT_END_SIGNAL),
                      new AbstractMap.SimpleEntry<>(EndMessageEvent.class, BPMNGlyphFactory.EVENT_END_MESSAGE),
                      new AbstractMap.SimpleEntry<>(EndTerminateEvent.class, BPMNGlyphFactory.EVENT_END_TERMINATE),
                      new AbstractMap.SimpleEntry<>(EndErrorEvent.class, BPMNGlyphFactory.EVENT_END_ERROR),
                      new AbstractMap.SimpleEntry<>(EndEscalationEvent.class, BPMNGlyphFactory.EVENT_END_ESCALATION),
                      new AbstractMap.SimpleEntry<>(EndCompensationEvent.class, BPMNGlyphFactory.EVENT_END_COMPENSATION))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @Override
    public FontHandler<BaseEndEvent, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .verticalAlignment(bean -> VerticalAlignment.BOTTOM)
                .horizontalAlignment(bean -> HorizontalAlignment.CENTER)
                .referencePosition(bean -> ReferencePosition.OUTSIDE)
                .textSizeConstraints(bean -> new HasTitle.Size(400, 100, SizeType.PERCENTAGE))
                .margin(VerticalAlignment.BOTTOM, 5d)
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
