/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public interface ExpressionGridCache extends CanvasControl<AbstractCanvas> {

    Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> getExpressionGrid(final String nodeUUID);

    void putExpressionGrid(final String nodeUUID,
                           final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> gridWidget);

    void removeExpressionGrid(final String nodeUUID);

    interface IsCacheable {

        boolean isCacheable();
    }
}
