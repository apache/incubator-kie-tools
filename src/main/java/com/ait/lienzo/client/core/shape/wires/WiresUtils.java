package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.types.Point2D;

public class WiresUtils {

    /**
     * This method obtains the location for a given node relative to the Layer,
     * but as a difference from the use of getAbsoluteLocation(), this method
     * does not works with absolute coordinates so is agnostic to the given transforms
     * applied on the layer's viewport.
     */
    public static Point2D getLocation( final IDrawable<?> shape ) {

        final double[] ploc = _getLocation( shape );

        return new Point2D( ploc[0], ploc[1] );

    }

    private static double[] _getLocation( final IDrawable<?> shape ) {

        double rx = getNodeX( shape );
        double ry = getNodeY( shape );

        final Node<?> parent = shape.getParent();

        if ( null != parent ) {

            final double[] ploc = _getLocation( parent );
            rx += ploc[0];
            ry += ploc[1];

        }

        return new double[] { rx, ry };
    }

    private static double getNodeX( final IDrawable<?> node ) {
        return node.getAttributes().getX();
    }

    private static double getNodeY( final IDrawable<?> node ) {
        return node.getAttributes().getY();
    }

}
