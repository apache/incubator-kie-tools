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

package org.kie.workbench.common.stunner.cm.client.shape.def;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.kie.workbench.common.stunner.bpmn.client.shape.def.BPMNShapeDef;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGViewFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public interface CaseManagementSvgShapeDef<W extends BPMNViewDefinition>
        extends BPMNShapeDef<W, SVGShapeView>,
                SVGShapeViewDef<W, CaseManagementSVGViewFactory> {

    SizeHandler<W, SVGShapeView> newSizeHandler();

    default SizeHandler.Builder<W, SVGShapeView> newSizeHandlerBuilder() {
        return new SizeHandler.Builder<>();
    }

    @Override
    default Optional<BiConsumer<View<W>, SVGShapeView>> sizeHandler() {
        return Optional.of(newSizeHandler()::handle);
    }

    default Class<CaseManagementSVGViewFactory> getViewFactoryType() {
        return CaseManagementSVGViewFactory.class;
    }
}
