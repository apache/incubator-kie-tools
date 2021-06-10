package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;

public interface IControlPointsAcceptor
{
    IControlPointsAcceptor ALL  = new DefaultControlPointsAcceptor(true);

    IControlPointsAcceptor NONE = new DefaultControlPointsAcceptor(false);

    boolean add(WiresConnector connector,
                       int index,
                       Point2D location);

    boolean move(WiresConnector connector,
                        Point2DArray pointsLocation);

    boolean delete(WiresConnector connector,
                          int index);

    class DefaultControlPointsAcceptor implements IControlPointsAcceptor {
        private final boolean accept;

        public DefaultControlPointsAcceptor(final boolean accept)
        {
            this.accept = accept;
        }

        @Override
        public boolean add(final WiresConnector connector,
                           final int index,
                           final Point2D location)
        {
            if (accept)
            {
                connector.addControlPoint(location.getX(), location.getY(), index);
            }
            return accept;
        }

        @Override
        public boolean move(final WiresConnector connector,
                            final Point2DArray pointsLocation)
        {
            if (accept)
            {
                for (int i = 0; i < pointsLocation.size(); i++)
                {
                    Point2D point = pointsLocation.get(i);
                    connector.moveControlPoint(i, new Point2D(point.getX(), point.getY()));
                }
            }
            return accept;
        }

        @Override
        public boolean delete(final WiresConnector connector,
                              final int index)
        {
            if (accept)
            {
                connector.destroyControlPoints(new int[] { index });
            }
            return accept;
        }

    }
}
