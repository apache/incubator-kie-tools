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
package org.uberfire.ext.wires.core.grids.client.widget.context;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

/**
 * The context of a Grid's cell being edited.
 */
public class GridBodyCellEditContext extends GridBodyCellRenderContext {

    private final Optional<Point2D> relativeLocation;

    public GridBodyCellEditContext(final double absoluteCellX,
                                   final double absoluteCellY,
                                   final double cellWidth,
                                   final double cellHeight,
                                   final double clipMinY,
                                   final double clipMinX,
                                   final int rowIndex,
                                   final int columnIndex,
                                   final boolean isFloating,
                                   final Transform transform,
                                   final GridRenderer renderer,
                                   final Optional<Point2D> relativeLocation) {
        super(absoluteCellX,
              absoluteCellY,
              cellWidth,
              cellHeight,
              clipMinY,
              clipMinX,
              rowIndex,
              columnIndex,
              isFloating,
              transform,
              renderer);
        this.relativeLocation = relativeLocation;
    }

    /**
     * Returns the Canvas coordinate relative to the GridWidget containing the cell being edited in response to a {@link MouseEvent}.
     * @return {@link Optional#empty()} if the edit operation did not result from a {@link MouseEvent}
     */
    public Optional<Point2D> getRelativeLocation() {
        return relativeLocation;
    }
}
