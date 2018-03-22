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
import java.util.function.BiConsumer;

import org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.BPMNShapeViewHandlers;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.TitleHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.ViewAttributesHandler;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;

public interface BPMNShapeDef<W extends BPMNViewDefinition, V extends ShapeView>
        extends ShapeViewDef<W, V> {

    @Override
    default Optional<BiConsumer<String, V>> titleHandler() {
        return Optional.of(newTitleHandler()::handle);
    }

    @Override
    @SuppressWarnings("unchecked")
    default Optional<BiConsumer<W, V>> fontHandler() {
        return Optional.of(newFontHandler()::handle);
    }

    @Override
    default BiConsumer<W, V> viewHandler() {
        return newViewAttributesHandler()::handle;
    }

    default TitleHandler<ShapeView> newTitleHandler() {
        return BPMNShapeViewHandlers.TITLE_HANDLER;
    }

    default BPMNShapeViewHandlers.FontHandlerBuilder<W, V> newFontHandlerBuilder() {
        return new BPMNShapeViewHandlers.FontHandlerBuilder<>();
    }

    default BPMNShapeViewHandlers.ViewAttributesHandlerBuilder<W, V> newViewAttributesHandlerBuilder() {
        return new BPMNShapeViewHandlers.ViewAttributesHandlerBuilder<W, V>();
    }

    default FontHandler<W, V> newFontHandler() {
        return newFontHandlerBuilder().build();
    }

    default ViewAttributesHandler<W, V> newViewAttributesHandler() {
        return newViewAttributesHandlerBuilder().build();
    }
}
