/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.content.view;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class MagnetImpl implements Magnet {

    private Point2D location;
    private MagnetType magnetType;

    private MagnetImpl(final @MapsTo("location") Point2D location,
                       final @MapsTo("magnetType") MagnetType magnetType) {
        this.location = location;
        this.magnetType = magnetType;
    }

    public void setLocation(final Point2D location) {
        this.location = location;
    }

    public void setMagnetType(final MagnetType magnetType) {
        this.magnetType = magnetType;
    }

    @Override
    public Point2D getLocation() {
        return location;
    }

    @Override
    public MagnetType getMagnetType() {
        return magnetType;
    }

    @Override
    public String toString() {
        return "[MagnetImpl at {" + location + "}, magnetType = " + magnetType + "]";
    }

    @NonPortable
    public static class Builder {

        public static MagnetImpl build(final double x,
                                       final double y) {
            return new MagnetImpl(new Point2D(x,
                                              y),
                                  null);
        }

        public static MagnetImpl build(final MagnetType magnetType) {
            return new MagnetImpl(null,
                                  magnetType);
        }
    }
}
