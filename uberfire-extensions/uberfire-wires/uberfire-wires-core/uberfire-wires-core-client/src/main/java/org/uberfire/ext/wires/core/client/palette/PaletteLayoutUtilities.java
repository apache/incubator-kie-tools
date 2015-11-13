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
package org.uberfire.ext.wires.core.client.palette;

import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;

/**
 * Helper to calculate the (x, y) of a PaletteShape to be added to a Panel
 */
public class PaletteLayoutUtilities {

    public static double getX( final int shapeNumber ) {
        return calculateX( shapeNumber );
    }

    public static double getY( final int shapeNumber ) {
        return calculateY( shapeNumber );
    }

    private static int calculateX( final int shapeNumber ) {
        int x = shapeNumber > 1 ? ( getPositionInRow( shapeNumber ) - 1 ) : 0;
        return x > 0 ? ( ShapeFactoryUtil.WIDTH_BOUNDING * x ) + ShapeFactoryUtil.SPACE_BETWEEN_BOUNDING * x : ShapeFactoryUtil.WIDTH_BOUNDING * x;
    }

    private static int calculateY( final int shapeNumber ) {
        int y = shapeNumber > 1 ? getRow( shapeNumber ) : 0;
        return y > 0 ? ( y * ShapeFactoryUtil.HEIGHT_BOUNDING ) + ShapeFactoryUtil.SPACE_BETWEEN_BOUNDING * y : y * ShapeFactoryUtil.HEIGHT_BOUNDING;
    }

    private static int getRow( final int shapeNumber ) {
        return Math.round( ( shapeNumber * ShapeFactoryUtil.WIDTH_BOUNDING ) / ShapeFactoryUtil.WIDTH_STENCIL );
    }

    private static int shapesByRow() {
        return Math.round( ShapeFactoryUtil.WIDTH_STENCIL / ShapeFactoryUtil.WIDTH_BOUNDING );
    }

    private static int getPositionInRow( final int shapeNumber ) {
        return ( shapeNumber - shapesByRow() ) >= 1 ? ( shapeNumber - ( shapesByRow() * getRow( shapeNumber ) ) ) : shapeNumber;
    }

}
