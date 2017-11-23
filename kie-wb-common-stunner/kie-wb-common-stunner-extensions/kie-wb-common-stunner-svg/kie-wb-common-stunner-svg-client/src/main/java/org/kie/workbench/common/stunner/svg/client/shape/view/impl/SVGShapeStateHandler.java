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

package org.kie.workbench.common.stunner.svg.client.shape.view.impl;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.AnimatedShapeStateStrokeHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class SVGShapeStateHandler<V extends SVGShapeView<?>, S extends Shape<V>>
        implements ShapeStateHandler<V, S> {

    private final AnimatedShapeStateStrokeHandler<V, S> handler;
    private Map<ShapeState, SVGShapeStateHolder> stateHolderMap = new HashMap<>(4);

    public SVGShapeStateHandler() {
        this.handler = new AnimatedShapeStateStrokeHandler<V, S>(this::getStateStrokeColor);
    }

    SVGShapeStateHandler(final AnimatedShapeStateStrokeHandler<V, S> handler) {
        this.handler = handler;
    }

    private String getStateStrokeColor(final ShapeState state) {
        final SVGShapeStateHolder stateHolder = stateHolderMap.get(state);
        if (null != stateHolder) {
            return stateHolder.getStrokeColor();
        }
        return state.getColor();
    }

    public SVGShapeStateHandler registerStateHolder(final SVGShapeStateHolder holder) {
        stateHolderMap.put(holder.getState(),
                           holder);
        return this;
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

    public AnimatedShapeStateStrokeHandler<V, S> getWrapped() {
        return handler;
    }
}
