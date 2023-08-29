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
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventThrowing;
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
                    .put(IntermediateLinkEventThrowing.class, BPMNSVGViewFactory::intermediateLinkThrowingEvent)
                    .put(IntermediateMessageEventThrowing.class, BPMNSVGViewFactory::intermediateMessageThrowingEvent)
                    .put(IntermediateEscalationEventThrowing.class, BPMNSVGViewFactory::intermediateEscalationThrowingEvent)
                    .put(IntermediateCompensationEventThrowing.class, BPMNSVGViewFactory::intermediateCompensationThrowingEvent);

    public static final Map<Class<? extends BaseThrowingIntermediateEvent>, Glyph> GLYPHS =
            Stream.of(new AbstractMap.SimpleEntry<>(IntermediateSignalEventThrowing.class, BPMNGlyphFactory.EVENT_INTERMEDIATE_THROWING_SIGNAL),
                      new AbstractMap.SimpleEntry<>(IntermediateLinkEventThrowing.class, BPMNGlyphFactory.EVENT_INTERMEDIATE_THROWING_LINK),
                      new AbstractMap.SimpleEntry<>(IntermediateMessageEventThrowing.class, BPMNGlyphFactory.EVENT_INTERMEDIATE_THROWING_MESSAGE),
                      new AbstractMap.SimpleEntry<>(IntermediateEscalationEventThrowing.class, BPMNGlyphFactory.EVENT_INTERMEDIATE_THROWING_ESCALATION),
                      new AbstractMap.SimpleEntry<>(IntermediateCompensationEventThrowing.class, BPMNGlyphFactory.EVENT_INTERMEDIATE_THROWING_COMPENSATION))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @Override
    public FontHandler<BaseThrowingIntermediateEvent, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .verticalAlignment(bean -> HasTitle.VerticalAlignment.BOTTOM)
                .horizontalAlignment(bean -> HasTitle.HorizontalAlignment.CENTER)
                .referencePosition(bean -> HasTitle.ReferencePosition.OUTSIDE)
                .textSizeConstraints(bean -> new HasTitle.Size(400, 100, HasTitle.Size.SizeType.PERCENTAGE))
                .margin(HasTitle.VerticalAlignment.BOTTOM, 5d)
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
