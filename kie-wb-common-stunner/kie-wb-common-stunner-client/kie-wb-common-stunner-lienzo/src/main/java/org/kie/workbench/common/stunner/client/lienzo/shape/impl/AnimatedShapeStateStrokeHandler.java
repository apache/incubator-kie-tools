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

package org.kie.workbench.common.stunner.client.lienzo.shape.impl;

import java.util.function.Function;

import org.kie.workbench.common.stunner.client.lienzo.shape.animation.ShapeDecoratorAnimation;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateHandler;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateStrokeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class AnimatedShapeStateStrokeHandler<V extends ShapeView<?>, S extends Shape<V>>
        implements ShapeStateHandler<V, S> {

    private final ShapeStateStrokeHandler<V, S> handler;

    public AnimatedShapeStateStrokeHandler() {
        this.handler = new ShapeStateStrokeHandler<V, S>(AnimatedShapeStateStrokeHandler::applyAnimatedStrokeState);
    }

    public AnimatedShapeStateStrokeHandler(final Function<ShapeState, String> stateColorProvider) {
        this.handler = new ShapeStateStrokeHandler<V, S>(AnimatedShapeStateStrokeHandler::applyAnimatedStrokeState,
                                                         stateColorProvider);
    }

    AnimatedShapeStateStrokeHandler(final ShapeStateStrokeHandler<V, S> handler) {
        this.handler = handler;
    }

    @Override
    public ShapeStateHandler<V, S> forShape(final S shape) {
        handler.forShape(shape);
        return this;
    }

    @Override
    public ShapeStateHandler<V, S> shapeUpdated() {
        handler.shapeUpdated();
        return this;
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        handler.applyState(shapeState);
    }

    @Override
    public ShapeState reset() {
        return handler.reset();
    }

    @Override
    public ShapeState getShapeState() {
        return handler.getShapeState();
    }

    public ShapeStateStrokeHandler<V, S> getWrapped() {
        return handler;
    }

    private static void applyAnimatedStrokeState(final Shape<? extends ShapeView<?>> shape,
                                                 final ShapeStateStrokeHandler.ShapeStrokeState state) {
        new ShapeDecoratorAnimation(state.getStrokeColor(),
                                    state.getStrokeWidth(),
                                    state.getStrokeAlpha())
                .forShape(shape)
                .run();
    }
}
