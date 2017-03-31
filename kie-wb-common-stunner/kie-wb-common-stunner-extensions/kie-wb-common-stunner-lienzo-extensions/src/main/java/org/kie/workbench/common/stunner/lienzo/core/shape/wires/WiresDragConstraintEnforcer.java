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

package org.kie.workbench.common.stunner.lienzo.core.shape.wires;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresUtils;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;

public class WiresDragConstraintEnforcer implements DragConstraintEnforcer {

    public static WiresDragConstraintEnforcer enforce(final WiresContainer container,
                                                      final DragBounds dragBounds) {
        final Optional<DragConstraintEnforcer> delegate =
                null != container.getGroup().getDragConstraints() ?
                        Optional.of(container.getGroup().getDragConstraints()) :
                        Optional.empty();
        final WiresDragConstraintEnforcer enforcer = new WiresDragConstraintEnforcer(container,
                                                                                     dragBounds,
                                                                                     delegate);
        container.getGroup().setDragConstraints(enforcer);
        return enforcer;
    }

    private final WiresContainer container;
    private final Optional<DragConstraintEnforcer> delegate;
    private DragBounds dragBounds;
    private double minX = 0;
    private double minY = 0;
    private double maxX = 0;
    private double maxY = 0;

    private WiresDragConstraintEnforcer(final WiresContainer container,
                                        final DragBounds dragBounds,
                                        final Optional<DragConstraintEnforcer> delegate) {
        this.container = container;
        this.delegate = delegate;
        setDragBounds(dragBounds);
    }

    public void setDragBounds(final DragBounds dragBounds) {
        this.dragBounds = dragBounds;
    }

    public void remove() {
        this.container.getGroup().setDragConstraints(delegate.orElse(null));
        this.dragBounds = null;
    }

    @Override
    public void startDrag(final DragContext dragContext) {
        checkBounds();
        final Group group = container.getGroup();
        final BoundingBox bb = group.getBoundingBox();
        final Point2D location = WiresUtils.getLocation(group);
        final double dragStartX = location.getX();
        final double dragStartY = location.getY();
        minX = dragBounds.getX1() - dragStartX;
        minY = dragBounds.getY1() - dragStartY;
        maxX = dragBounds.getX2() - bb.getWidth() - dragStartX;
        maxY = dragBounds.getY2() - bb.getHeight() - dragStartY;
        if (delegate.isPresent()) {
            delegate.get().startDrag(dragContext);
        }
    }

    @Override
    public boolean adjust(final com.ait.lienzo.client.core.types.Point2D dxy) {
        double dxxx = dxy.getX();
        double dxxy = dxy.getY();
        boolean adjust = false;
        if (dxxx < minX) {
            dxy.setX(minX);
            adjust = true;
        }
        if (dxxy < minY) {
            dxy.setY(minY);
            adjust = true;
        }
        if (dxxx > maxX) {
            dxy.setX(maxX);
            adjust = true;
        }
        if (dxxy > maxY) {
            dxy.setY(maxY);
            adjust = true;
        }
        if (!adjust && delegate.isPresent()) {
            return delegate.get().adjust(dxy);
        }
        return adjust;
    }

    public Optional<DragConstraintEnforcer> getDelegate() {
        return delegate;
    }

    private void checkBounds() {
        if (null == dragBounds) {
            throw new IllegalStateException("No drag bounds specified " +
                                                    "or drag enforcer instance not valid.");
        }
    }
}
