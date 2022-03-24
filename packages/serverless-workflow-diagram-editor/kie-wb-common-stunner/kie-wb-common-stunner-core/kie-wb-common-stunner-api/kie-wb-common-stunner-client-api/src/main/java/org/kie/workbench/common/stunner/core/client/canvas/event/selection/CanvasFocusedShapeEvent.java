/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.event.selection;

import java.util.Optional;
import java.util.function.Function;

import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

public class CanvasFocusedShapeEvent extends AbstractCanvasHandlerEvent<CanvasHandler> {

    private static final int FOCUS_PADDING = 100;

    private final String uuid;

    public CanvasFocusedShapeEvent(final CanvasHandler canvasHandler,
                                   final String uuid) {
        super(canvasHandler);
        this.uuid = uuid;
    }

    public int getX() {
        return getShapeX().intValue() - FOCUS_PADDING;
    }

    public int getY() {
        return getShapeY().intValue() - FOCUS_PADDING;
    }

    private Double getShapeX() {
        return getShape(shape -> shape.getShapeView().getShapeX());
    }

    private Double getShapeY() {
        return getShape(shape -> shape.getShapeView().getShapeY());
    }

    private Double getShape(final Function<Shape, Double> shapeFunction) {

        final Shape shape = getCanvas().getShape(uuid);

        return Optional
                .ofNullable(shape)
                .map(shapeFunction)
                .orElse((double) FOCUS_PADDING);
    }

    private Canvas getCanvas() {
        return getCanvasHandler().getCanvas();
    }
}
