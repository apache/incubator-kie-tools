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

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;

public class SVGPrimitive<T extends IPrimitive<?>> {

    private final T primitive;
    private final boolean scalable;
    private final LayoutContainer.Layout layout;

    public SVGPrimitive(T primitive,
                        boolean scalable,
                        LayoutContainer.Layout layout) {
        this.primitive = primitive;
        this.scalable = scalable;
        this.layout = layout;
    }

    public boolean isScalable() {
        return scalable;
    }

    public LayoutContainer.Layout getLayout() {
        return layout;
    }

    public String getId() {
        return get().getID();
    }

    public T get() {
        return primitive;
    }

    public void destroy() {
        primitive.removeFromParent();
    }
}
