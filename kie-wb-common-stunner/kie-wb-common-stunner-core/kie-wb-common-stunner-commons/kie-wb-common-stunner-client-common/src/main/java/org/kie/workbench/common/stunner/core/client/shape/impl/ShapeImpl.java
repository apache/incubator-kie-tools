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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

/**
 * A default Shape implementation.
 * This shape view's attributes are not being updated as with any model updates.
 * @param <V> The Shape View type.
 */
public class ShapeImpl<V extends ShapeView>
        implements
        Shape<V>,
        Lifecycle {

    private final V view;
    private final ShapeStateHelper<V, Shape<V>> shapeStateHelper;
    private String uuid;

    public ShapeImpl(final V view) {
        this.view = view;
        this.shapeStateHelper = new ShapeStateHelper<V, Shape<V>>(this);
    }

    public ShapeImpl(final V view,
                     final ShapeStateHelper<V, Shape<V>> shapeStateHelper) {
        this.view = view;
        this.shapeStateHelper = shapeStateHelper;
        this.shapeStateHelper.forShape(this);
    }

    public void setUUID(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public void beforeDraw() {
    }

    @Override
    public void afterDraw() {
        if (view instanceof HasTitle) {
            ((HasTitle) view).moveTitleToTop();
        }
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        shapeStateHelper
                .applyState(shapeState);
    }

    public ShapeStateHelper<V, Shape<V>> getShapeStateHelper() {
        return shapeStateHelper;
    }

    @Override
    public V getShapeView() {
        return view;
    }

    @Override
    public void destroy() {
        view.destroy();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShapeImpl)) {
            return false;
        }
        ShapeImpl that = (ShapeImpl) o;
        return uuid != null && uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid == null ? 0 : ~~uuid.hashCode();
    }
}
