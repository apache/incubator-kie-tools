package com.ait.lienzo.client.core.shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class PolyMorphicLineTest {

    @Test
    public void testDrawOrthogonalLinePointsTailAboveHeadTwoPoints() {
        Point2D p0 = new Point2D(1114, 328);
        Point2D p1 = new Point2D(1134, 132);

        Point2DArray ps = new Point2DArray();
        ps.push(p0, p1);
        Direction headDirection = Direction.SOUTH;
        Direction tailDirection = Direction.WEST;

        final NFastDoubleArray array = PolyMorphicLine.drawOrthogonalLinePoints(ps,
                                                                                headDirection,
                                                                                tailDirection,
                                                                                1,
                                                                                1,
                                                                                true);

        assertEquals(5, array.size() / 2);
    }

    @Test
    public void testDrawOrthogonalLinePointsMultiplePointsHorizontalTips() {
        Point2D p0 = new Point2D(100, 100);
        Point2D p1 = new Point2D(200, 100);//horizontal
        Point2D p2 = new Point2D(200, 200);//vertical
        Point2D p3 = new Point2D(300, 200);//horizontal
        Point2D p4 = new Point2D(350, 200);//horizontal
        Point2D p5 = new Point2D(350, 300);//Vertical
        Point2D p6 = new Point2D(450, 300);//horizontal
        Point2D p7 = new Point2D(450, 400);//vertical
        Point2D p8 = new Point2D(500, 400);//horizontal

        Point2DArray ps = new Point2DArray();
        ps.push(p0, p1, p2, p3, p4, p5, p6, p7, p8);
        Direction headDirection = Direction.WEST;
        Direction tailDirection = Direction.EAST;

        final NFastDoubleArray array = PolyMorphicLine.drawOrthogonalLinePoints(ps,
                                                                                headDirection,
                                                                                tailDirection,
                                                                                20,
                                                                                20,
                                                                                true);

        assertEquals(10, array.size() / 2);
    }

    @Test
    public void testDrawOrthogonalLinePointsMultiplePointsVerticalTips() {
        Point2D p0 = new Point2D(200, 100);
        Point2D p1 = new Point2D(200, 200);//vertical
        Point2D p2 = new Point2D(300, 200);//horizontal
        Point2D p3 = new Point2D(350, 200);//horizontal
        Point2D p4 = new Point2D(350, 300);//Vertical
        Point2D p5 = new Point2D(450, 300);//horizontal
        Point2D p6 = new Point2D(450, 400);//vertical

        Point2DArray ps = new Point2DArray();
        ps.push(p0, p1, p2, p3, p4, p5, p6);
        Direction headDirection = Direction.NORTH;
        Direction tailDirection = Direction.SOUTH;

        final NFastDoubleArray array = PolyMorphicLine.drawOrthogonalLinePoints(ps,
                                                                                headDirection,
                                                                                tailDirection,
                                                                                20,
                                                                                20,
                                                                                true);

        assertEquals(8, array.size() / 2);
    }

    @Test
    public void testLineAfterResetHeadDirection() {
        Point2D p0 = new Point2D(1114, 328);
        Point2D p1 = new Point2D(1134, 132);
        Direction headDirection = Direction.SOUTH;
        Direction tailDirection = Direction.WEST;

        final PolyMorphicLine line = new PolyMorphicLine(p0, p1);
        line.setHeadDirection(headDirection);
        line.setTailDirection(tailDirection);
        line.parse();

        //Before correction
        assertEquals(2, line.points.getLength());

        line.resetHeadDirectionPoints(new ArrayList<>());

        //After correction
        assertEquals(Direction.SOUTH, line.getHeadDirection());
        assertEquals(Direction.WEST, line.getTailDirection());
        assertEquals(5, line.points.getLength());
    }

    @Test
    public void testLineAfterResetTailDirection() {
        Point2D p0 = new Point2D(1114, 328);
        Point2D p1 = new Point2D(1134, 132);
        Direction headDirection = Direction.SOUTH;
        Direction tailDirection = Direction.WEST;

        final PolyMorphicLine line = new PolyMorphicLine(p0, p1);
        line.setHeadDirection(headDirection);
        line.setTailDirection(tailDirection);
        line.parse();

        //Before correction
        assertEquals(2, line.points.getLength());

        line.resetTailDirectionPoints(new ArrayList<>());

        //After correction
        assertEquals(Direction.SOUTH, line.getHeadDirection());
        assertEquals(Direction.WEST, line.getTailDirection());
        assertEquals(5, line.points.getLength());
    }

    @Test
    public void testIsVertical() {
        Point2D p0 = new Point2D(1114, 132);
        Point2D p1 = new Point2D(1114, 328);

        PolyMorphicLine.isVertical(p0, p1);

        assertTrue(PolyMorphicLine.isVertical(p0, p1));
    }

    @Test
    public void testIsNotVertical() {
        Point2D p0 = new Point2D(1114, 132);
        Point2D p1 = new Point2D(1115, 328);

        assertFalse(PolyMorphicLine.isVertical(p0, p1));
    }

    @Test
    public void testIsHorizontal() {
        Point2D p0 = new Point2D(500, 132);
        Point2D p1 = new Point2D(1114, 132);

        assertTrue(PolyMorphicLine.isHorizontal(p0, p1));
    }

    @Test
    public void testIsNotHorizontal() {
        Point2D p0 = new Point2D(1114, 132);
        Point2D p1 = new Point2D(1115, 328);

        assertFalse(PolyMorphicLine.isHorizontal(p0, p1));
    }

    @Test
    public void testIsOrthogonalVertical() {
        Point2D p0 = new Point2D(1114, 132);
        Point2D p1 = new Point2D(1114, 328);

        assertTrue(PolyMorphicLine.isOrthogonal(p0, p1));
    }

    @Test
    public void testIsOrthogonalHorizontal() {
        Point2D p0 = new Point2D(500, 132);
        Point2D p1 = new Point2D(1114, 132);

        assertTrue(PolyMorphicLine.isOrthogonal(p0, p1));
    }

    @Test
    public void testIsNotOrthogonal() {
        Point2D p0 = new Point2D(1114, 132);
        Point2D p1 = new Point2D(1115, 328);

        assertFalse(PolyMorphicLine.isOrthogonal(p0, p1));
    }

    @Test
    public void testGetOrthogonalDirectionSouth() {
        Point2D p0 = new Point2D(500, 100);
        Point2D p1 = new Point2D(500, 200);

        assertEquals(Direction.SOUTH, PolyMorphicLine.getOrthogonalDirection(p0, p1));
    }

    @Test
    public void testGetOrthogonalDirectionNorth() {
        Point2D p0 = new Point2D(500, 200);
        Point2D p1 = new Point2D(500, 100);

        assertEquals(Direction.NORTH, PolyMorphicLine.getOrthogonalDirection(p0, p1));
    }

    @Test
    public void testGetOrthogonalDirectionEast() {
        Point2D p0 = new Point2D(200, 200);
        Point2D p1 = new Point2D(500, 200);

        assertEquals(Direction.EAST, PolyMorphicLine.getOrthogonalDirection(p0, p1));
    }

    @Test
    public void testGetOrthogonalDirectionWest() {
        Point2D p0 = new Point2D(500, 200);
        Point2D p1 = new Point2D(200, 200);

        assertEquals(Direction.WEST, PolyMorphicLine.getOrthogonalDirection(p0, p1));
    }

    @Test
    public void testGetOrthogonalDirectionNone() {
        Point2D p0 = new Point2D(500, 200);
        Point2D p1 = new Point2D(200, 300);

        assertEquals(Direction.NONE, PolyMorphicLine.getOrthogonalDirection(p0, p1));
    }

    @Test
    public void testCorrectComputedPointsTwoPoints() {
        Point2D p0 = new Point2D(500, 200);
        Point2D p1 = new Point2D(200, 300);
        Point2DArray points = new Point2DArray();

        points.push(p0, p1);

        final Point2DArray pointsCorrected = PolyMorphicLine.correctComputedPoints(points,
                                                                                   new ArrayList<>(),
                                                                                   new ArrayList<>());

        assertEquals(points.size(), pointsCorrected.size());
        for (int i = 0; i < pointsCorrected.size(); i++) {
            assertTrue(points.get(i).equals(pointsCorrected.get(i)));
        }
    }

    @Test
    public void testCorrectComputedPointsOrthogonal() {
        Point2D p0 = new Point2D(100, 100);
        Point2D p1 = new Point2D(200, 100);//horizontal
        Point2D p2 = new Point2D(200, 200);//vertical
        Point2D p3 = new Point2D(300, 200);//horizontal
        Point2D p4 = new Point2D(350, 200);//horizontal
        Point2D p5 = new Point2D(350, 300);//Vertical
        Point2D p6 = new Point2D(450, 300);//horizontal
        Point2D p7 = new Point2D(450, 400);//vertical
        Point2D p8 = new Point2D(500, 400);//horizontal

        Point2DArray points = new Point2DArray();

        points.push(p0, p1, p2, p3, p4, p5, p6, p7, p8);

        final List<Point2D> pointsCorrected = Arrays.asList(PolyMorphicLine.correctComputedPoints(points,
                                                                                                  new ArrayList<>(),
                                                                                                  new ArrayList<>()).asArray());

        assertEquals(8, pointsCorrected.size());
        assertTrue(pointsCorrected.contains(p0));
        assertTrue(pointsCorrected.contains(p1));
        assertTrue(pointsCorrected.contains(p2));

        // Point p3 must be removed from the set
        assertFalse(pointsCorrected.contains(p3));

        assertTrue(pointsCorrected.contains(p4));
        assertTrue(pointsCorrected.contains(p5));
        assertTrue(pointsCorrected.contains(p6));
        assertTrue(pointsCorrected.contains(p7));
        assertTrue(pointsCorrected.contains(p8));
    }

    @Test
    public void testCorrectComputedPointsWithNonOrthogonal() {
        Point2D p0 = new Point2D(100, 100);
        Point2D p1 = new Point2D(200, 100);//horizontal
        Point2D p2 = new Point2D(200, 200);//vertical
        Point2D p3 = new Point2D(300, 200);//horizontal
        Point2D p4 = new Point2D(350, 210);//horizontal
        Point2D p5 = new Point2D(350, 300);//Vertical
        Point2D p6 = new Point2D(450, 300);//horizontal

        //Non orthogonal
        Point2D p7 = new Point2D(450, 405);//vertical
        Point2D p8 = new Point2D(500, 410);//horizontal

        Point2DArray points = new Point2DArray();
        List<Point2D> nonOrthogonalPoints = new ArrayList<>();

        points.push(p0, p1, p2, p3, p4, p5, p6, p7, p8);
        //Non orthogonal
        nonOrthogonalPoints.add(p7);
        nonOrthogonalPoints.add(p8);

        final List<Point2D> pointsCorrected = Arrays.asList(PolyMorphicLine.correctComputedPoints(points,
                                                                                                  nonOrthogonalPoints,
                                                                                                  new ArrayList<>()).asArray());

        assertEquals(9, pointsCorrected.size());
        assertTrue(pointsCorrected.contains(p0));
        assertTrue(pointsCorrected.contains(p1));
        assertTrue(pointsCorrected.contains(p2));
        assertTrue(pointsCorrected.contains(p3));
        assertTrue(pointsCorrected.contains(p4));
        assertTrue(pointsCorrected.contains(p5));
        assertTrue(pointsCorrected.contains(p6));
        assertTrue(pointsCorrected.contains(p7));
        assertTrue(pointsCorrected.contains(p8));
    }

    @Test
    public void testCorrectComputedPointsUserDefined() {
        Point2D p0 = new Point2D(100, 100);
        Point2D p1 = new Point2D(200, 100);//horizontal
        Point2D p2 = new Point2D(200, 200);//vertical
        Point2D p3 = new Point2D(300, 200);//horizontal
        Point2D p4 = new Point2D(350, 200);//horizontal
        Point2D p5 = new Point2D(350, 300);//Vertical
        Point2D p6 = new Point2D(450, 300);//horizontal
        Point2D p7 = new Point2D(450, 400);//vertical
        Point2D p8 = new Point2D(500, 400);//horizontal

        Point2DArray points = new Point2DArray();
        List<Point2D> userDefinedPoints = new ArrayList<>();

        points.push(p0, p1, p2, p3, p4, p5, p6, p7, p8);

        //Orthogonal pinned points
        userDefinedPoints.add(p6);
        userDefinedPoints.add(p7);

        final List<Point2D> pointsCorrected = Arrays.asList(PolyMorphicLine.correctComputedPoints(points,
                                                                                                  new ArrayList<>(),
                                                                                                  userDefinedPoints).asArray());

        assertEquals(8, pointsCorrected.size());
        assertTrue(pointsCorrected.contains(p0));
        assertTrue(pointsCorrected.contains(p1));
        assertTrue(pointsCorrected.contains(p2));

        // Point p3 must be removed from the set
        assertFalse(pointsCorrected.contains(p3));

        assertTrue(pointsCorrected.contains(p4));
        assertTrue(pointsCorrected.contains(p5));
        assertTrue(pointsCorrected.contains(p6));
        assertTrue(pointsCorrected.contains(p7));
        assertTrue(pointsCorrected.contains(p8));
    }
}
