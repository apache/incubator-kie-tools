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

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;

public interface CaseManagementActivityShapeDef<W extends BPMNViewDefinition, V extends ShapeView>
        extends RectangleShapeDef<W, V>,
                CaseManagementShapeDef<W, V> {

    double WIDTH = 136d;
    double HEIGHT = 48d;

    @Override
    default Optional<BiConsumer<View<W>, V>> sizeHandler() {
        return Optional.of(newSizeHandlerBuilder()
                                   .width(this::getWidth)
                                   .height(this::getHeight)
                                   .build()::handle);
    }

    @Override
    default Double getWidth(final W element) {
        return WIDTH;
    }

    @Override
    default Double getHeight(final W element) {
        return HEIGHT;
    }

    @Override
    default double getCornerRadius(final W element) {
        return 5;
    }

    SafeUri getIconUri(final Class<? extends W> task);
}
