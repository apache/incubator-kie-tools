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

import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@JsType
public class Point2DConnection implements Connection {

    private final Point2D location;

    public static Point2DConnection at(final Point2D location) {
        return new Point2DConnection(location);
    }

    public Point2DConnection(Point2D location) {
        this.location = location;
    }

    @Override
    public Point2D getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "[Point2DConnection at {" + location + "}" + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point2DConnection) {
            Point2DConnection other = (Point2DConnection) o;
            return ((null != location && null != other.location) ?
                    (location.getX() == other.location.getX() && location.getY() == other.location.getY()) :
                    null == location && null == other.location);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes((null != location) ? Objects.hashCode(location) : 0);
    }
}
