/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox;

import com.google.gwt.dom.client.Style;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.BaseDTDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiCell;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * A DOMElement that can contain multiple values; e.g. a multi-select ListBox.
 */
public abstract class MultiValueDOMElement<T, W extends ListBox> extends BaseDTDOMElement<T, W> {

    public MultiValueDOMElement(final W widget,
                                final GridLayer gridLayer,
                                final GridWidget gridWidget,
                                final boolean restrictEditorWidthToCell,
                                final boolean restrictEditorHeightToCell) {
        super(widget,
              gridLayer,
              gridWidget);
        if (restrictEditorWidthToCell) {
            style(widget).setWidth(100,
                                   Style.Unit.PCT);
        }
        if (restrictEditorHeightToCell) {
            style(widget).setHeight(100,
                                    Style.Unit.PCT);
        }

        style(widget).setPadding(0,
                                 Style.Unit.PX);
        style(widgetContainer).setPadding(2,
                                          Style.Unit.PX);

        getContainer().setWidget(widget);
    }

    @Override
    public void initialise(final GridBodyCellRenderContext context) {
        transform(context);
    }

    @Override
    public void flush(final T value) {
        final int rowIndex = context.getRowIndex();
        final int columnIndex = context.getColumnIndex();
        if (value == null) {
            gridWidget.getModel().deleteCell(rowIndex,
                                             columnIndex);
        } else {
            gridWidget.getModel().setCellValue(rowIndex,
                                               columnIndex,
                                               new GuidedDecisionTableUiCell<T>(value));
        }
    }
}
