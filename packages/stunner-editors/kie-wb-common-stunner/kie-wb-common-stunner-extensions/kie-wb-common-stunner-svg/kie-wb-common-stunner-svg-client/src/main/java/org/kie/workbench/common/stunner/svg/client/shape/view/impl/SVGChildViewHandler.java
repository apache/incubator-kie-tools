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

package org.kie.workbench.common.stunner.svg.client.shape.view.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.DecoratedShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGContainer;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;

public class SVGChildViewHandler {

    private final WiresShapeView<?> view;
    private final List<SVGPrimitive<?>> primChildren = new LinkedList<>();
    private final List<SVGBasicShapeView> svgChildren = new LinkedList<>();

    public SVGChildViewHandler(final WiresShapeView<?> view) {
        this.view = view;
    }

    @SuppressWarnings("unchecked")
    public void addChild(final SVGPrimitive<?> child) {
        final IPrimitive<?> primitive = child.get();
        primChildren.add(child);
        if (child.isScalable()
                && view instanceof DecoratedShapeView) {
            ((DecoratedShapeView) view).addScalableChild(primitive);
        } else if (null != child.getLayout()) {
            view.addChild(primitive, child.getLayout());
        } else {
            view.addChild(primitive);
        }
    }

    @SuppressWarnings("unchecked")
    public void addSVGChild(final SVGContainer parent,
                            final SVGBasicShapeView child) {
        if (!hasSvgChild(child)) {
            svgChildren.add(child);
            parent.addPrimitive((IPrimitive<?>) child.getContainer());
        }
    }

    public void clear() {
        primChildren.forEach(SVGPrimitive::destroy);
        primChildren.clear();
        svgChildren.forEach(ShapeView::destroy);
        svgChildren.clear();
    }

    public Collection<SVGPrimitive<?>> getChildren() {
        return primChildren;
    }

    public Collection<SVGBasicShapeView> getSVGChildren() {
        return svgChildren;
    }

    private IContainer<?, IPrimitive<?>> getContainer() {
        return view.getGroup();
    }

    private boolean hasSvgChild(final SVGBasicShapeView child) {
        final String name = child.getName();
        return svgChildren.stream().anyMatch(c -> name.equals(c.getName()));
    }
}
