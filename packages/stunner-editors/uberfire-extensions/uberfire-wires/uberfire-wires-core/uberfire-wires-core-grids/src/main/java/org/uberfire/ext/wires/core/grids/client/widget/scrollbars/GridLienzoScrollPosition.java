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

package org.uberfire.ext.wires.core.grids.client.widget.scrollbars;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

/*
 * Converts the viewport coordinates to a proportional unit, in the Grid Lienzo scrollbars context.
 */

class GridLienzoScrollPosition {

    private final GridLienzoScrollHandler gridLienzoScrollHandler;

    GridLienzoScrollPosition(final GridLienzoScrollHandler gridLienzoScrollHandler) {
        this.gridLienzoScrollHandler = gridLienzoScrollHandler;
    }

    Double currentRelativeX() {

        final Double delta = deltaX();

        return delta == 0d ? 0d : 100 * currentX() / delta;
    }

    Double currentRelativeY() {

        final Double delta = deltaY();

        return delta == 0d ? 0d : 100 * currentY() / delta;
    }

    Double currentPositionX(final Double level) {

        final Double position = deltaX() * level / 100;

        return -(bounds().minBoundX() + position);
    }

    Double currentPositionY(final Double level) {

        final Double position = deltaY() * level / 100;

        return -(bounds().minBoundY() + position);
    }

    Double deltaX() {
        return bounds().maxBoundX() - bounds().minBoundX() - getVisibleBounds().getWidth();
    }

    Double deltaY() {
        return bounds().maxBoundY() - bounds().minBoundY() - getVisibleBounds().getHeight();
    }

    private Double currentX() {
        return -(getTransform().getTranslateX() / getTransform().getScaleX() + bounds().minBoundX());
    }

    private Double currentY() {
        return -(getTransform().getTranslateY() / getTransform().getScaleY() + bounds().minBoundY());
    }

    Transform getTransform() {
        final Viewport viewport = getDefaultGridLayer().getViewport();

        return viewport.getTransform();
    }

    Bounds getVisibleBounds() {
        return getDefaultGridLayer().getVisibleBounds();
    }

    GridLienzoScrollBounds bounds() {
        return gridLienzoScrollHandler.scrollBounds();
    }

    private DefaultGridLayer getDefaultGridLayer() {
        return gridLienzoScrollHandler.getDefaultGridLayer();
    }
}
