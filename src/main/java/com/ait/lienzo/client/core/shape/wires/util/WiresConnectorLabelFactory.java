package com.ait.lienzo.client.core.shape.wires.util;

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextBoundsAndLineBreaksWrap;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.TextAlign;
import java.util.function.BiConsumer;

public class WiresConnectorLabelFactory
{
    public static WiresConnectorLabel newLabelOnFirstSegment(final String text,
                                                             final WiresConnector connector)
    {
        return new WiresConnectorLabel(text, connector, new FirstSegmentLabelExecutor().consumer());
    }

    public static WiresConnectorLabel newLabelOnLongestSegment(final String text,
                                                               final WiresConnector connector)
    {
        return new WiresConnectorLabel(text, connector, new LongestSegmentLabelExecutor().consumer());
    }

    public static class SegmentLabelExecutor
    {
        private static final double TEXT_WRAP_MAX_WIDTH  = 200d;

        private static final double TEXT_WRAP_MAX_HEIGHT = 11d;

        private static final double OFFSET               = 10d;

        public BiConsumer<Segment, Text> consumer()
        {
            return (segment, text) -> {
                // Calculate some first segment dimensions on a cartesian axis.
                final Point2D start    = segment.start;
                final Point2D end      = segment.end;
                final double  distance = segment.length;
                final double  rotation = segment.tetha;
                final Point2D center   = Geometry.findCenter(start, end);
                final double  cos      = Math.cos(rotation);
                final double  sin      = Math.sin(rotation);

                // Wrap the text.
                final double         maxDistanceX = distance > TEXT_WRAP_MAX_WIDTH ? TEXT_WRAP_MAX_WIDTH : distance;
                final double         maxDistanceY = distance > TEXT_WRAP_MAX_HEIGHT ? TEXT_WRAP_MAX_HEIGHT : distance;
                final BoundingBox    wrap         = BoundingBox.fromDoubles(0, 0, maxDistanceX, maxDistanceY);
                text.setTextAlign(TextAlign.LEFT);
                final TextBoundsAndLineBreaksWrap textWrap     = new TextBoundsAndLineBreaksWrap(text, wrap);
                text.setWrapper(textWrap);
                text.moveToTop();

                // Set the right location.
                final BoundingBox tbb = textWrap.getTextBoundaries();
                final double  tbbw   = maxDistanceX / 2;
                final double  tox    = Math.abs(tbbw * cos);
                final Point2D offset = new Point2D(Math.abs(OFFSET * sin), Math.abs(OFFSET * cos) * -1);
                text.setLocation(center.subXY(tox, tbb.getHeight()).add(offset));
            };
        }
    }

    public static class FirstSegmentLabelExecutor
    {
        private final BiConsumer<Segment, Text> executor;

        public FirstSegmentLabelExecutor() {
            this(new SegmentLabelExecutor().consumer());
        }

        FirstSegmentLabelExecutor(BiConsumer<Segment, Text> executor) {
            this.executor = executor;
        }

        public BiConsumer<WiresConnector, Text> consumer()
        {
            return (connector, text) -> {
                final Point2DArray points = connector.getLine().getPoint2DArray();
                if (points.size() >= 2)
                {
                    executor.accept(new Segment(0, points.get(0), points.get(1)), text);
                }
            };
        }

    }

    public static class LongestSegmentLabelExecutor
    {
        private final BiConsumer<Segment, Text> executor;

        public LongestSegmentLabelExecutor() {
            this(new SegmentLabelExecutor().consumer());
        }

        LongestSegmentLabelExecutor(BiConsumer<Segment, Text> executor) {
            this.executor = executor;
        }

        public BiConsumer<WiresConnector, Text> consumer()
        {
            return (connector, text) -> {
                final Segment[] segments = parseSegments(connector.getLine().getPoint2DArray());
                final Segment   largest  = largest(segments);
                executor.accept(largest, text);
            };
        }

        private static Segment largest(Segment[] segments)
        {
            Segment result = segments[0];
            for (int i = 1; i < segments.length; i++)
            {
                final Segment segment = segments[i];
                if (segment.length > result.length)
                {
                    result = segment;
                }
            }
            return result;
        }

        private static Segment[] parseSegments(final Point2DArray linePoints)
        {
            final Segment[] segments = new Segment[linePoints.size() - 1];
            for (int i = 0; i < (linePoints.size() - 1); i++)
            {
                final Point2D start = linePoints.get(i);
                final Point2D end   = linePoints.get(i + 1);
                segments[i] = new Segment(i, start, end);
            }
            return segments;
        }
    }

    public static class Segment
    {
        private final int     index;

        private final double  length;

        private final double  tetha;

        private final Point2D start;

        private final Point2D end;

        public Segment(final int index,
                        final Point2D start,
                        final Point2D end)
        {
            this.index = index;
            this.start = start;
            this.end = end;
            this.length = Geometry.distance(start, end);
            this.tetha = Geometry.findAngle(start, end);
        }

        public int getIndex() {
            return index;
        }

        public double getLength() {
            return length;
        }

        public double getTetha() {
            return tetha;
        }

        public Point2D getStart() {
            return start;
        }

        public Point2D getEnd() {
            return end;
        }
    }
}
