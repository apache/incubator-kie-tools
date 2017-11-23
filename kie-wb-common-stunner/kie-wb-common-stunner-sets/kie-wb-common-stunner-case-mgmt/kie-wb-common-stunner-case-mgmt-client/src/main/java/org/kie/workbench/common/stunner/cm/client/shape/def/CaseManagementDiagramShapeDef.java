/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.stunner.cm.client.shape.view.DiagramView;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;

public final class CaseManagementDiagramShapeDef
        implements RectangleShapeDef<CaseManagementDiagram, DiagramView>,
                   CaseManagementShapeDef<CaseManagementDiagram, DiagramView> {

    public static final double WIDTH = 950;
    public static final double HEIGHT = 950;

    @Override
    public Optional<BiConsumer<View<CaseManagementDiagram>, DiagramView>> sizeHandler() {
        return Optional.of(newSizeHandlerBuilder()
                                   .width(this::getWidth)
                                   .height(this::getHeight)
                                   .build()::handle);
    }

    @Override
    public Double getWidth(final CaseManagementDiagram element) {
        return WIDTH;
    }

    @Override
    public Double getHeight(final CaseManagementDiagram element) {
        return HEIGHT;
    }

    @Override
    public double getCornerRadius(final CaseManagementDiagram element) {
        return 0;
    }

    @Override
    public Class<? extends ShapeDef> getType() {
        return CaseManagementDiagramShapeDef.class;
    }
}
