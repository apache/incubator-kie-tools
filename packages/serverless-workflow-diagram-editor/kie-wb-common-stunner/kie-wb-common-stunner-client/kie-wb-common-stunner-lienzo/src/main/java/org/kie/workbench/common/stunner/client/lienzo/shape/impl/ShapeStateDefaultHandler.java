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


package org.kie.workbench.common.stunner.client.lienzo.shape.impl;

import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.client.lienzo.util.ShapeViewUserDataEncoder;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributesFactory;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.HasShadow;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.NONE;
import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.SELECTED;

public class ShapeStateDefaultHandler
        implements ShapeStateHandler {

    public enum ShapeType {
        BACKGROUND,
        BORDER,
        CONTAINER
    }

    private static final Shadow SHADOW_SELECTED = new Shadow(ColorName.BLACK.getColor().setA(0.40), 5, 2, 2);

    protected final ShapeStateAttributeHandler<ShapeView> handler;
    public ShapeView backgroundShape;
    public Supplier<ShapeView> borderShapeSupplier;
    private final ShapeViewUserDataEncoder shapeViewDataEncoder;

    public ShapeStateDefaultHandler() {
        this(new ShapeStateAttributeHandler<>());
    }

    ShapeStateDefaultHandler(final ShapeStateAttributeHandler<ShapeView> handler) {
        this.handler = handler;
        this.shapeViewDataEncoder = ShapeViewUserDataEncoder.get();
        handler.useAttributes(ShapeStateAttributesFactory::buildStateAttributes);
    }

    public ShapeStateDefaultHandler setBorderShape(final Supplier<ShapeView> shapeSupplier) {
        handler.setView(shapeSupplier);
        borderShapeSupplier = shapeSupplier;

        /**
         * TODO: need to fix when resolution of JBPM-7681 is available
         * @see <a href="https://issues.jboss.org/browse/JBPM-7681">JBPM-7681</a>
         */
        shapeViewDataEncoder.applyShapeViewType(shapeSupplier, ShapeType.BORDER);
        return this;
    }

    public ShapeStateDefaultHandler setBackgroundShape(final ShapeView shapeSupplier) {
        backgroundShape = shapeSupplier;

        /**
         * TODO: need to fix when resolution of JBPM-7681 is available
         * @see <a href="https://issues.jboss.org/browse/JBPM-7681">JBPM-7681</a>
         */
        shapeViewDataEncoder.applyShapeViewType(shapeSupplier, ShapeType.BACKGROUND);
        return this;
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        if (getShapeState() == NONE) {
            shapeAttributesChanged();
        }
        handler.applyState(shapeState);
        getShadowShape().ifPresent(this::updateShadow);
    }

    @Override
    public ShapeStateHandler shapeAttributesChanged() {
        handler.shapeAttributesChanged();
        return this;
    }

    @Override
    public ShapeState reset() {
        getShadowShape().ifPresent(HasShadow::removeShadow);
        return handler.reset();
    }

    @Override
    public ShapeState getShapeState() {
        return handler.getShapeState();
    }

    protected void updateShadow(final HasShadow shape) {
        if (isStateSelected()) {
            shape.setShadow(SHADOW_SELECTED.getColor(),
                            SHADOW_SELECTED.getBlur(),
                            SHADOW_SELECTED.getOffset().getX(),
                            SHADOW_SELECTED.getOffset().getY());
        } else {
            shape.removeShadow();
        }
    }

    private Optional<HasShadow> getShadowShape() {
        final ShapeView<?> candidate = null != backgroundShape ? backgroundShape : handler.getShapeView();
        if (candidate instanceof HasShadow) {
            return Optional.of((HasShadow) candidate);
        }
        return Optional.empty();
    }

    private boolean isStateSelected() {
        return SELECTED.equals(getShapeState());
    }
}