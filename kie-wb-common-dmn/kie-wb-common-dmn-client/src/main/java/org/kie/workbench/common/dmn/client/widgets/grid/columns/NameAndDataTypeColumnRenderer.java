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
import com.ait.lienzo.client.core.shape.Text;
import com.google.gwt.core.client.GWT;
import org.gwtbootstrap3.client.ui.TextArea;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.single.impl.BaseGridColumnSingletonDOMElementRenderer;

public class NameAndDataTypeColumnRenderer extends BaseGridColumnSingletonDOMElementRenderer<String, TextArea, TextAreaDOMElement> {

    static final String FONT_STYLE_TYPE_REF = "italic";

    static final double SPACING = 8.0;

    public NameAndDataTypeColumnRenderer(final TextAreaSingletonDOMElementFactory factory) {
        super(factory);
    }

    @Override
    protected Group renderHeaderContent(final GridHeaderColumnRenderContext context,
                                        final List<GridColumn.HeaderMetaData> headerMetaData,
                                        final int headerRowIndex,
                                        final double blockWidth,
                                        final double rowHeight) {
        final Group headerGroup = GWT.create(Group.class);

        if (headerRowIndex >= headerMetaData.size()) {
            return headerGroup;
        }

        final GridColumn.HeaderMetaData headerRowMetaData = headerMetaData.get(headerRowIndex);
        final String title = headerRowMetaData.getTitle();

        if (headerRowMetaData instanceof NameAndDataTypeHeaderMetaData) {
            final NameAndDataTypeHeaderMetaData nadHeaderMetaData = (NameAndDataTypeHeaderMetaData) headerRowMetaData;
            final Text name = context.getRenderer().getTheme().getHeaderText();
            name.setText(title);
            name.setListening(false);
            name.setX(blockWidth / 2);
            name.setY(rowHeight / 2 - SPACING);

            final Text typeRef = context.getRenderer().getTheme().getHeaderText();
            typeRef.setFontStyle(FONT_STYLE_TYPE_REF);
            typeRef.setFontSize(BaseExpressionGridTheme.FONT_SIZE - 2.0);
            typeRef.setText("(" + nadHeaderMetaData.getTypeRef().toString() + ")");
            typeRef.setListening(false);
            typeRef.setX(blockWidth / 2);
            typeRef.setY(rowHeight / 2 + SPACING);

            headerGroup.add(name);
            headerGroup.add(typeRef);

            return headerGroup;
        }

        return super.renderHeaderContent(context,
                                         headerMetaData,
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
