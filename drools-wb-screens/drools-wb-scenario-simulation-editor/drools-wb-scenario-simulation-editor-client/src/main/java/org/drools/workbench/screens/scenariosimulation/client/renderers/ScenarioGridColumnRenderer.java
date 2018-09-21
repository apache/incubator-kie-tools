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
package org.drools.workbench.screens.scenariosimulation.client.renderers;

import java.util.List;
import java.util.function.BiFunction;

import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.ColumnRenderingStrategyFlattened;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.StringColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class ScenarioGridColumnRenderer extends StringColumnRenderer {

    @Override
    public List<GridRenderer.RendererCommand> renderColumn(final GridColumn<?> column,
                                                           final GridBodyColumnRenderContext context,
                                                           final BaseGridRendererHelper rendererHelper,
                                                           final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                                           final BiFunction<Boolean, GridColumn<?>, Boolean> columnRenderingConstraint) {
        return ColumnRenderingStrategyFlattened.render(column,
                                                       context,
                                                       rendererHelper,
                                                       renderingInformation,
                                                       columnRenderingConstraint);
    }

}