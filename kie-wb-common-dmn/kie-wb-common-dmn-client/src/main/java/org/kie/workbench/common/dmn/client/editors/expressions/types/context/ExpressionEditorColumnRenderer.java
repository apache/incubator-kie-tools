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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.Group;
import com.google.gwt.core.client.GWT;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.BaseGridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridWidgetRegistry;

public class ExpressionEditorColumnRenderer extends BaseGridColumnRenderer<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> {

    private final GridWidgetRegistry registry;

    public ExpressionEditorColumnRenderer(final GridWidgetRegistry registry) {
        this.registry = PortablePreconditions.checkNotNull("registry", registry);
    }

    @Override
    public Group renderCell(final GridCell<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> cell,
                            final GridBodyCellRenderContext context) {
        if (cell == null || cell.getValue() == null) {
            return null;
        }

        final Group g = GWT.create(Group.class);
        if (cell.getValue() != null && cell.getValue() instanceof ExpressionCellValue) {
            final ExpressionCellValue ecv = (ExpressionCellValue) cell.getValue();
            ecv.getValue().ifPresent(editor -> {
                g.add(editor.setX(editor.getPadding()).setY(editor.getPadding()));
                registry.register(editor);
            });
        }

        return g;
    }
}
