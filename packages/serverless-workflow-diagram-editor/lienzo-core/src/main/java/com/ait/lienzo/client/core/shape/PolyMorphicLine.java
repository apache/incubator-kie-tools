/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.client.core.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;
import jsinterop.annotations.JsProperty;

import static com.ait.lienzo.client.core.shape.OrthogonalLineUtils.addPoint;
import static com.ait.lienzo.shared.core.types.Direction.EAST;
import static com.ait.lienzo.shared.core.types.Direction.NONE;
import static com.ait.lienzo.shared.core.types.Direction.NORTH;
import static com.ait.lienzo.shared.core.types.Direction.SOUTH;
import static com.ait.lienzo.shared.core.types.Direction.WEST;

public class PolyMorphicLine extends AbstractDirectionalMultiPointShape<PolyMorphicLine> {

    static final double DEFAULT_OFFSET = 10d;

    static final double DEFAULT_CORRECTION_OFFSET = 0d;
    static final double CORRECTION_OFFSET = 1d;

    private Point2D m_headOffsetPoint;

    private Point2D m_tailOffsetPoint;

    @JsProperty
    private double cornerRadius;

    private double m_breakDistance;

    private List<Point2D> orthogonalIndexesToRecalculate = new ArrayList<>();
    private List<Point2D> inferredPoints = new ArrayList<>();
    private List<Point2D> userDefinedPoints = new ArrayList<>();

    public PolyMorphicLine(final Point2D... points) {
        this(Point2DArray.fromArrayOfPoint2D(points));
    }

    public PolyMorphicLine(final Point2DArray points) {
        super(ShapeType.ORTHOGONAL_POLYLINE);

        setControlPoints(points);
        setHeadDirection(NONE);
        setTailDirection(NONE);
    }

    public PolyMorphicLine(final Point2DArray points, final double corner) {
        this(points);

        setCornerRadius(corner);
    }

    private List<Point2D> getUserDefinedPoints() {
        List<Point2D> userPoints = new ArrayList<>();
        for (int i = 1; i < points.size() - 1; i++) {
            if (!inferredPoints.contains(points.get(i))) {
                userPoints.add(points.get(i));
            }
        }

        return userPoints;
    }

    private void addInferredPoints(Point2DArray points) {
        cleanInferredPoints();

        for (int i = 0; i < points.size(); i++) {
            if (!inferredPoints.contains(points.get(i))) {
                inferredPoints.add(points.get(i));
            }
        }

        inferredPoints.removeAll(userDefinedPoints);
        cleanInferredPoints();
    }

    // Clear points that do not exist anymore
    private void cleanInferredPoints() {
        List<Point2D> pointsToRemove = new ArrayList<>();
        pointsToRemove.addAll(userDefinedPoints);
        for (int i = 0; i < inferredPoints.size(); i++) {
            Point2D p = inferredPoints.get(i);
            if (!contains(p)) {
                pointsToRemove.add(p);
            }
        }
        inferredPoints.removeAll(pointsToRemove);
        cleanUserPoints();
    }

    private void cleanUserPoints() {
        List<Point2D> pointsToRemove = new ArrayList<>();
        for (int i = 0; i < userDefinedPoints.size(); i++) {
            Point2D p = userDefinedPoints.get(i);
            if (!contains(p)) {
                pointsToRemove.add(p);
            }
        }
        userDefinedPoints.removeAll(pointsToRemove);
    }

    private boolean contains(Point2D p) {
        for (int i = 0; i < points.size(); i++) {
            Point2D point2D = points.get(i);
            if (p.equals(point2D)) {
                return true;
            }
        }
        return false;
    }

    // Handle point states coming from outside of lienzo
    public void addManagedPoint(final Double x, final Double y, boolean isInferred) {
        Point2D managedPoint = new Point2D(x, y);
        for (int i = 0; i < points.size(); i++) {
            if (managedPoint.equals(points.get(i))) {
                if (isInferred) {
                    if (!inferredPoints.contains(points.get(i))) {
                        inferredPoints.add(points.get(i));
                    }
                } else if (!userDefinedPoints.contains(points.get(i))) {
                    inferredPoints.remove(points.get(i));
                }
            }
        }
    }

    public List<Point2D> handleInferredPoints(final Point2D p0, final Point2D p1) {
        if (null != p0 && null != p1) {
            final boolean isP0UserPoint = userDefinedPoints.contains(p0);
            final boolean isP1UserPoint = userDefinedPoints.contains(p1);

            if (!isP0UserPoint && !isP1UserPoint) {
                inferredPoints.remove(p0);
                inferredPoints.remove(p1);
            } else {
                if (isP0UserPoint && isP1UserPoint) {
                    if (!isPartOfOtherUserSegment(p0, true)) {
                        inferredPoints.add(p0);
                    }
                    if (!isPartOfOtherUserSegment(p1, false)) {
                        inferredPoints.add(p1);
                    }
                } else if (!isP0UserPoint && isP1UserPoint) {
                    inferredPoints.remove(p0);
                } else if (isP0UserPoint && !isP1UserPoint) {
                    inferredPoints.remove(p1);
                }
            }
        }

        userDefinedPoints.clear();
        userDefinedPoints.addAll(getUserDefinedPoints());

        return inferredPoints;
    }

    private boolean isPartOfOtherUserSegment(final Point2D point, final boolean before) {
        int i = 0;
        for (; i < points.size() - 1; i++) {
            if (points.get(i).equals(point)) {
                break;
            }
        }

        if (i > 0 && i < points.size() - 1) {
            if (before) {
                return userDefinedPoints.contains(points.get(i - 1));
            }

            return userDefinedPoints.contains(points.get(i + 1));
        }

        return false;
    }

    public boolean isInferred(final Point2D point) {
        for (int i = 0; i < inferredPoints.size(); i++) {
            if (inferredPoints.get(i).equals(point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean parse() {
        if (0 == points.size()) {
            return false;
        }

        userDefinedPoints.clear();
        userDefinedPoints.addAll(getUserDefinedPoints());

        infer();

        inferDirectionChanges();

        return parsePoints();
    }

    private boolean parsePoints() {
        Point2DArray list = points;

        list = list.noAdjacentPoints();
        final int size = list.size();

        if (0 == size) {
            return false;
        }

        final PathPartList path = getPathPartList();
        final double headOffset = getHeadOffset();
        final double tailOffset = getTailOffset();

        if (size > 1) {
            m_headOffsetPoint = Geometry.getProjection(list.get(0), list.get(1), headOffset);
            m_tailOffsetPoint = Geometry.getProjection(list.get(size - 1), list.get(size - 2), tailOffset);

            path.M(m_headOffsetPoint);

            final double corner = getCornerRadius();
            if (corner <= 0) {
                for (int i = 1; i < size - 1; i++) {
                    path.L(list.get(i));
                }

                path.L(m_tailOffsetPoint);
            } else {
                list = list.copy();
                list.set(size - 1, m_tailOffsetPoint);

                Geometry.drawArcJoinedLines(path, list, corner);
            }
        } else if (size == 1) {
            m_headOffsetPoint = list.get(0).copy().offset(headOffset, headOffset);
            m_tailOffsetPoint = list.get(0).copy().offset(tailOffset, tailOffset);

            path.M(m_headOffsetPoint);

            final double corner = getCornerRadius();
            if (corner <= 0) {
                path.L(m_tailOffsetPoint);
            } else {
                list = Point2DArray.fromArrayOfPoint2D(list.get(0).copy(), list.get(0).copy());

                Geometry.drawArcJoinedLines(path, list, corner);
            }
        }

        cleanInferredPoints();

        return true;
    }

    private boolean isHeadDirectionChanged(List<Point2D> nonOrthogonalPoints) {
        if (!isFirstSegmentOrthogonal()) {
            return false;
        }
        int size = points.size();
        if (size >= 2) {
            Direction headDirection = getHeadDirection();
            if (null != headDirection && !headDirection.equals(NONE)) {
                Point2D p0 = points.get(0);
                Point2D p1 = points.get(1);
                if (isOrthogonal(p0, p1) &&
                        !nonOrthogonalPoints.contains(p1) &&
                        getDefaultHeadOffset() != 0) {
                    Direction actualHeadDirection = getOrthogonalDirection(p0, p1);
                    if (!headDirection.equals(actualHeadDirection)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isTailDirectionChanged(List<Point2D> nonOrthogonalPoints) {
        if (!isLastSegmentOrthogonal()) {
            return false;
        }
        int size = points.size();
        if (size >= 2) {
            Direction tailDirection = getTailDirection();
            if (null != tailDirection && !tailDirection.equals(NONE)) {
                Point2D pN_1 = points.get(size - 2);
                Point2D pN = points.get(size - 1);
                if (isOrthogonal(pN, pN_1) &&
                        !nonOrthogonalPoints.contains(pN_1) &&
                        getDefaultTailOffset() != 0) {
                    Direction actualTailDirection = getOrthogonalDirection(pN, pN_1);
                    if (!tailDirection.equals(actualTailDirection)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void inferDirectionChanges() {
        List<Point2D> nonOrthogonalPoints = computeNonOrthogonalPoints();
        if (isHeadDirectionChanged(nonOrthogonalPoints)) {
            resetHeadDirectionPoints(nonOrthogonalPoints);
        }
        if (isTailDirectionChanged(nonOrthogonalPoints)) {
            resetTailDirectionPoints(nonOrthogonalPoints);
        }
    }

    void resetHeadDirectionPoints(List<Point2D> nonOrthogonalPoints) {
        int size = points.size();
        Point2D p0 = points.get(0);

        int i = 1;
        for (; i < size; i++) {
            Point2D pI = points.get(i);
            if (nonOrthogonalPoints.contains(pI) || userDefinedPoints.contains(pI)) {
                break;
            }
        }

        Point2D pI = points.get(i - 1);
        Point2DArray headPoints = inferOrthogonalSegments(p0, pI, getHeadDirection(), getTailDirection(), getDefaultHeadOffset(), getDefaultTailOffset());
        for (; i < size; i++) {
            headPoints.push(points.get(i));
        }

        this.points = correctComputedPoints(headPoints, nonOrthogonalPoints, userDefinedPoints);
        addInferredPoints(headPoints);
    }

    void resetTailDirectionPoints(List<Point2D> nonOrthogonalPoints) {
        int size = points.size();
        Point2D p0 = points.get(size - 1);

        int i = size - 2;
        for (; i >= 0; i--) {
            Point2D pI = points.get(i);

            if (nonOrthogonalPoints.contains(pI) || userDefinedPoints.contains(pI)) {
                break;
            }
        }

        Point2DArray staticPoints = new Point2DArray();
        for (int j = 0; j <= i; j++) {
            staticPoints.push(points.get(j));
        }

        Point2D pI = points.get(i + 1);
        Point2DArray tailPoints = inferOrthogonalSegments(pI, p0, getHeadDirection(), getTailDirection(), getDefaultHeadOffset(), getDefaultTailOffset());

        for (int j = 0; j < tailPoints.size(); j++) {
            staticPoints.push(tailPoints.get(j));
        }

        this.points = correctComputedPoints(staticPoints, nonOrthogonalPoints, userDefinedPoints);
        addInferredPoints(tailPoints);
    }

    private void infer() {
        if (!orthogonalIndexesToRecalculate.isEmpty()) {
            Point2DArray inferred = inferOrthogonalSegments(getHeadDirection(), getTailDirection(), getDefaultHeadOffset(), getDefaultTailOffset());
            Point2DArray corrected = correctComputedPoints(inferred, Collections.<Point2D>emptyList(), userDefinedPoints);
            setPoints(corrected);
            getLayer().batch();
            orthogonalIndexesToRecalculate.clear();
            addInferredPoints(corrected);
        }
    }

    private static List<Point2D> computeNonOrthogonalPoints(Point2DArray points,
                                                            boolean isFirstSegmentOrthogonal,
                                                            boolean isLastSegmentOrthogonal) {
        List<Point2D> nonOrthogonalPoints = new ArrayList<Point2D>();
        int size = points.size();
        if (size > 2) {
            int i = 1;
            if (!isFirstSegmentOrthogonal) {
                nonOrthogonalPoints.add(points.get(1));
                i = 2;
            }
            if (!isLastSegmentOrthogonal) {
                size -= 2;
                nonOrthogonalPoints.add(points.get(size));
            }
            for (; i < size - 1; i++) {
                Point2D lastP = points.get(i - 1);
                Point2D p = points.get(i);
                Point2D nextP = points.get(i + 1);
                if (!isOrthogonal(lastP, p) || !isOrthogonal(p, nextP)) {
                    nonOrthogonalPoints.add(p);
                }
            }
        }
        return nonOrthogonalPoints;
    }

    public List<Point2D> computeNonOrthogonalPoints() {
        return orthogonalIndexesToRecalculate.isEmpty() ?
                computeNonOrthogonalPoints(points, isFirstSegmentOrthogonal(), isLastSegmentOrthogonal()) :
                Collections.<Point2D>emptyList();
    }

    @Override
    public int getHeadReferencePointIndex() {
        List<Point2D> nonOrthogonalPoints = computeNonOrthogonalPoints();

        if (!nonOrthogonalPoints.isEmpty() || !userDefinedPoints.isEmpty()) {
            Point2D nonOrthogonalP0 = nonOrthogonalPoints.isEmpty() ? null : nonOrthogonalPoints.get(nonOrthogonalPoints.size() - 1);
            Point2D userP0 = userDefinedPoints.isEmpty() ? null : userDefinedPoints.get(userDefinedPoints.size() - 1);

            for (int i = points.size() - 1; i >= 0; i--) {
                if ((nonOrthogonalP0 != null && points.get(i).equals(nonOrthogonalP0)) ||
                        (userP0 != null && points.get(i).equals(userP0))) {
                    return indexOfPoint(points.get(i));
                }
            }
        }

        return -1;
    }

    @Override
    public int getTailReferencePointIndex() {
        List<Point2D> nonOrthogonalPoints = computeNonOrthogonalPoints();

        if (!nonOrthogonalPoints.isEmpty() || !userDefinedPoints.isEmpty()) {
            Point2D nonOrthogonalP0 = nonOrthogonalPoints.isEmpty() ? null : nonOrthogonalPoints.get(0);
            Point2D userP0 = userDefinedPoints.isEmpty() ? null : userDefinedPoints.get(0);

            for (int i = 0; i < points.size(); i++) {
                if ((nonOrthogonalP0 != null && points.get(i).equals(nonOrthogonalP0)) ||
                        (userP0 != null && points.get(i).equals(userP0))) {
                    return indexOfPoint(points.get(i));
                }
            }
        }

        return -1;
    }

    private boolean isFirstSegmentOrthogonal = true;
    private boolean isLastSegmentOrthogonal = true;

    public void setFirstSegmentOrthogonal(boolean orthogonal) {
        this.isFirstSegmentOrthogonal = orthogonal;
    }

    public void setLastSegmentOrthogonal(boolean orthogonal) {
        this.isLastSegmentOrthogonal = orthogonal;
    }

    public boolean isFirstSegmentOrthogonal() {
        return isFirstSegmentOrthogonal;
    }

    public boolean isLastSegmentOrthogonal() {
        return isLastSegmentOrthogonal;
    }

    @Override
    public PolyMorphicLine refresh() {
        return super.refresh();
    }

    @Override
    public void updatePointAtIndex(int index, double x, double y) {
        List<Point2D> nonOrthogonalPoints = computeNonOrthogonalPoints();
        boolean isHead = index == 0;
        boolean isTail = index == (points.size() - 1);

        if (!isHead && !isTail) {
            super.updatePointAtIndex(index, x, y);
            return;
        }

        Point2D point = points.get(index);
        double dx = x - point.getX();
        double dy = y - point.getY();
        if (dx == 0 && dy == 0) {
            return;
        }

        Point2D other = null;
        if (isHead) {
            propagateUp(index, dx, dy, getDefaultHeadOffset(), nonOrthogonalPoints);
            other = points.get(1);
        } else {
            propagateDown(index, dx, dy, getDefaultTailOffset(), nonOrthogonalPoints);
            other = points.get(index - 1);
        }

        boolean isNonOrthogonal = nonOrthogonalPoints.contains(other);
        boolean isNonUserDefined = !userDefinedPoints.contains(other);
        boolean isNonInferred = !inferredPoints.contains(other);
        if (isNonOrthogonal && isNonUserDefined && isNonInferred) {
            if (isVertical(point, other)) {
                // It was NON orthogonal but now, after drag the point, it results vertical
                point.setX(point.getX() + 2);
            } else if (isHorizontal(point, other)) {
                // It was NON orthogonal but now, after drag the point, it results horiztonal
                point.setY(point.getY() + 2);
            }
        }

        Point2DArray corrected = correctComputedPoints(points, nonOrthogonalPoints, userDefinedPoints);
        setPoints(corrected);
        refresh();
    }

    public void propagateUp(int index, double dx, double dy, double min, List<Point2D> nonOrthogonalPoints) {
        if (dx == 0 && dy == 0) {
            return;
        }
        if (index >= (points.size() - 1)) {
            return;
        }
        int nextIndex = index + 1;
        Point2D candidate = points.get(index);
        Point2D next = points.get(nextIndex);

        boolean isHorizontal = false;
        boolean isVertical = false;

        // If the line has two points and the segment is flagged as nonOrthogonal it must behave as nonOrthogonal
        boolean isNonOrthogonal = !(points.size() == 2 && (!isFirstSegmentOrthogonal() || !isLastSegmentOrthogonal()));
        if (!nonOrthogonalPoints.contains(next) && isNonOrthogonal) {
            isHorizontal = isHorizontal(candidate, next);
            isVertical = isVertical(candidate, next);
            double px = 0;
            double py = 0;

            boolean isNextLast = points.size() > 2 && nextIndex >= (points.size() - 1);
            boolean isFirstOrLast = index < 1 || isNextLast;
            double segmentMin = isFirstOrLast ? min : 0;

            Point2D last = null;
            if (index == 0 || isNextLast) {
                last = points.get(points.size() - 1);
            }

            final double offset = getDefaultHeadOffset() + getDefaultTailOffset();
            if (isHorizontal) {
                px = propagateOrthogonalSegmentUp(candidate.getX(), next.getX(), dx, segmentMin, null != last ? last.getX() - offset : null);
                py = dy;
                dx = isNextLast && px != 0 ? 0 : dx;
                dy = !isNextLast ? dy : 0;
            } else if (isVertical) {
                dx = !isNextLast ? dx : 0;
                px = dx;
                py = propagateOrthogonalSegmentUp(candidate.getY(), next.getY(), dy, segmentMin, null != last ? last.getY() - offset : null);
                dy = isNextLast && py != 0 ? 0 : dy;
            } else {
                // No need to propagate on no orthogonal segments
                px = 0;
                py = 0;
            }

            boolean propagate = px != 0 || py != 0;
            if (propagate) {
                propagateUp(nextIndex, px, py, min, nonOrthogonalPoints);
            }
        }

        if (dx != 0 || dy != 0) {
            candidate.setX(candidate.getX() + dx);
            candidate.setY(candidate.getY() + dy);
        }

        if (isHorizontal) {
            if ((dy != 0) && (candidate.getY() != next.getY())) {
                orthogonalIndexesToRecalculate.add(candidate);
            }
        }
        if (isVertical) {
            if (dx != 0 && (candidate.getX() != next.getX())) {
                orthogonalIndexesToRecalculate.add(candidate);
            }
        }
    }

    private double propagateOrthogonalSegmentUp(double candidate, double next, double dist, double min, Double lastValue) {
        double p = 0;
        double ad = Math.abs(next - candidate);
        double cx = candidate + dist;
        double d = next > candidate ? next - cx : cx - next;
        boolean grows = d > Math.abs(ad);

        if (d >= min && !grows) {
            // Do not propagate
            p = 0;
        }
        if (d < min && !grows) {
            p = (cx + (min * (next > candidate ? 1 : -1))) - next;
        }
        if (d < min && grows) {
            // Do not propagate, will propagate once d = 0 & grows, if necessary
            p = 0;
        }
        if (d >= min && grows) {
            p = 0;
            // Propagate back
            if (null != lastValue) {
                if (cx < lastValue) {
                    // If last point is: after -> do not propagate
                    p = candidate < lastValue ? 0 : lastValue - candidate;
                } else {
                    // If last point is: before -> propagate?
                    p = dist;
                }
            }
        }
        return p;
    }

    public void propagateDown(int index, double dx, double dy, double min, List<Point2D> nonOrthogonalPoints) {
        if (dx == 0 && dy == 0) {
            return;
        }
        if (index < 1) {
            return;
        }

        int nextIndex = index - 1;
        Point2D candidate = points.get(index);
        Point2D next = points.get(nextIndex);

        boolean isHorizontal = false;
        boolean isVertical = false;

        // If the line has two points and the segment is flagged as nonOrthogonal it must behave as nonOrthogonal
        boolean isNonOrthogonal = !(points.size() == 2 && (!isFirstSegmentOrthogonal() || !isLastSegmentOrthogonal()));
        if (!nonOrthogonalPoints.contains(next) && isNonOrthogonal) {
            isHorizontal = isHorizontal(candidate, next);
            isVertical = isVertical(candidate, next);
            double px = 0;
            double py = 0;
            boolean isNextFirst = points.size() > 2 && nextIndex < 1;
            double segmentMin = index >= (points.size() - 1) || isNextFirst ? min : 0;

            Point2D first = null;
            if (points.size() > 2 && (index == (points.size() - 1) || isNextFirst)) {
                first = points.get(0);
            }

            final double offset = getDefaultHeadOffset() + getDefaultTailOffset();
            if (isHorizontal) {
                px = propagateOrthogonalSegmentDown(candidate.getX(), next.getX(), dx, segmentMin, null != first ? first.getX() + offset : null);
                py = dy;
                dx = isNextFirst && px != 0 ? 0 : dx;
                dy = !isNextFirst ? dy : 0;
            } else if (isVertical) {
                dx = !isNextFirst ? dx : 0;
                px = dx;
                py = propagateOrthogonalSegmentDown(candidate.getY(), next.getY(), dy, segmentMin, null != first ? first.getY() + offset : null);
                dy = isNextFirst && py != 0 ? 0 : dy;
            } else {
                // No need to propagate on no orthogonal segments
                px = 0;
                py = 0;
            }

            boolean propagate = px != 0 || py != 0;
            if (propagate) {
                propagateDown(nextIndex, px, py, min, nonOrthogonalPoints);
            }
        }

        if (dx != 0 || dy != 0) {
            candidate.setX(candidate.getX() + dx);
            candidate.setY(candidate.getY() + dy);
        }

        if (isHorizontal) {
            if ((dy != 0) && (candidate.getY() != next.getY())) {
                orthogonalIndexesToRecalculate.add(next);
            }
        }
        if (isVertical) {
            if (dx != 0 && (candidate.getX() != next.getX())) {
                orthogonalIndexesToRecalculate.add(next);
            }
        }
    }

    private double propagateOrthogonalSegmentDown(double candidate, double next, double dist, double min, Double lastValue) {
        double p = 0;
        double ad = Math.abs(next - candidate);
        double cx = candidate + dist;
        double d = next > candidate ? next - cx : cx - next;
        boolean grows = d > Math.abs(ad);

        if (d >= min && !grows) {
            // Do not propagate
            p = 0;
        }
        if (d < min && !grows) {
            p = (cx + (min * (next > candidate ? 1 : -1))) - next;
        }
        if (d < min && grows) {
            // Do not propagate, will propagate once d = 0 & grows, if necessary
            p = 0;
        }
        if (d >= min && grows) {
            p = 0;
            // Propagate back
            if (null != lastValue) {
                if (cx > lastValue) {
                    // If last point is: after -> do not propagate
                    p = candidate > lastValue ? 0 : lastValue - candidate;
                } else {
                    // If last point is: before -> propagate?
                    p = dist;
                }
            }
        }
        return p;
    }

    public Point2DArray inferOrthogonalSegments(Direction headDirection, Direction tailDirection, double headOffset, double tailOffset) {
        Point2DArray result = new Point2DArray();
        Point2DArray copy = points;
        int size = copy.size();
        for (int i = 0; i < size; i++) {
            if (orthogonalIndexesToRecalculate.contains(copy.get(i)) && (i < size - 1)) {
                boolean isFirstOrLastPoint = (i == 0 || i == (size - 1));
                headOffset = isFirstOrLastPoint ? headOffset : 0;
                tailOffset = isFirstOrLastPoint ? tailOffset : 0;
                Point2DArray inferred = inferOrthogonalSegments(copy, i, headDirection, tailDirection, headOffset, tailOffset);
                for (int j = 0; j < inferred.size(); j++) {
                    Point2D o = inferred.get(j);
                    result.push(o);
                }
                i++;
            } else {
                Point2D point = copy.get(i);
                result.push(point);
            }
        }

        return result;
    }

    public Point2DArray inferOrthogonalSegments(Point2DArray copy, int index, Direction headDirection, Direction tailDirection, double headOffset, double tailOffset) {
        Point2D p0 = copy.get(index);
        Point2D p1 = copy.get(index + 1);
        return inferOrthogonalSegments(p0, p1, headDirection, tailDirection, headOffset, tailOffset);
    }

    public Point2DArray inferOrthogonalSegments(Point2D p0, Point2D p1, Direction headDirection, Direction tailDirection, double headOffset, double tailOffset) {
        Point2DArray result = new Point2DArray();
        result.push(p0);
        if (isOrthogonal(p0, p1)) {
            result.push(p1);
            return result;
        }
        Point2DArray ps = new Point2DArray();
        ps.push(p0.copy());
        ps.push(p1.copy());
        NFastDoubleArray p = drawOrthogonalLinePoints(ps, headDirection, tailDirection, headOffset, tailOffset, true);
        Point2DArray array = Point2DArray.fromNFastDoubleArray(p);
        array = correctComputedPoints(array, Collections.<Point2D>emptyList(), userDefinedPoints);
        if (array.size() > 2) {
            for (int j = (headOffset != 0 ? 0 : 1); j < array.size(); j++) {
                Point2D op = array.get(j);
                result.push(op);
            }
        }
        if (!p1.equals(result.get(result.size() - 1))) {
            result.push(p1);
        }
        return result;
    }

    public static NFastDoubleArray drawOrthogonalLinePoints(final Point2DArray points, Direction headDirection, Direction tailDirection,
                                                            double headOffset, double tailOffset, boolean write) {
        final NFastDoubleArray buffer = new NFastDoubleArray();
        // Edge case with two points where the head is after or below the tail breaking orthogonality
        final double correction = points.size() == 2 ? CORRECTION_OFFSET : DEFAULT_CORRECTION_OFFSET;

        Point2D p0 = points.get(0);
        p0 = OrthogonalLineUtils.correctP0(headDirection, correction, headOffset, write, buffer, p0);

        int i = 1;
        Direction direction = headDirection;
        final int size = points.size();
        Point2D p1;

        for (; i < size - 1; i++) {
            p1 = points.get(i);

            if (points.size() > 2 && i > 1) {
                direction = OrthogonalLineUtils.getNextDirection(direction, p0.getX(), p0.getY(), p1.getX(), p1.getY());
                addPoint(buffer, p1.getX(), p1.getY(), write);
            } else {
                direction = OrthogonalLineUtils.drawOrthogonalLineSegment(buffer, direction, null, p0.getX(), p0.getY(), p1.getX(), p1.getY(), write);
            }

            if (null == direction) {
                return null;
            }
            p0 = p1;
        }
        p1 = points.get(size - 1);

        if (points.size() == 2 || (points.size() > 2 && isOrthogonal(p0, p1))) {
            OrthogonalLineUtils.drawTail(points, buffer, direction, tailDirection, p0, p1, correction, headOffset, tailOffset);
        } else {
            addPoint(buffer, p1.getX(), p1.getY(), write);
        }

        return buffer;
    }

    public static Point2DArray correctComputedPoints(Point2DArray points, List<Point2D> nonOrthogonalPoints, List<Point2D> userDefinedPoints) {
        Point2DArray result = new Point2DArray();
        if (points.size() == 2) {
            result.push(points.get(0));
            result.push(points.get(1));
        } else if (points.size() > 2) {
            Point2D ref = points.get(0);
            result.push(ref);
            for (int i = 1; i < (points.size() - 1); i++) {
                Point2D p0 = points.get(i);
                Point2D p1 = points.get(i + 1);

                boolean write = true;

                if (!nonOrthogonalPoints.contains(p0) && !userDefinedPoints.contains(p0)) { //check for non user defined points
                    if (ref.getX() == p0.getX() && p0.getX() == p1.getX()) {
                        write = false;
                    }
                    if (ref.getY() == p0.getY() && p0.getY() == p1.getY()) {
                        write = false;
                    }
                }

                if (write) {
                    result.push(p0);
                }

                if (i == points.size() - 2) {
                    result.push(p1);
                }

                ref = p0;
            }
        }
        return result;
    }

    public static boolean isVertical(Point2D p0, Point2D p1) {
        return p1.getX() == p0.getX();
    }

    public static boolean isHorizontal(Point2D p0, Point2D p1) {
        return p1.getY() == p0.getY();
    }

    public static boolean isOrthogonal(Point2D p0, Point2D p1) {
        return isVertical(p0, p1) || isHorizontal(p0, p1);
    }

    public static Direction getOrthogonalDirection(final Point2D p0, final Point2D p1) {
        if (isHorizontal(p0, p1)) {
            return p0.getX() < p1.getX() ? EAST : WEST;
        }
        if (isVertical(p0, p1)) {
            return p0.getY() < p1.getY() ? SOUTH : NORTH;
        }
        return NONE;
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (getPathPartList().size() < 1) {
            if (!parse()) {
                return BoundingBox.fromDoubles(0, 0, 0, 0);
            }
        }
        return getPathPartList().getBoundingBox();
    }

    @Override
    protected boolean fill(Context2D context, double alpha) {
        return false;
    }

    @Override
    public PolyMorphicLine setHeadDirection(Direction direction) {
        headDirection = direction;
        return this;
    }

    @Override
    public PolyMorphicLine setTailDirection(Direction direction) {
        tailDirection = direction;
        return this;
    }

    @Override
    public PolyMorphicLine setHeadOffset(double offset) {
        this.headOffset = offset;
        return this;
    }

    @Override
    public PolyMorphicLine setTailOffset(double offset) {
        this.tailOffset = offset;
        return this;
    }

    public double getDefaultHeadOffset() {
        return super.getHeadOffset() > 0 ? super.getHeadOffset() : DEFAULT_OFFSET;
    }

    public double getDefaultTailOffset() {
        return super.getTailOffset() > 0 ? super.getTailOffset() : DEFAULT_OFFSET;
    }

    @Override
    public PolyMorphicLine setControlPoints(Point2DArray points) {
        this.points = points;
        addInferredPoints(points);
        return this;
    }

    @Override
    public PolyMorphicLine setPoints(Point2DArray points) {
        this.points = points;
        addInferredPoints(points);
        return this;
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public PolyMorphicLine setCornerRadius(final double radius) {
        this.cornerRadius = cornerRadius;

        return refresh();
    }

    public double getBreakDistance() {
        return m_breakDistance;
    }

    public PolyMorphicLine setBreakDistance(double distance) {
        m_breakDistance = distance;

        return refresh();
    }

    @Override
    public PolyMorphicLine setPoint2DArray(final Point2DArray points) {
        return setControlPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray() {
        return getControlPoints();
    }

    @Override
    public boolean isControlPointShape() {
        return true;
    }

    @Override
    public Point2D getHeadOffsetPoint() {
        return m_headOffsetPoint;
    }

    @Override
    public Point2D getTailOffsetPoint() {
        return m_tailOffsetPoint;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return getBoundingBoxAttributesComposed(Attribute.CONTROL_POINTS, Attribute.CORNER_RADIUS);
    }

    @Override
    public Shape<PolyMorphicLine> copyTo(Shape<PolyMorphicLine> other) {
        super.copyTo(other);
        ((PolyMorphicLine) other).m_headOffsetPoint = m_headOffsetPoint.copy();
        ((PolyMorphicLine) other).m_tailOffsetPoint = m_tailOffsetPoint.copy();
        ((PolyMorphicLine) other).m_breakDistance = m_breakDistance;
        ((PolyMorphicLine) other).cornerRadius = cornerRadius;

        return other;
    }

    @Override
    public PolyMorphicLine cloneLine() {
        PolyMorphicLine orthogonalPolyLine = new PolyMorphicLine(this.getControlPoints().copy(), cornerRadius);
        return (PolyMorphicLine) copyTo(orthogonalPolyLine);
    }
}