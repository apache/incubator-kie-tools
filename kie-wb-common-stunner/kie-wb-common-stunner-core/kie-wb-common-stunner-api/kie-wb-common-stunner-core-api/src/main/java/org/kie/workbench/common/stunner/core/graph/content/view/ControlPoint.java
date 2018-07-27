/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class ControlPoint {

    private Point2D location;
    private Integer index;

    public static ControlPoint build(final Point2D location) {
        return new ControlPoint(location, null);
    }

    public static ControlPoint build(final Point2D location,
                                     final int index) {
        return new ControlPoint(location, index);
    }

    public static ControlPoint build(final double x,
                                     final double y) {
        return new ControlPoint(Point2D.create(x, y), null);
    }

    public static ControlPoint build(final double x,
                                     final double y,
                                     final int index) {
        return new ControlPoint(Point2D.create(x, y), index);
    }

    public ControlPoint(final @MapsTo("location") Point2D location,
                        final @MapsTo("index") Integer index) {
        this.location = location;
        this.index = index;
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation(final Point2D location) {
        this.location = location;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(final Integer index) {
        this.index = index;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ControlPoint that = (ControlPoint) o;
        return Objects.equals(location, that.location)
                && Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hash(location),
                                         null != index ? Objects.hash(index) : 0);
    }

    @Override
    public String toString() {
        return "ControlPoint{" +
                "location=" + location +
                ", index=" + index +
                '}';
    }
}
