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

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class SVGShapeStateHandler {

    private final SVGShapeView<?> view;
    private Map<ShapeState, SVGShapeStateHolderImpl> stateHolderMap = new HashMap<>(4);

    public SVGShapeStateHandler(final SVGShapeView<?> view) {
        this.view = view;
    }

    public SVGShapeStateHandler registerStateHolder(final ShapeState state,
                                                    final SVGShapeStateHolderImpl holder) {
        stateHolderMap.put(state,
                           holder);
        return this;
    }

    public boolean applyState(final ShapeState state) {
        final SVGShapeStateHolderImpl holder = stateHolderMap.get(state);
        if (null != holder) {
            if (holder.hasAlpha()) {
                view.setAlpha(holder.getAlpha());
            }
            if (holder.hasFillColor()) {
                view.setFillColor(holder.getFillColor());
            }
            if (holder.hasFillAlpha()) {
                view.setFillAlpha(holder.getFillAlpha());
            }
            if (holder.hasStrokeColor()) {
                view.setStrokeColor(holder.getStrokeColor());
            }
            if (holder.hasStrokeAlpha()) {
                view.setStrokeAlpha(holder.getStrokeAlpha());
            }
            if (holder.hasStrokeWidth()) {
                view.setStrokeWidth(holder.getStrokeWidth());
            }
            return true;
        }
        return false;
    }
}
