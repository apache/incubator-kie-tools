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

import com.ait.lienzo.client.core.shape.Shape;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresScalableContainer;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.DecoratedShapeView;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ShapeViewSupportedEvents;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class SVGShapeViewImpl
        extends DecoratedShapeView<SVGShapeViewImpl>
        implements SVGShapeView<SVGShapeViewImpl> {

    private final String name;
    private final SVGChildViewHandler childViewHandler;
    private final SVGShapeStateHandler shapeStateHandler;

    public SVGShapeViewImpl(final String name,
                            final Shape<?> theShape,
                            final double width,
                            final double height,
                            final boolean resizable) {
        super(resizable ? ShapeViewSupportedEvents.ALL_DESKTOP_EVENT_TYPES : ShapeViewSupportedEvents.DESKTOP_NO_RESIZE_EVENT_TYPES,
              new WiresScalableContainer(),
              theShape,
              width,
              height);
        this.name = name;
        this.childViewHandler = new SVGChildViewHandler(getGroup(),
                                                        width,
                                                        height);
        this.shapeStateHandler = new SVGShapeStateHandler(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
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
    public boolean applyState(final ShapeState shapeState) {
        return shapeStateHandler.applyState(shapeState);
    }
}
