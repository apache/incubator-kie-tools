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
package org.uberfire.ext.wires.core.trees.client.layout;

import java.util.Map;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.canvas.WiresCanvas;
import org.uberfire.ext.wires.core.trees.client.layout.treelayout.Rectangle2D;

/**
 * Layout utilities
 */
public class WiresLayoutUtilities {

    public static final double PADDING = 100;

    /**
     * Get the bounds of the layout information
     * @param layout Layout information
     * @return The bounds of the layout
     */
    public static Rectangle2D getLayoutBounds( final Map<WiresBaseShape, Point2D> layout ) {
        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;
        for ( Map.Entry<WiresBaseShape, Point2D> e : layout.entrySet() ) {
            final double ax = e.getValue().getX();
            final double ay = e.getValue().getY();
            if ( ax < minX ) {
                minX = ax;
            }
            if ( ax > maxX ) {
                maxX = ax;
            }
            if ( ay < minY ) {
                minY = ay;
            }
            if ( ay > maxY ) {
                maxY = ay;
            }
        }
        final double width = maxX - minX;
        final double height = maxY - minY;
        final Rectangle2D bounds = new Rectangle2D( minX,
                                                    minY,
                                                    width,
                                                    height );
        return bounds;
    }

    /**
     * Get the bounds of the Canvas to hold the provided Layout bounds. This notionally adds padding to the Layout bounds, however
     * if the Canvas bounds are less than a minimum defined as WiresCanvas.DEFAULT_SIZE_WIDTH and WiresCanvas.DEFAULT_SIZE_HEIGHT
     * then the minimum dimensions are used.
     * @param layoutBounds The bounds of the Layout
     * @return The bounds of the Canvas including padding to contain the Layout.
     */
    public static Rectangle2D getCanvasBounds( final Rectangle2D layoutBounds ) {
        double canvasWidth = layoutBounds.getWidth() + PADDING * 2;
        double canvasHeight = layoutBounds.getHeight() + PADDING * 2;
        if ( canvasWidth < WiresCanvas.DEFAULT_SIZE_WIDTH ) {
            canvasWidth = WiresCanvas.DEFAULT_SIZE_WIDTH;
        }
        if ( canvasHeight < WiresCanvas.DEFAULT_SIZE_HEIGHT ) {
            canvasHeight = WiresCanvas.DEFAULT_SIZE_HEIGHT;
        }

        return new Rectangle2D( 0,
                                0,
                                canvasWidth,
                                canvasHeight );
    }

    /**
     * Align the Layout within a Canvas
     * @param layout Layout information
     * @return Canvas bounds
     */
    public static Rectangle2D alignLayoutInCanvas( final Map<WiresBaseShape, Point2D> layout ) {
        final Rectangle2D layoutBounds = getLayoutBounds( layout );
        final Rectangle2D canvasBounds = getCanvasBounds( layoutBounds );
        for ( Map.Entry<WiresBaseShape, Point2D> e : layout.entrySet() ) {
            double ax = e.getValue().getX();
            double ay = e.getValue().getY();

            ax = ax + ( ( canvasBounds.getWidth() - layoutBounds.getWidth() ) / 2 );
            ay = ay + PADDING;
            e.setValue( new Point2D( ax,
                                     ay ) );
        }
        return canvasBounds;
    }

    /**
     * Resize the canvas based on the layout information
     * @param canvasBounds Bounds of the Canvas
     * @param viewport Viewport to be re-sized
     */
    public static void resizeViewPort( final Rectangle2D canvasBounds,
                                       final Viewport viewport ) {
        viewport.setPixelSize( (int) canvasBounds.getWidth(),
                               (int) canvasBounds.getHeight() );
        viewport.draw();
    }

}
