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
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
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
                         BPMNSVGViewFactory::inclusiveGateway);

    public static final Map<Class<? extends BaseGateway>, SvgDataUriGlyph> GLYPHS =
            new HashMap<Class<? extends BaseGateway>, SvgDataUriGlyph>() {{
                put(ParallelGateway.class,
                    BPMNSVGGlyphFactory.PARALLEL_MULTIPLE_GATEWAY_GLYPH);
                put(ExclusiveGateway.class,
                    BPMNSVGGlyphFactory.EXCLUSIVE_GATEWAY_GLYPH);
                put(InclusiveGateway.class,
                    BPMNSVGGlyphFactory.INCLUSIVE_GATEWAY_GLYPH);
            }};

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
}
