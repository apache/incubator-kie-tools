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

package org.kie.workbench.common.dmn.client.editors.expressions;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;

public class ExpressionContainer extends BaseGridWidget {

    private static final String COLUMN_GROUP = "ExpressionContainer$Expression0";

    public ExpressionContainer(final DMNGridLayer gridLayer) {
        super(new DMNGridData(gridLayer),
              gridLayer,
              gridLayer,
              new ExpressionContainerRenderer());
        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        final GridColumn expressionColumn = new ExpressionEditorColumn(new BaseHeaderMetaData(COLUMN_GROUP),
                                                                       this);
        expressionColumn.setMovable(false);
        expressionColumn.setResizable(true);

        model.appendColumn(expressionColumn);
        model.appendRow(new DMNGridRow());

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer || gridColumn.equals(expressionColumn));
    }

    @Override
    public boolean onDragHandle(final INodeXYEvent event) {
        return false;
    }
}
