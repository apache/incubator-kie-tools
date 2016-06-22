/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Set;

import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;

/**
 * Helper class to calculate the maximum bounds enclosed by Decision Tables.
 */
public class GuidedDecisionTableModellerBoundsHelper {

    public static final double BOUNDS_MIN_X = -2000;
    public static final double BOUNDS_MAX_X = 2000;
    public static final double BOUNDS_MIN_Y = -2000;
    public static final double BOUNDS_MAX_Y = 2000;

    public static final double BOUNDS_PADDING = 20;

    private final Bounds bounds = new BaseBounds( BOUNDS_MIN_X,
                                                  BOUNDS_MIN_Y,
                                                  BOUNDS_MAX_X - BOUNDS_MIN_X,
                                                  BOUNDS_MAX_Y - BOUNDS_MIN_Y );

    /**
     * Returns the maximum bounds (subject to a minimum) enclosed by the provided collection of Decision Tables.
     * @param dtPresenters The collection of Decision Tables to check. Cannot be null. Can be empty, in which case the minimum bounds is returned.
     * @return The maximum bounds enclosed by the Decision Tables.
     */
    public Bounds getBounds( final Set<GuidedDecisionTableView.Presenter> dtPresenters ) {
        double minX = BOUNDS_MIN_X;
        double minY = BOUNDS_MIN_Y;
        double maxX = BOUNDS_MAX_X;
        double maxY = BOUNDS_MAX_Y;

        for ( GuidedDecisionTableView.Presenter dtPresenter : dtPresenters ) {
            final GuidedDecisionTableView dtView = dtPresenter.getView();
            minX = Math.min( dtView.getX() - BOUNDS_PADDING,
                             minX );
            minY = Math.min( dtView.getY() - BOUNDS_PADDING,
                             minY );
            maxX = Math.max( dtView.getX() + dtView.getWidth() + BOUNDS_PADDING,
                             maxX );
            maxY = Math.max( dtView.getY() + dtView.getHeight() + BOUNDS_PADDING,
                             maxY );
        }

        bounds.setX( minX );
        bounds.setY( minY );
        bounds.setWidth( maxX - minX );
        bounds.setHeight( maxY - minY );

        return bounds;
    }

}
