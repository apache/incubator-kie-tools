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
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.EventGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
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

public class GatewayShapeDef
        implements BPMNSvgShapeDef<BaseGateway> {

    public static final SVGShapeViewResources<BaseGateway, BPMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<BaseGateway, BPMNSVGViewFactory>()
                    .put(ParallelGateway.class,
                         BPMNSVGViewFactory::parallelMultipleGateway)
                    .put(ExclusiveGateway.class,
                         BPMNSVGViewFactory::exclusiveGateway)
                    .put(InclusiveGateway.class,
                         BPMNSVGViewFactory::inclusiveGateway)
                    .put(EventGateway.class,
                         BPMNSVGViewFactory::eventGateway);

    public static final Map<Class<? extends BaseGateway>, Glyph> GLYPHS =
            Stream.of(new AbstractMap.SimpleEntry<>(ParallelGateway.class, BPMNGlyphFactory.GATEWAY_PARALLEL_MULTIPLE),
                      new AbstractMap.SimpleEntry<>(ExclusiveGateway.class, BPMNGlyphFactory.GATEWAY_EXCLUSIVE),
                      new AbstractMap.SimpleEntry<>(InclusiveGateway.class, BPMNGlyphFactory.GATEWAY_INCLUSIVE),
                      new AbstractMap.SimpleEntry<>(EventGateway.class, BPMNGlyphFactory.GATEWAY_EVENT))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @Override
    public SizeHandler<BaseGateway, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .radius(task -> task.getDimensionsSet().getRadius().getValue())
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseGateway task) {
        return VIEW_RESOURCES
                .getResource(factory,
                             task)
                .build(false);
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseGateway> type,
                          final String defId) {
        return GLYPHS.get(type);
    }

    @Override
    public FontHandler<BaseGateway, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .verticalAlignment(bean -> VerticalAlignment.BOTTOM)
                .horizontalAlignment(bean -> HorizontalAlignment.CENTER)
                .referencePosition(bean -> ReferencePosition.OUTSIDE)
                .textSizeConstraints(bean -> new HasTitle.Size(400, 100, SizeType.PERCENTAGE))
                .margin(VerticalAlignment.BOTTOM, 5d)
                .build();
    }
}
