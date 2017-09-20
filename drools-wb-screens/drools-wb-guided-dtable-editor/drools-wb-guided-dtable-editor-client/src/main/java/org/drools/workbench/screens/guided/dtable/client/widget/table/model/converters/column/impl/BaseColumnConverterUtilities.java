/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.util.Arrays;
import java.util.List;

import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;

public class BaseColumnConverterUtilities {

    public static void toWidget(final boolean isMultipleSelect,
                                final GridCell<String> cell,
                                final ListBox widget) {
        if (cell == null || cell.getValue() == null || cell.getValue().getValue() == null) {
            if (widget.getItemCount() > 0) {
                widget.setSelectedIndex(0);
            }
        } else {
            final String value = cell.getValue().getValue();
            if (isMultipleSelect) {
                final List<String> values = Arrays.asList(value.split(","));
                for (int i = 0; i < widget.getItemCount(); i++) {
                    widget.setItemSelected(i,
                                           values.contains(widget.getValue(i)));
                }
            } else {
                for (int i = 0; i < widget.getItemCount(); i++) {
                    if (widget.getValue(i).equals(value)) {
                        widget.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    public static String fromWidget(final boolean isMultipleSelect,
                                    final ListBox widget) {
        final StringBuilder sb = new StringBuilder();
        if (isMultipleSelect) {
            for (int i = 0; i < widget.getItemCount(); i++) {
                if (widget.isItemSelected(i)) {
                    sb.append(widget.getValue(i)).append(",");
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
        } else {
            int selectedIndex = widget.getSelectedIndex();
            if (selectedIndex >= 0) {
                sb.append(widget.getValue(selectedIndex));
            }
        }

        return sb.toString();
    }
}
