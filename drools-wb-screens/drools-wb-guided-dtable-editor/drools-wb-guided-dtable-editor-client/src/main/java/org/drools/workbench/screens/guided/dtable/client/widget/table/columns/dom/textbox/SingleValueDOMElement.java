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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.BaseDTDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiCell;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * A DOMElement that can contain single values; e.g. a TextBox.
 */
public abstract class SingleValueDOMElement<T, W extends Widget> extends BaseDTDOMElement<T, W> {

    public SingleValueDOMElement(final W widget,
                                 final GridLayer gridLayer,
                                 final GridWidget gridWidget) {
        super(widget,
              gridLayer,
              gridWidget);
        style(widget).setWidth(100,
                               Style.Unit.PCT);
        style(widget).setHeight(100,
                                Style.Unit.PCT);
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
            gridWidget.getModel().setCell(rowIndex,
                                          columnIndex,
                                          new GuidedDecisionTableUiCell<T>(value));
        }
    }
}
