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

package org.kie.workbench.common.stunner.client.lienzo.wires;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.types.Point2D;

public abstract class DelegateWiresCompositeControl implements WiresCompositeControl {

    protected abstract WiresCompositeControl getDelegate();

    @Override
    public void useIndex(Supplier<WiresLayerIndex> index) {
        getDelegate().useIndex(index);
    }

    @Override
    public Context getContext() {
        return getDelegate().getContext();
    }

    @Override
    public boolean isAllowed() {
        return getDelegate().isAllowed();
    }

    @Override
    public WiresContainer getSharedParent() {
        return getDelegate().getSharedParent();
    }

    @Override
    public void execute() {
        getDelegate().execute();
    }

    @Override
    public boolean accept() {
        return getDelegate().accept();
    }

    @Override
    public boolean isOutOfBounds(double dx, double dy) {
        return getDelegate().isOutOfBounds(dx, dy);
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public void reset() {
        getDelegate().reset();
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        getDelegate().onMouseClick(event);
    }

    @Override
    public void onMouseDown(MouseEvent event) {
        getDelegate().onMouseDown(event);
    }

    @Override
    public void onMouseUp(MouseEvent event) {
        getDelegate().onMouseUp(event);
    }

    @Override
    public void onMoveStart(double x, double y) {
        getDelegate().onMoveStart(x, y);
    }

    @Override
    public boolean onMove(double dx, double dy) {
        return getDelegate().onMove(dx, dy);
    }

    @Override
    public void onMoveComplete() {
        getDelegate().onMoveComplete();
    }

    @Override
    public Point2D getAdjust() {
        return getDelegate().getAdjust();
    }

    @Override
    public void destroy() {
        getDelegate().destroy();
    }
}
