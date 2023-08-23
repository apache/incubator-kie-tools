/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.layer;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

public class InverseGridWidgetClipper implements IPathClipper {

    private final BoundingBox outerBoundingBox;
    private final BoundingBox innerBoundingBox;
    private boolean isActive = false;

    public InverseGridWidgetClipper(final GridWidget outer,
                                    final GridWidget inner) {
        this.outerBoundingBox = BoundingBox.fromDoubles(outer.getComputedLocation().getX(),
                                                        outer.getComputedLocation().getY(),
                                                        outer.getWidth() + BaseExpressionGridTheme.STROKE_WIDTH,
                                                        outer.getHeight() + BaseExpressionGridTheme.STROKE_WIDTH);
        this.innerBoundingBox = BoundingBox.fromDoubles(inner.getComputedLocation().getX(),
                                                        inner.getComputedLocation().getY(),
                                                        inner.getComputedLocation().getX() + inner.getWidth() + BaseExpressionGridTheme.STROKE_WIDTH,
                                                        inner.getComputedLocation().getY() + inner.getHeight() + BaseExpressionGridTheme.STROKE_WIDTH);
    }

    @Override
    public boolean clip(final Context2D context) {
        context.beginPath();

        //Left edge
        context.rect(0,
                     0,
                     innerBoundingBox.getMinX(),
                     outerBoundingBox.getHeight());
        //Top edge
        context.rect(innerBoundingBox.getMinX(),
                     0,
                     innerBoundingBox.getWidth(),
                     innerBoundingBox.getMinY());
        //Bottom edge
        context.rect(innerBoundingBox.getMinX(),
                     innerBoundingBox.getMaxY(),
                     innerBoundingBox.getWidth(),
                     outerBoundingBox.getMaxY() - innerBoundingBox.getMaxY());
        //Right edge
        context.rect(innerBoundingBox.getMaxX(),
                     0,
                     outerBoundingBox.getMaxX() - innerBoundingBox.getMaxX(),
                     outerBoundingBox.getHeight());

        context.clip();

        return true;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean setActive(final boolean isActive) {
        this.isActive = isActive;
        return isActive;
    }
}
