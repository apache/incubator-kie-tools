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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Viewport;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

/*
 * Represents the Grid Lienzo bounds in the scrollbars context.
 */

class GridLienzoScrollBounds {

    private static final Double DEFAULT_VALUE = 0D;

    private final GridLienzoScrollHandler gridLienzoScrollHandler;

    private Bounds defaultBounds;

    GridLienzoScrollBounds(final GridLienzoScrollHandler GridLienzoScrollHandler) {
        this.gridLienzoScrollHandler = GridLienzoScrollHandler;
    }

    Double maxBoundX() {

        final List<Double> boundsValues =
                getVisibleGridWidgets()
                        .map(gridWidget -> gridWidget.getX() + gridWidget.getWidth())
                        .collect(Collectors.toList());

        addExtraBounds(boundsValues,
                       bounds -> bounds.getX() + bounds.getWidth());

        return maxValue(boundsValues);
    }

    Double maxBoundY() {

        final List<Double> boundsValues =
                getVisibleGridWidgets()
                        .map(gridWidget -> gridWidget.getY() + gridWidget.getHeight())
                        .collect(Collectors.toList());

        addExtraBounds(boundsValues,
                       bounds -> bounds.getY() + bounds.getHeight());

        return maxValue(boundsValues);
    }

    Double minBoundX() {

        final List<Double> boundsValues =
                getVisibleGridWidgets()
                        .map(IPrimitive::getX)
                        .collect(Collectors.toList());

        addExtraBounds(boundsValues,
                       Bounds::getX);

        return minValue(boundsValues);
    }

    Double minBoundY() {

        final List<Double> boundsValues =
                getVisibleGridWidgets()
                        .map(IPrimitive::getY)
                        .collect(Collectors.toList());

        addExtraBounds(boundsValues,
                       Bounds::getY);

        return minValue(boundsValues);
    }

    Stream<GridWidget> getVisibleGridWidgets() {
        return getGridWidgets()
                .stream()
                .filter(IDrawable::isVisible);
    }

    private double maxValue(final List<Double> boundsValues) {
        return boundsValues.stream().reduce(Double::max).orElse(DEFAULT_VALUE);
    }

    private double minValue(final List<Double> boundsValues) {
        return boundsValues.stream().reduce(Double::min).orElse(DEFAULT_VALUE);
    }

    private void addExtraBounds(final List<Double> bounds,
                                final Function<Bounds, Double> function) {
        if (hasVisibleBounds()) {
            bounds.add(function.apply(getVisibleBounds()));
        }

        if (hasDefaultBounds() && !isGridPinned()) {
            bounds.add(function.apply(getDefaultBounds()));
        }
    }

    Bounds getVisibleBounds() {
        return getDefaultGridLayer().getVisibleBounds();
    }

    Boolean hasDefaultBounds() {
        return Optional.ofNullable(getDefaultBounds()).isPresent();
    }

    Boolean hasVisibleBounds() {
        final Viewport viewport = getDefaultGridLayer().getViewport();

        return Optional.ofNullable(viewport).isPresent();
    }

    Bounds getDefaultBounds() {
        return defaultBounds;
    }

    void setDefaultBounds(final Bounds defaultBounds) {
        this.defaultBounds = defaultBounds;
    }

    DefaultGridLayer getDefaultGridLayer() {
        return gridLienzoScrollHandler.getDefaultGridLayer();
    }

    Set<GridWidget> getGridWidgets() {
        return getDefaultGridLayer().getGridWidgets();
    }

    private boolean isGridPinned() {
        return getDefaultGridLayer().isGridPinned();
    }
}
