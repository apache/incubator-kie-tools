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

package com.ait.lienzo.client.core.shape.toolbox;

import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.Direction;

public class Positions {

    public static Point2D anchorFor(final BoundingBox boundingBox,
                                    final Direction direction) {
        Point2DArray cardinals = Geometry.getCardinals(boundingBox,
                                                       MagnetManager.EIGHT_CARDINALS);
        switch (direction) {
            case NORTH:
                return cardinals.get(1);
            case SOUTH:
                return cardinals.get(5);
            case EAST:
                return cardinals.get(3);
            case WEST:
                return cardinals.get(7);
            case NONE:
                return cardinals.get(0);
            case NORTH_EAST:
                return cardinals.get(2);
            case SOUTH_EAST:
                return cardinals.get(4);
            case SOUTH_WEST:
                return cardinals.get(6);
            case NORTH_WEST:
                return cardinals.get(8);
        }
        throw new IllegalArgumentException("invalid direction [" + direction + "]");
    }
}
