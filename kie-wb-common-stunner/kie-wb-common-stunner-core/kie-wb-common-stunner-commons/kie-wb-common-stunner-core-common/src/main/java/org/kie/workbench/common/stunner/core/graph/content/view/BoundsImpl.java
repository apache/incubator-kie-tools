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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public final class BoundsImpl implements Bounds {

    private BoundImpl lr;
    private BoundImpl ul;

    public static BoundsImpl build() {
        return new BoundsImpl(new BoundImpl(0d, 0d),new BoundImpl(0d, 0d));
    }

    public static BoundsImpl build(final double x1,
                                   final double y1,
                                   final double x2,
                                   final double y2) {
        return new BoundsImpl(new BoundImpl(x1, y1),new BoundImpl(x2, y2));
    }

    public BoundsImpl(final @MapsTo("ul") BoundImpl ul,
                      final @MapsTo("lr") BoundImpl lr) {
        this.ul = ul;
        this.lr = lr;
    }

    @Override
    public BoundImpl getLowerRight() {
        return lr;
    }

    @Override
    public BoundImpl getUpperLeft() {
        return ul;
    }

    public void setLowerRight(final BoundImpl lr) {
        this.lr = lr;
    }

    public void setUpperLeft(final BoundImpl ul) {
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
        if (o instanceof BoundsImpl) {
            BoundsImpl other = (BoundsImpl) o;
            return ((null != ul)? ul.equals(other.ul) : null == other.ul) &&
                    ((null != lr)? lr.equals(other.lr) : null == other.lr);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes((null != ul)? ul.hashCode() : 0,
                                         (null != lr)? lr.hashCode() : 0);
    }
}
