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

import java.util.OptionalInt;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class MagnetConnection extends DiscreteConnection {

    public static final int MAGNET_CENTER = 0;

    private Point2D location;
    private Boolean auto;

    private MagnetConnection(final @MapsTo("location") Point2D location,
                             final @MapsTo("auto") Boolean auto) {
        checkNotNull("location",
                     location);
        checkNotNull("auto",
                     auto);
        this.location = location;
        this.auto = auto;
    }

    public MagnetConnection setLocation(final Point2D location) {
        this.location = location;
        // Once changing the location of the connection, the magnet index has to be re-calculated.
        this.setOptionalIndex(OptionalInt.empty());
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
            assert null != x && null != y;
            final MagnetConnection connection = new MagnetConnection(new Point2D(x,
                                                                                 y),
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

        public static MagnetConnection forElement(final Element<? extends View<?>> element) {
            final BoundsImpl bounds = (BoundsImpl) element.getContent().getBounds();
            final double width = bounds.getWidth();
            final double height = bounds.getHeight();
            final MagnetConnection center = new MagnetConnection(new Point2D(width / 2,
                                                                             height / 2),
                                                                 false);
            center.setIndex(MAGNET_CENTER);
            return center;
        }
    }
}
