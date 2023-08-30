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


package org.kie.workbench.common.stunner.core.client.canvas;

public class CanvasGrid {

    public final static CanvasGrid DEFAULT_GRID = new CanvasGrid(new GridLine(100,
                                                                              0.2,
                                                                              "#0000FF"),
                                                                 new GridLine(25,
                                                                              0.2,
                                                                              "#00FF00"));

    public final static CanvasGrid DRAG_GRID = new CanvasGrid(new GridLine(50,
                                                                           0.4,
                                                                           "#bfbfbf"),
                                                              new GridLine(25,
                                                                           0.5,
                                                                           "#e6e6e6"));

    public final static CanvasGrid SMALL_POINT_GRID = new CanvasGrid(new GridLine(51,
                                                                                  0.8,
                                                                                  "#b3b3b3",
                                                                                  1.5,
                                                                                  50));

    public static class GridLine {

        private final double distance;
        private final double alpha;
        private final String color;
        private final double width;
        private final double dashArray;

        private GridLine(final double distance,
                         final double alpha,
                         final String color) {
            this.distance = distance;
            this.alpha = alpha;
            this.color = color;
            this.width = 1;
            this.dashArray = -1;
        }

        private GridLine(final double distance,
                         final double alpha,
                         final String color,
                         final double width,
                         final double dashArray) {
            this.distance = distance;
            this.alpha = alpha;
            this.color = color;
            this.width = width;
            this.dashArray = dashArray;
        }

        public double getDistance() {
            return distance;
        }

        public double getAlpha() {
            return alpha;
        }

        public String getColor() {
            return color;
        }

        public double getWidth() {
            return width;
        }

        public double getDashArray() {
            return dashArray;
        }
    }

    private final GridLine line1;
    private final GridLine line2;

    private CanvasGrid(final GridLine line1) {
        this.line1 = line1;
        this.line2 = null;
    }

    private CanvasGrid(final GridLine line1,
                       final GridLine line2) {
        this.line1 = line1;
        this.line2 = line2;
    }

    public GridLine getLine1() {
        return line1;
    }

    public GridLine getLine2() {
        return line2;
    }
}
