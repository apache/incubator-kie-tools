package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.ait.lienzo.client.core.shape.OrthogonalPolyLine.correctEndWithOffset;
import static com.ait.lienzo.shared.core.types.Direction.EAST;
import static com.ait.lienzo.shared.core.types.Direction.NONE;
import static com.ait.lienzo.shared.core.types.Direction.NORTH;
import static com.ait.lienzo.shared.core.types.Direction.NORTH_EAST;
import static com.ait.lienzo.shared.core.types.Direction.SOUTH;
import static com.ait.lienzo.shared.core.types.Direction.WEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class OrthogonalPolyLineTest {

    @Test
    public void testGetBoundingBoxForEmptyPath() {
        OrthogonalPolyLine polyLine = new OrthogonalPolyLine(new Point2DArray());
        BoundingBox box = polyLine.getBoundingBox();

        assertEquals(0, box.getMinX(), 0.000001);
        assertEquals(0, box.getMinY(), 0.000001);
        assertEquals(0, box.getMaxX(), 0.000001);
        assertEquals(0, box.getMaxY(), 0.000001);
    }

    @Test
    public void testParse() {
        Point2DArray points = new Point2DArray();
        OrthogonalPolyLine polyLine = spy(new OrthogonalPolyLine(points));

        when(polyLine.getControlPoints()).thenReturn(points);
        when(polyLine.getHeadDirection()).thenReturn(NONE);
        when(polyLine.getTailDirection()).thenReturn(NONE);

        points.push(new Point2D(0, 0));
        points.push(new Point2D(5, 5));
        assertTrue(polyLine.parse());
    }

    @Test
    public void testCorrectEndWithNorthOffset() {
        testCorrectEndWithOffset(NORTH, 3, 0);
    }

    @Test
    public void testCorrectEndWithEastOffset() {
        testCorrectEndWithOffset(EAST, 6, 3);
    }

    @Test
    public void testCorrectEndWithSouthOffset() {
        testCorrectEndWithOffset(SOUTH, 3, 6);
    }

    @Test
    public void testCorrectEndWithWestOffset() {
        testCorrectEndWithOffset(WEST, 0, 3);
    }

    @Test
    public void testCorrectEndWithNONEOffset() {
        testCorrectEndWithOffset(NONE, 3, 3);
    }

    @Test(expected = IllegalStateException.class)
    public void testCorrectEndWithOffsetWithWrongDirection() {
        testCorrectEndWithOffset(NORTH_EAST, 1000, 1000);
    }

    private void testCorrectEndWithOffset(Direction direction, double expectedX, double expectedY) {
        Point2D point = new Point2D(3, 3);
        double offset = 3;
        point = correctEndWithOffset(offset, direction, point);
        assertEquals(expectedX, point.getX(), 0.0000001);
        assertEquals(expectedY, point.getY(), 0.0000001);
    }
}
