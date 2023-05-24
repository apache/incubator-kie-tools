/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.sw.client.shapes.icons;

import com.ait.lienzo.client.core.types.Point2D;

public interface IconPosition {

    Point2D RIGHT_TOP_CORNER = new Point2D(224, 6);
    Point2D LEFT_FROM_RIGHT_TOP_CORNER = new Point2D(202, 6);
    Point2D CENTER_TOP = new Point2D(180, 6);
    Point2D RIGHT_BOTTOM = new Point2D(224, 50);
    Point2D BOTTOM_FROM_RIGHT_TOP_CORNER = new Point2D(224, 28);
}
