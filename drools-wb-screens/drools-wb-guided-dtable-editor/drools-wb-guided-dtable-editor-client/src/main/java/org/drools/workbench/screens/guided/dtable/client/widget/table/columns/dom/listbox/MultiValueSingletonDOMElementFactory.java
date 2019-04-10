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

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * A DOMElement Factory for single-instance multi-value DOMElements.
 */
public abstract class MultiValueSingletonDOMElementFactory<T, W extends ListBox, E extends MultiValueDOMElement<T, W>> extends BaseSingletonDOMElementFactory<T, W, E> {

    public MultiValueSingletonDOMElementFactory(final GridLienzoPanel gridPanel,
                                                final GridLayer gridLayer,
                                                final GuidedDecisionTableView gridWidget) {
        super(gridPanel,
              gridLayer,
              gridWidget);
    }

    public void toWidget(final GridCell<T> cell,
                         final W widget) {
        if (cell == null || cell.getValue() == null || cell.getValue().getValue() == null) {
            if (widget.getItemCount() > 0) {
                widget.setSelectedIndex(0);
            }
        } else {
            final T value = cell.getValue().getValue();
            final String convertedValue = convert(value);
            for (int i = 0; i < widget.getItemCount(); i++) {
                if (widget.getValue(i).equals(convertedValue)) {
                    widget.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public T fromWidget(final W widget) {
        final StringBuilder sb = new StringBuilder();
        int selectedIndex = widget.getSelectedIndex();
        if (selectedIndex >= 0) {
            sb.append(widget.getValue(selectedIndex));
        }
        return convert(sb.toString());
    }

    /**
     * Convert from the given typed value to a String
     * @param value The value to be converted
     * @return A String representing the type
     */
    public abstract String convert(final T value);

    /**
     * Convert from the given String to a typed value
     * @param value The String to be converted
     * @return A typed value
     */
    public abstract T convert(final String value);
}
