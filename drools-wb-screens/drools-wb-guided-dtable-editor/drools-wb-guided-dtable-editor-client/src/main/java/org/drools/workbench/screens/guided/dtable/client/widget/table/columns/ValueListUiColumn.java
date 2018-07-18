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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ait.lienzo.client.core.shape.Text;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxSingletonDOMElementFactory;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

public class ValueListUiColumn extends BaseSingletonDOMElementUiColumn<String, ListBox, ListBoxDOMElement<String, ListBox>, ListBoxSingletonDOMElementFactory<String, ListBox>> {

    private final Map<String, String> valueListLookup = new HashMap<String, String>();

    public ValueListUiColumn(final List<HeaderMetaData> headerMetaData,
                             final double width,
                             final boolean isResizable,
                             final boolean isVisible,
                             final GuidedDecisionTablePresenter.Access access,
                             final ListBoxSingletonDOMElementFactory<String, ListBox> factory,
                             final Map<String, String> valueListLookup) {
        this(headerMetaData,
             width,
             isResizable,
             isVisible,
             access,
             factory,
             valueListLookup,
             false);
    }

    public ValueListUiColumn(final List<HeaderMetaData> headerMetaData,
                             final double width,
                             final boolean isResizable,
                             final boolean isVisible,
                             final GuidedDecisionTablePresenter.Access access,
                             final ListBoxSingletonDOMElementFactory<String, ListBox> factory,
                             final Map<String, String> valueListLookup,
                             final boolean isMultipleSelect) {
        super(headerMetaData,
              new ValueListUiColumnCellRenderer(factory,
                                                valueListLookup,
                                                isMultipleSelect),
              width,
              isResizable,
              isVisible,
              access,
              factory);
        this.valueListLookup.putAll(valueListLookup);
    }

    @Override
    public void doEdit(final GridCell<String> cell,
                       final GridBodyCellRenderContext context,
                       final Consumer<GridCellValue<String>> callback) {
        factory.attachDomElement(context,
                                 ConsumerFactory.makeOnCreationCallback(factory,
                                                                        cell,
                                                                        valueListLookup),
                                 ConsumerFactory.makeOnDisplayListBoxCallback());
    }
}

class ValueListUiColumnCellRenderer
        extends BaseSingletonDOMElementUiColumn.CellRenderer<String, ListBox, ListBoxDOMElement<String, ListBox>> {

    private final Map<String, String> valueListLookup;
    private boolean isMultipleSelect;

    public ValueListUiColumnCellRenderer(final SingletonDOMElementFactory<ListBox, ListBoxDOMElement<String, ListBox>> factory,
                                         final Map<String, String> valueListLookup,
                                         final boolean isMultipleSelect) {
        super(factory);
        this.valueListLookup = valueListLookup;
        this.isMultipleSelect = isMultipleSelect;
    }

    @Override
    protected void doRenderCellContent(final Text t,
                                       final String value,
                                       final GridBodyCellRenderContext context) {
        t.setText(getLabel(value.toString()));
    }

    private String getLabel(final String value) {
        if (!isMultipleSelect) {
            return getValue(value);
        }

        final String[] values = value.split(",");

        return Stream.of(values)
                .map(s -> getValue(s))
                .collect(Collectors.joining(","));
    }

    private String getValue(final String value) {
        if (valueListLookup.containsKey(value)) {
            return valueListLookup.get(value);
        } else {
            return value;
        }
    }
}
