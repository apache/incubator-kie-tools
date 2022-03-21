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

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresNoneLayoutContainer;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGContainer;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;

public class SVGBasicShapeViewImpl
        extends WiresShapeView<SVGBasicShapeViewImpl>
        implements SVGBasicShapeView<SVGBasicShapeViewImpl> {

    private final String name;
    private final SVGPrimitiveShape svgPrimitive;
    private final SVGChildViewHandler childViewHandler;

    public SVGBasicShapeViewImpl(final String name,
                                 final SVGPrimitiveShape svgPrimitive,
                                 final double width,
                                 final double height) {
        super(setupDecorator(new MultiPath(),
                             0,
                             0,
                             width,
                             height),
              new WiresNoneLayoutContainer());
        this.name = name;
        this.svgPrimitive = svgPrimitive;
        this.childViewHandler = new SVGChildViewHandler(this);
        addChild(getShape());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Shape<?> getShape() {
        return svgPrimitive.get();
    }

    @Override
    public SVGBasicShapeViewImpl addChild(final SVGPrimitive<?> child) {
        childViewHandler.addChild(child);
        return this;
    }

    @Override
    public Collection<SVGPrimitive<?>> getChildren() {
        return childViewHandler.getChildren();
    }

    @Override
    public SVGPrimitive getPrimitive() {
        return svgPrimitive;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SVGBasicShapeViewImpl addSVGChild(final SVGContainer parent,
                                             final SVGBasicShapeView child) {
        childViewHandler.addSVGChild(parent,
                                     child);
        return this;
    }

    @Override
    public Collection<SVGBasicShapeView> getSVGChildren() {
        return childViewHandler.getSVGChildren();
    }

    @Override
    public void destroy() {
        svgPrimitive.destroy();
        childViewHandler.clear();
        super.destroy();
    }

    private static MultiPath setupDecorator(final MultiPath path,
                                            final double x,
                                            final double y,
                                            final double width,
                                            final double height) {
        return path.clear().rect(x,
                                 y,
                                 width,
                                 height)
                .setStrokeColor(ColorName.BLACK)
                .setStrokeAlpha(0)
                .setFillAlpha(0.001);
    }
}
