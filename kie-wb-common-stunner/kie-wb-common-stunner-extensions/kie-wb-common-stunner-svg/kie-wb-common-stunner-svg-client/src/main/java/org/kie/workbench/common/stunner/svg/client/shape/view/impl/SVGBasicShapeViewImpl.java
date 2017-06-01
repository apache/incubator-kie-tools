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

package org.kie.workbench.common.stunner.svg.client.shape.view.impl;

import java.util.Collection;
import java.util.logging.Logger;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresNoneLayoutContainer;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;

public class SVGBasicShapeViewImpl
        extends WiresShapeView<SVGBasicShapeViewImpl>
        implements SVGBasicShapeView<SVGBasicShapeViewImpl> {

    private static Logger LOGGER = Logger.getLogger(SVGBasicShapeViewImpl.class.getName());

    private final String name;
    private final Shape<?> theShape;
    private final SVGChildViewHandler childViewHandler;

    public SVGBasicShapeViewImpl(final String name,
                                 final Shape<?> theShape,
                                 final double width,
                                 final double height) {
        super(setupDecorator(new MultiPath(),
                             0,
                             0,
                             width,
                             height),
              new WiresNoneLayoutContainer());
        this.name = name;
        this.theShape = theShape;
        this.childViewHandler = new SVGChildViewHandler(getGroup(),
                                                        width,
                                                        height);
        addChild(theShape);
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public Shape<?> getShape() {
        return theShape;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SVGBasicShapeView addSVGChild(final String parent,
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
        super.destroy();
        childViewHandler.clear();
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