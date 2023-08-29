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


package org.kie.workbench.common.stunner.shapes.client.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ShapeViewSupportedEvents;
import org.kie.workbench.common.stunner.lienzo.util.LienzoPaths;

/**
 * The lienzo view implementation for the Rectangle shape.
 * <p>
 * TODO: Disabling for now the resize for rectangles when they're using a corner radius value different
 * from zero - ARC resize is not implemented yet on lienzo side, and the corners are built using ARCs.
 * See <a>org.kie.workbench.common.stunner.lienzo.util.LienzoPaths#rectangle</a>.
 */
public class RectangleView extends AbstractHasSizeView<RectangleView> {

    private final double corner_radius;

    public RectangleView(final double width,
                         final double height,
                         final double corner) {
        super(corner == 0 ? ShapeViewSupportedEvents.ALL_DESKTOP_EVENT_TYPES :
                      ShapeViewSupportedEvents.DESKTOP_NO_RESIZE_EVENT_TYPES,
              create(new MultiPath(),
                     width,
                     height,
                     corner));
        super.setResizable(corner == 0);
        this.corner_radius = corner;
    }

    @Override
    public RectangleView setSize(final double width,
                                 final double height) {
        create(getPath().clear(),
               width,
               height,
               corner_radius);
        updateFillGradient(width,
                           height);
        refresh();
        return this;
    }

    /**
     * Append the path parts for a rectangle.
     * @param path The source multipath
     * @param w The rectangle width
     * @param h The rectangle height
     * @param r The rectangle corner radius
     */
    private static MultiPath create(final MultiPath path,
                                    final double w,
                                    final double h,
                                    final double r) {
        return LienzoPaths.rectangle(path,
                                     w,
                                     h,
                                     r);
    }
}
