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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import org.gwtbootstrap3.client.ui.base.TextBoxBase;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.single.impl.BaseGridColumnSingletonDOMElementRenderer;

import static org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderUtilities.getEditableHeaderContent;

public class NameAndDataTypeDOMElementColumnRenderer<W extends TextBoxBase, E extends BaseDOMElement<String, W>> extends BaseGridColumnSingletonDOMElementRenderer<String, W, E> {

    public NameAndDataTypeDOMElementColumnRenderer(final BaseSingletonDOMElementFactory<String, W, E> factory) {
        super(factory);
    }

    @Override
    public Group renderHeaderContent(final List<GridColumn.HeaderMetaData> headerMetaData,
                                     final GridHeaderColumnRenderContext context,
                                     final int headerRowIndex,
                                     final double blockWidth,
                                     final double rowHeight) {
        return getEditableHeaderContent(() -> super.renderHeaderContent(headerMetaData,
                                                                        context,
                                                                        headerRowIndex,
                                                                        blockWidth,
                                                                        rowHeight),
                                        headerMetaData,
                                        context,
                                        headerRowIndex,
                                        blockWidth,
                                        rowHeight);
    }

    @Override
    public Group renderCell(final GridCell<String> cell,
                            final GridBodyCellRenderContext context) {
        if (cell == null || cell.getValue() == null) {
            return null;
        }

        return RendererUtils.getExpressionCellText(context, cell);
    }
}
