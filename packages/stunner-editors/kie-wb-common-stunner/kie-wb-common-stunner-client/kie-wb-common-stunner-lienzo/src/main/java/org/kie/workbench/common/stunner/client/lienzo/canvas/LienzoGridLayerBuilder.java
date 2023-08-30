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


package org.kie.workbench.common.stunner.client.lienzo.canvas;

import com.ait.lienzo.client.core.shape.GridLayer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.shared.core.types.LineCap;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasGrid;

public class LienzoGridLayerBuilder {

    public static GridLayer getLienzoGridFor(final CanvasGrid canvasGrid) {
        final CanvasGrid.GridLine line1 = canvasGrid.getLine1();
        final CanvasGrid.GridLine line2 = canvasGrid.getLine2();
        final Line gridLine1 = createLine(line1);
        final Line gridLine2 = null != line2 ? createLine(line2) : null;
        if (null != gridLine2) {
            return new GridLayer(line1.getDistance(),
                                 gridLine1,
                                 line2.getDistance(),
                                 gridLine2);
        } else {
            return new GridLayer(line1.getDistance(),
                                 gridLine1);
        }
    }

    private static Line createLine(CanvasGrid.GridLine line1) {
        final Line line = new Line(0,
                                   0,
                                   0,
                                   0)
                .setStrokeColor(line1.getColor())
                .setAlpha(line1.getAlpha())
                .setStrokeWidth(line1.getWidth())
                .setLineCap(LineCap.ROUND);
        if (line1.getDashArray() > -1) {
            line.setDashArray(1,
                              line1.getDashArray());
        }
        return line;
    }
}
