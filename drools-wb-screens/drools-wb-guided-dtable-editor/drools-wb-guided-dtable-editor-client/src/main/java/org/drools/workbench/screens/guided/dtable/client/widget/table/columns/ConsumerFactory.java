/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.MultiValueDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.MultiValueSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.SingleValueDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.SingleValueSingletonDOMElementFactory;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;

/**
 * Factory for common consumers used by the different columns.
 */
public class ConsumerFactory {

    static final int MAX_VISIBLE_ROWS = 10;

    /**
     * Callback to set the value of a TextBox based on the Cells value.
     * @param factory Factory that can convert Cells' values to String.
     * @param cell The Cell to be rendered.
     * @return
     */
    public static <T, W extends TextBox, E extends SingleValueDOMElement<T, W>, F extends SingleValueSingletonDOMElementFactory<T, W, E>> Consumer<E> makeOnCreationCallback(final F factory,
                                                                                                                                                                             final GridCell<T> cell) {
        return (e) -> {
            if (hasValue(cell)) {
                e.getWidget().setValue(factory.convert(cell.getValue().getValue()));
            } else {
                e.getWidget().setValue("");
            }
        };
    }

    /**
     * Callback to set the Focus on the TextBox.
     * @return
     */
    public static <T, W extends TextBox, E extends SingleValueDOMElement<T, W>> Consumer<E> makeOnDisplayTextBoxCallback() {
        return (e) -> e.getWidget().setFocus(true);
    }

    /**
     * Callback to select an item in a ListBox based on the Cells value.
     * @param factory Factory that can convert Cells' values to String.
     * @param cell The Cell to be rendered.
     * @return
     */
    public static <T, W extends ListBox, E extends MultiValueDOMElement<T, W>, F extends MultiValueSingletonDOMElementFactory<T, W, E>> Consumer<E> makeOnCreationCallback(final F factory,
                                                                                                                                                                           final GridCell<T> cell,
                                                                                                                                                                           final Map<String, String> enumLookups) {
        return (e) -> {
            final W widget = e.getWidget();

            if (widget.isMultipleSelect()) {
                widget.setVisibleItemCount(Math.min(MAX_VISIBLE_ROWS,
                                                    enumLookups.size()));
            }

            for (Map.Entry<String, String> lookup : enumLookups.entrySet()) {
                widget.addItem(lookup.getValue(),
                               lookup.getKey());
            }
            factory.toWidget(cell,
                             widget);
        };
    }

    /**
     * Callback to set the Focus on the ListBox.
     * @return
     */
    public static <T, W extends ListBox, E extends MultiValueDOMElement<T, W>> Consumer<E> makeOnDisplayListBoxCallback() {
        return (e) -> e.getWidget().setFocus(true);
    }

    /**
     * Callback to set the value of a DatePicker based on the Cells value.
     * @param cell The Cell to be rendered.
     * @return
     */
    public static <E extends SingleValueDOMElement<Date, DatePicker>> Consumer<E> makeOnCreationCallback(final GridCell<Date> cell) {
        return (e) -> {
            final DatePicker widget = e.getWidget();
            if (hasValue(cell)) {
                widget.setValue(cell.getValue().getValue());
            } else {
                widget.setValue(new Date());
            }
        };
    }

    /**
     * Callback to set the Focus on the DatePicker.
     * @return
     */
    public static <E extends SingleValueDOMElement<Date, DatePicker>> Consumer<E> makeOnDisplayDatePickerCallback() {
        return (e) -> e.getWidget().setFocus(true);
    }

    private static <T> boolean hasValue(final GridCell<T> cell) {
        if (cell == null || cell.getValue() == null || cell.getValue().getValue() == null) {
            return false;
        }
        return true;
    }
}
