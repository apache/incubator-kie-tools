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

package org.kie.workbench.common.stunner.svg.gen.model.impl;

import java.util.Optional;

import org.kie.workbench.common.stunner.svg.gen.model.ShapeDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.StyleDefinition;

public abstract class AbstractShapeDefinition<V>
        extends AbstractPrimitiveDefinition<V>
        implements ShapeDefinition<V> {

    private Optional<ShapeStateDefinition> stateDefinition;
    private StyleDefinition styleDefinition;

    protected AbstractShapeDefinition(final String id) {
        super(id);
        this.stateDefinition = Optional.empty();
    }

    @Override
    public Optional<ShapeStateDefinition> getStateDefinition() {
        return stateDefinition;
    }

    public void setStateDefinition(final Optional<ShapeStateDefinition> stateDefinition) {
        this.stateDefinition = stateDefinition;
    }

    @Override
    public StyleDefinition getStyleDefinition() {
        return styleDefinition;
    }

    public void setStyleDefinition(final StyleDefinition styleDefinition) {
        this.styleDefinition = styleDefinition;
    }
}
