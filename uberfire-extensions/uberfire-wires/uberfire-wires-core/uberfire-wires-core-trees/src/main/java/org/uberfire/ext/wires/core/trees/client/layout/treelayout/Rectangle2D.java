/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.core.trees.client.layout.treelayout;

/**
 * The original implementation used java.awt.geom.Rectangle2D that is not available for use in GWT.
 */
public class Rectangle2D {

    private double x;
    private double y;
    private double width;
    private double height;

    public Rectangle2D( final double x,
                        final double y,
                        final double w,
                        final double h ) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

}
