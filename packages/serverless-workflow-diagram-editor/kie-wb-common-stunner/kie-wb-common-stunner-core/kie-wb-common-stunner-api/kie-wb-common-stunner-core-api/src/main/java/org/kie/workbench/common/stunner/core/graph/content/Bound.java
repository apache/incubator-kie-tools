/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.content;

import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@JsType
public class Bound {

    public static Bound create(final double x,
                               final double y) {
        return new Bound(x, y);
    }

    private Double x;
    private Double y;

    public Bound(Double x,
                 Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public boolean hasX() {
        return null != x;
    }

    public Double getY() {
        return y;
    }

    public boolean hasY() {
        return null != y;
    }

    public void setX(final Double x) {
        this.x = x;
    }

    public void setY(final Double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Bound) {
            Bound other = (Bound) o;
            return ((null != x) ? x.equals(other.x) : null == other.x) &&
                    ((null != y) ? y.equals(other.y) : null == other.y);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes((null != x) ? x.hashCode() : 0,
                                         (null != y) ? y.hashCode() : 0);
    }
}
