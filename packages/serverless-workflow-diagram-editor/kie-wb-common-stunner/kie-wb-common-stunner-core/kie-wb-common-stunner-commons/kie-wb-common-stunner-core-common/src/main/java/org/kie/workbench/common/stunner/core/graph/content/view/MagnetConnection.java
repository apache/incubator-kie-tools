/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.content.view;

import java.util.Objects;
import java.util.function.Function;

import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@JsType
public class MagnetConnection extends DiscreteConnection {

    public static final int MAGNET_CENTER = 0;
    public static final int MAGNET_TOP = 1;
    public static final int MAGNET_RIGHT = 2;
    public static final int MAGNET_BOTTOM = 3;
    public static final int MAGNET_LEFT = 4;

    private Point2D location;
    private Boolean auto;

    private MagnetConnection(Point2D location,
                             Boolean auto) {
        Objects.requireNonNull(auto, "Parameter named 'auto' should be not null!");
        this.location = location;
        this.auto = auto;
    }

    private MagnetConnection(int index) {
        setIndex(index);
    }

    public MagnetConnection setLocation(final Point2D location) {
        this.location = location;
        return this;
    }

    public MagnetConnection setAuto(final boolean auto) {
        this.auto = auto;
        return this;
    }

    @Override
    public Point2D getLocation() {
        return location;
    }

    public boolean isAuto() {
        return auto;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MagnetConnection) {
            MagnetConnection other = (MagnetConnection) o;
            return ((null != location && null != other.location) ?
                    (location.getX() == other.location.getX() && location.getY() == other.location.getY()) :
                    null == location && null == other.location) &&
                    ((null != auto) ? auto.equals(other.auto) : null == other.auto);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes((null != location) ? HashUtil.combineHashCodes(1,
                                                                                        Double.hashCode(location.getX()),
                                                                                        Double.hashCode(location.getY()))
                                                 : 0,
                                         (null != auto) ? auto.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "[MagnetConnection at {" + location + "}" + "]";
    }

    @NonPortable
    public static class Builder {

        private enum RelativePosition {
            ABOVE,
            BELOW,
            LEFT,
            RIGHT,
            CENTRE
        }

        private Double x;
        private Double y;

        private boolean auto = false;
        private Integer magnet;

        public Builder atX(final double x) {
            this.x = x;
            return this;
        }

        public Builder atY(final double y) {
            this.y = y;
            return this;
        }

        public Builder auto() {
            return auto(true);
        }

        public Builder auto(final boolean auto) {
            this.auto = auto;
            return this;
        }

        public Builder magnet(final int index) {
            this.magnet = index;
            return this;
        }

        public MagnetConnection build() {
            final Point2D p = null != x && null != y ?
                    new Point2D(x,
                                y) :
                    null;
            final MagnetConnection connection = new MagnetConnection(p,
                                                                     auto);
            if (null != magnet) {
                connection.setIndex(magnet);
            }
            return connection;
        }

        public static MagnetConnection at(final double x,
                                          final double y) {
            return new MagnetConnection(new Point2D(x,
                                                    y),
                                        false);
        }

        public static MagnetConnection forTarget(final Element<? extends View<?>> source,
                                                 final Element<? extends View<?>> target) {
            final RelativePosition relativePosition = getTargetPositionRelativeToSource(source, target);
            switch (relativePosition) {
                case ABOVE:
                    return atTop(source);
                case BELOW:
                    return atBottom(source);
                case LEFT:
                    return atLeft(source);
                case RIGHT:
                    return atRight(source);
                default:
                    return atCenter(source);
            }
        }

        //        | ABOVE |
        // -------+-------+-------
        // LEFT   |       + RIGHT
        // -------+-------+-------
        //        | BELOW +
        private static RelativePosition getTargetPositionRelativeToSource(final Element<? extends View<?>> source,
                                                                          final Element<? extends View<?>> target) {
            final Point2D sourcePosition = GraphUtils.getPosition(source.getContent());
            final Point2D targetPosition = (Objects.nonNull(target) ? GraphUtils.getPosition(target.getContent()) : sourcePosition);
            final Bounds sourceBounds = source.getContent().getBounds();
            final Bounds targetBounds = target.getContent().getBounds();

            //Simple non-overlapping cases...
            if (targetPosition.getY() + targetBounds.getHeight() < sourcePosition.getY()) {
                return RelativePosition.ABOVE;
            }
            if (targetPosition.getY() > sourcePosition.getY() + sourceBounds.getHeight()) {
                return RelativePosition.BELOW;
            }
            if (targetPosition.getX() + targetBounds.getWidth() < sourcePosition.getX()) {
                return RelativePosition.LEFT;
            }
            if (targetPosition.getX() > sourcePosition.getX() + sourceBounds.getWidth()) {
                return RelativePosition.RIGHT;
            }

            return RelativePosition.CENTRE;
        }

        public static MagnetConnection atCenter(final Element<? extends View<?>> element) {
            return atLocation(element,
                              MAGNET_CENTER,
                              bounds -> bounds.getWidth() > 0 && bounds.getHeight() > 0 ?
                                      new Point2D(bounds.getWidth() / 2,
                                                  bounds.getHeight() / 2) :
                                      null,
                              false);
        }

        public static MagnetConnection atTop(final Element<? extends View<?>> element) {
            return atLocation(element,
                              MAGNET_TOP,
                              bounds -> bounds.getWidth() > 0 ?
                                      new Point2D(bounds.getWidth() / 2,
                                                  0) :
                                      null);
        }

        public static MagnetConnection atBottom(final Element<? extends View<?>> element) {
            return atLocation(element,
                              MAGNET_BOTTOM,
                              bounds -> bounds.getWidth() > 0 && bounds.getHeight() > 0 ?
                                      new Point2D(bounds.getWidth() / 2,
                                                  bounds.getHeight()) :
                                      null);
        }

        public static MagnetConnection atRight(final Element<? extends View<?>> element) {
            return atLocation(element,
                              MAGNET_RIGHT,
                              bounds -> bounds.getWidth() > 0 && bounds.getHeight() > 0 ?
                                      new Point2D(bounds.getWidth(),
                                                  bounds.getHeight() / 2) :
                                      null);
        }

        public static MagnetConnection atLeft(final Element<? extends View<?>> element) {
            return atLocation(element,
                              MAGNET_LEFT,
                              bounds -> bounds.getWidth() > 0 && bounds.getHeight() > 0 ?
                                      new Point2D(0,
                                                  bounds.getHeight() / 2) :
                                      null);
        }

        private static MagnetConnection atLocation(final Element<? extends View<?>> element,
                                                   final int magnet,
                                                   final Function<Bounds, Point2D> atResolver) {
            return atLocation(element, magnet, atResolver, true);
        }

        private static MagnetConnection atLocation(final Element<? extends View<?>> element,
                                                   final int magnet,
                                                   final Function<Bounds, Point2D> atResolver,
                                                   final boolean isAuto) {
            final Bounds bounds = element.getContent().getBounds();
            final Point2D at = atResolver.apply(bounds);
            return new MagnetConnection(magnet).setLocation(at).setAuto(isAuto);
        }
    }
}

