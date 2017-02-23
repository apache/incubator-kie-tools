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

package org.kie.workbench.common.stunner.svg.client.shape.def;

import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;

/**
 * An SVG Shape Definition type that allows runtime updates
 * and can be composed by other SVG shape view instances.
 * <p>
 * Once the SVG shape view instance has been built, this type
 * provides the binding between the definition and the view instance
 * for different attributes. The size for the view can be
 * changed at runtime as well.
 * @param <W> The bean type.
 */
public interface SVGMutableShapeDef<W, F> extends SVGShapeDef<W, F>,
                                                  MutableShapeDef<W> {

    /**
     * The width of the SVG view's bounding box.
     * The way shape views are being updated to achieve new width
     * is up to each implementations.
     * @param element The model instance.
     * @return The shape's width.
     */
    double getWidth(final W element);

    /**
     * The height of the SVG view's bounding box.
     * The way shape views are being updated to achieve new height
     * is up to each implementations.
     * @param element The model instance.
     * @return The shape's height.
     */
    double getHeight(final W element);

    /**
     * If the SVGShape is composed by some
     * SVGShapeView instances, this method allows
     * to display or make not visible each of
     * the inner views as from the <code>element</code>
     * instance's state at runtime.
     * @param viewName The name of the inner SVGSHapeVie.
     * @param element The definition instance.
     * @return Whether the <code>viewName</code> inner SVGShapeView, which
     * the shape is composed by, has to be displayed.
     */
    boolean isSVGViewVisible(final String viewName,
                             final W element);
}