/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ait.lienzo.client.widget.panel.mediators;

import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;

/**
 * Defines a mediator that can mediate {@link com.ait.lienzo.client.core.types.Transform} applied to a {@link
 * com.ait.lienzo.client.core.shape.Viewport}.
 */
public interface TransformMediator
{
    /**
     * Adjusts a {@link com.ait.lienzo.client.core.types.Transform} to mediate transformations.
     *
     * @param transform     The proposed Transform to be applied to the Viewport.
     * @param visibleBounds {@link com.ait.lienzo.client.widget.panel.Bounds} of the visible portion of the Viewport.
     * @return A new Transform.
     */
    Transform adjust(final Transform transform,
                     final Bounds visibleBounds);
}
