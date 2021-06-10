/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;

public interface IDirectionalMultiPointShape<T extends Shape<T> & IDirectionalMultiPointShape<T>> extends IOffsetMultiPointShape<T> {

    T setTailDirection(Direction direction);

    Direction getTailDirection();

    T setHeadDirection(Direction direction);

    Direction getHeadDirection();

    double getCorrectionOffset();

    T setCorrectionOffset(final double offset);

    boolean isPathPartListPrepared();

    Point2D adjustPoint(double x, double y, double deltaX, double deltaY);

    T cloneLine();
}
