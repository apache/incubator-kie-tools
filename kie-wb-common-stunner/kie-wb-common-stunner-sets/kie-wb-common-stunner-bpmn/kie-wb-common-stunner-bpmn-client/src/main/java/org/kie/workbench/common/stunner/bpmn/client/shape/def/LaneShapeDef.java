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

import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class LaneShapeDef extends BaseDimensionedShapeDef
        implements BPMNSvgShapeDef<Lane> {

    @Override
    public FontHandler<Lane, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .position(c -> HasTitle.Position.LEFT)
                .rotation(c -> 270d)
                .build();
    }

    @Override
    public SizeHandler<Lane, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .width(e -> e.getDimensionsSet().getWidth().getValue())
                .height(e -> e.getDimensionsSet().getHeight().getValue())
                .minWidth(task -> 200d)
                .minHeight(task -> 200d)
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final Lane lane) {
        return newViewInstance(Optional.ofNullable(lane.getDimensionsSet().getWidth()),
                               Optional.ofNullable(lane.getDimensionsSet().getHeight()),
                               factory.lane());
    }

    @Override
    public Glyph getGlyph(final Class<? extends Lane> type,
                          final String defId) {
        return BPMNGlyphFactory.LANE;
    }
}
