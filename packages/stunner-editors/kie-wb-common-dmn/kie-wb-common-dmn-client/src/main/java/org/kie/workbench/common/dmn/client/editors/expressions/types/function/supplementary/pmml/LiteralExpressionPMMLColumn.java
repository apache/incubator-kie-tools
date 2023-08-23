/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.gwt.user.client.ui.ListBox;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.ListBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.ListBoxDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNSimpleGridColumn;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;

public class LiteralExpressionPMMLColumn extends DMNSimpleGridColumn<LiteralExpressionPMMLGrid, String> implements HasSingletonDOMElementResource {

    private final ListBoxSingletonDOMElementFactory factory;

    public LiteralExpressionPMMLColumn(final ListBoxSingletonDOMElementFactory factory,
                                       final double width,
                                       final LiteralExpressionPMMLGrid gridWidget) {
        super(Collections.emptyList(),
              new LiteralExpressionPMMLColumnRenderer(),
              width,
              gridWidget);
        this.factory = Objects.requireNonNull(factory);
        setMovable(false);
        setResizable(false);
    }

    @Override
    public void edit(final GridCell<String> cell,
                     final GridBodyCellRenderContext context,
                     final Consumer<GridCellValue<String>> callback) {
        gridWidget.loadValues(doEdit(cell, context));
    }

    private Consumer<List<String>> doEdit(final GridCell<String> cell,
                                          final GridBodyCellRenderContext context) {
        return documentNames -> {
            if (!documentNames.isEmpty()) {
                factory.attachDomElement(context,
                                         e -> setupListBox(cell, e, documentNames),
                                         e -> e.setFocus(true));
            }
        };
    }

    private void setupListBox(final GridCell<String> cell,
                              final ListBoxDOMElement e,
                              final List<String> documentNames) {
        final ListBox widget = e.getWidget();
        widget.clear();
        documentNames.forEach(documentName -> widget.addItem(documentName,
                                                             StringUtils.createQuotedString(documentName)));
        e.setValue(assertCellValue(assertCell(cell).getValue()).getValue());
    }

    @Override
    protected GridCellValue<String> makeDefaultCellValue() {
        return new BaseGridCellValue<>("");
    }

    @Override
    public void flush() {
        factory.flush();
    }

    @Override
    public void destroyResources() {
        super.destroyResources();
        factory.destroyResources();
    }

    @Override
    public void setWidth(final double width) {
        super.setWidth(width);
        updateWidthOfPeers();
    }
}
