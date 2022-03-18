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

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class BPMNDiagramShapeDef extends BaseDimensionedShapeDef
        implements BPMNSvgShapeDef<BPMNDiagramImpl> {

    @Override
    public SizeHandler<BPMNDiagramImpl, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .width(e -> e.getDimensionsSet().getWidth().getValue())
                .height(e -> e.getDimensionsSet().getHeight().getValue())
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BPMNDiagramImpl diagram) {
        return newViewInstance(Optional.ofNullable(diagram.getDimensionsSet().getWidth()),
                               Optional.ofNullable(diagram.getDimensionsSet().getHeight()),
                               factory.rectangle());
    }
}
