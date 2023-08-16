/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.core.client.canvas;

import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

/**
 * The current transforms that are supported for canvas layers. See
 * <a>org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl</a>
 * See
 * <a>org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl</a>
 */
public interface Transform {

    /**
     * Returns the cartesian coordinates for the current translation, if any.
     */
    Point2D getTranslate();

    /**
     * Returns the values for the current scale on each cartesian axis, if any.
     */
    Point2D getScale();

    /**
     * Returns the cartesian coordinates resulting after applying this instance's
     * transforms.
     */
    Point2D transform(final double x, final double y);

    /**
     * Returns the cartesian coordinates resulting after applying this instance's
     * transform inverses.
     */
    Point2D inverse(final double x, final double y);
}
