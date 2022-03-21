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

package org.kie.workbench.common.stunner.svg.client.shape.view;

import java.util.Collection;

import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public interface SVGContainerShapeView<T extends SVGContainerShapeView>
        extends
        ShapeView<T> {

    T addChild(SVGPrimitive<?> child);

    Collection<SVGPrimitive<?>> getChildren();

    /**
     * Adds an SVG shape view instance as a child for this view.
     * @param parent The target parent element used for adding the child view's container.
     * @param child The SVGShapeView instance.
     */
    T addSVGChild(SVGContainer parent,
                  SVGBasicShapeView child);

    /**
     * Returns the SVGShapeView's children for this view.
     */
    Collection<SVGBasicShapeView> getSVGChildren();

    /**
     * Returns the container instance for this view.
     * This method is used to provide the children
     * for other composite SVG views.
     */
    IContainer<?, IPrimitive<?>> getContainer();

    /**
     * Refresh children layout for the shape.
     */
    void refresh();
}
