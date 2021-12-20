/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.shapes.client.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ShapeViewSupportedEvents;

public class PolygonView extends AbstractHasRadiusView<PolygonView> {

    private static final int SIDES = 4;
    private static final double CORNER = 0;

    public PolygonView(final double radius) {
        super(ShapeViewSupportedEvents.DESKTOP_NO_RESIZE_EVENT_TYPES,
              create(new MultiPath(),
                     SIDES,
                     radius,
                     CORNER));
        super.setResizable(false);
    }

    @Override
    public PolygonView setRadius(final double radius) {
        create(getPath().clear(),
               SIDES,
               radius,
               CORNER);
        return this;
    }

    // TODO: If cornerRadius > 0 -> bug.
    private static MultiPath create(final MultiPath result,
                                    final int sides,
                                    final double radius,
                                    final double cornerRadius) {
        final double ix = radius;
        final double iy = radius;
        if ((sides > 2) && (radius > 0)) {
            result.M(ix,
                     iy - radius);
            if (cornerRadius <= 0) {
                for (int n = 1; n < sides; n++) {
                    final double theta = (n * 2 * Math.PI / sides);
                    result.L(ix + (radius * Math.sin(theta)),
                             iy + (-1 * radius * Math.cos(theta)));
                }
                result.Z();
            } else {
                final Point2DArray list = Point2DArray.fromArrayOfDouble(ix,
                                                                         iy - radius);
                for (int n = 1; n < sides; n++) {
                    final double theta = (n * 2 * Math.PI / sides);
                    list.pushXY(ix + (radius * Math.sin(theta)),
                                iy + (-1 * radius * Math.cos(theta)));
                }
                Geometry.drawArcJoinedLines(result.getPathPartList(),
                                            list.pushXY(ix,
                                                        iy - radius),
                                            cornerRadius);
            }
        }
        return result;
    }
}
