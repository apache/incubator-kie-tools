/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import com.ait.lienzo.client.core.types.Transform;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;

/**
 * A {@link TransformMediator} that prevents transformations from being applied such that the "visible bounds" (i.e.
 * the visible portion of the Viewport) would extend beyond the boundary of a notional rectangular region. This
 * particular implementation uses a "dynamic bounds" from the underlying {@link GuidedDecisionTableModellerView}
 */

public class BoundaryTransformMediator implements TransformMediator {

    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

    private final GuidedDecisionTableModellerView view;

    public BoundaryTransformMediator(final GuidedDecisionTableModellerView view) {
        this.view = PortablePreconditions.checkNotNull("view",
                                                       view);
        updateBounds();
    }

    private void updateBounds() {
        final Bounds bounds = view.getBounds();
        this.minX = bounds.getX();
        this.minY = bounds.getY();
        this.maxX = this.minX + bounds.getWidth();
        this.maxY = this.minY + bounds.getHeight();
    }

    @Override
    public Transform adjust(final Transform transform,
                            final Bounds visibleBounds) {
        updateBounds();

        Transform newTransform = transform.copy();

        final double scaleX = transform.getScaleX();
        final double scaleY = transform.getScaleY();
        final double translateX = newTransform.getTranslateX();
        final double translateY = newTransform.getTranslateY();
        final double scaledTranslateX = translateX / scaleX;
        final double scaledTranslateY = translateY / scaleY;
        final double visibleBoundsWidth = visibleBounds.getWidth();
        final double visibleBoundsHeight = visibleBounds.getHeight();

        if (-scaledTranslateX < minX) {
            newTransform = newTransform.translate(-scaledTranslateX - minX,
                                                  0);
        }
        if (-scaledTranslateY < minY) {
            newTransform = newTransform.translate(0,
                                                  -scaledTranslateY - minY);
        }
        if (-scaledTranslateX + visibleBoundsWidth > maxX) {
            newTransform = newTransform.translate(-scaledTranslateX + visibleBoundsWidth - maxX,
                                                  0);
        }
        if (-scaledTranslateY + visibleBoundsHeight > maxY) {
            newTransform = newTransform.translate(0,
                                                  -scaledTranslateY + visibleBoundsHeight - maxY);
        }

        return newTransform;
    }
}
