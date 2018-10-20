/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ait.lienzo.client.widget.panel.scrollbars;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;

class ScrollPosition {

    private final ScrollablePanelHandler scrollHandler;

    ScrollPosition(final ScrollablePanelHandler scrollHandler) {
        this.scrollHandler = scrollHandler;
    }

    double currentRelativeX() {

        final double delta = deltaX();

        return delta == 0d ? 0d : 100 * currentX() / delta;
    }

    double currentRelativeY() {

        final double delta = deltaY();

        return delta == 0d ? 0d : 100 * currentY() / delta;
    }

    double currentPositionX(final Double level) {

        final double position = deltaX() * level / 100;

        return -(bounds().minBoundX() + position);
    }

    double currentPositionY(final Double level) {

        final double position = deltaY() * level / 100;

        return -(bounds().minBoundY() + position);
    }

    double deltaX() {
        return bounds().maxBoundX() - bounds().minBoundX() - getVisibleBounds().getWidth();
    }

    double deltaY() {
        return bounds().maxBoundY() - bounds().minBoundY() - getVisibleBounds().getHeight();
    }

    Transform getTransform() {
        if (null == getLayer()) {
            return new Transform();
        }

        final Viewport viewport = getLayer().getViewport();

        return viewport.getTransform();
    }

    Bounds getVisibleBounds() {
        return scrollHandler.getPanel().getVisibleBounds();
    }

    ScrollBounds bounds() {
        return scrollHandler.scrollBounds();
    }

    private Double currentX() {
        return -(getTransform().getTranslateX() / getTransform().getScaleX() + bounds().minBoundX());
    }

    private Double currentY() {
        return -(getTransform().getTranslateY() / getTransform().getScaleY() + bounds().minBoundY());
    }

    Layer getLayer() {
        return scrollHandler.getLayer();
    }
}
