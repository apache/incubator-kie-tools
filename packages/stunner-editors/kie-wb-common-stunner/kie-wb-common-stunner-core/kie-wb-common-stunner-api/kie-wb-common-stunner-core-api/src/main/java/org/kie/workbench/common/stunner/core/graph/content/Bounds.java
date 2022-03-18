/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class Bounds {

    public static Bounds create() {
        return create(0d, 0d, 0d, 0d);
    }

    public static Bounds createEmpty() {
        return create(null, null);
    }

    public static Bounds createMinBounds(final double x1,
                                         final double y1) {
        return create(Bound.create(x1, y1),
                      null);
    }

    public static Bounds create(final double x1,
                                final double y1,
                                final double x2,
                                final double y2) {
        return create(Bound.create(x1, y1),
                      Bound.create(x2, y2));
    }

    public static Bounds create(final Bound xy0,
                                final Bound xy1) {
        return new Bounds(xy0,
                          xy1);
    }

    private Bound lr;
    private Bound ul;

    public Bounds(final @MapsTo("ul") Bound ul,
                  final @MapsTo("lr") Bound lr) {
        this.ul = ul;
        this.lr = lr;
    }

    public Bound getLowerRight() {
        return lr;
    }

    public boolean hasLowerRight() {
        return null != lr;
    }

    public Bound getUpperLeft() {
        return ul;
    }

    public boolean hasUpperLeft() {
        return null != ul;
    }

    public void setLowerRight(final Bound lr) {
        this.lr = lr;
    }

    public void setUpperLeft(final Bound ul) {
        this.ul = ul;
    }

    public double getX() {
        return getUpperLeft().getX();
    }

    public double getY() {
        return getUpperLeft().getY();
    }

    public double getWidth() {
        return getLowerRight().getX() - getUpperLeft().getX();
    }

    public double getHeight() {
        return getLowerRight().getY() - getUpperLeft().getY();
    }

    @Override
    public String toString() {
        return "UL=" + ul + " | LR=" + lr;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Bounds) {
            Bounds other = (Bounds) o;
            return Objects.equals(ul, other.ul) && Objects.equals(lr, other.lr);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(ul),
                                         Objects.hashCode(lr));
    }
}
