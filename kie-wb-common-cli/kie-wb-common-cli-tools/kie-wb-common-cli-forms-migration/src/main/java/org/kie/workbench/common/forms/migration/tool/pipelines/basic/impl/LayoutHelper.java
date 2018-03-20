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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

public class LayoutHelper {

    private int maxColumns = 12;

    private List<List<LayoutComponent>> grid = new ArrayList();

    private List<LayoutComponent> currentRow;

    public LayoutHelper() {
        addRow();
    }

    public LayoutHelper(int maxColumns) {
        this();

        if(1 > maxColumns || maxColumns > 12) {
            throw new IllegalArgumentException("Invalid maxColums value (" + maxColumns + ") value should be between 1 - 12");
        }

        this.maxColumns = maxColumns;
    }

    public LayoutHelper newRow() {
        if(!currentRow.isEmpty()) {
            addRow();
        }
        return this;
    }

    private void addRow() {
        currentRow = new ArrayList<>();
        grid.add(currentRow);
    }

    public LayoutHelper add(LayoutComponent layoutComponent) {
        if(currentRow.size() == maxColumns) {
            newRow();
        }

        currentRow.add(layoutComponent);

        return this;
    }

    public LayoutTemplate build() {
        LayoutTemplate layoutTemplate = new LayoutTemplate();

        grid.forEach(row -> layoutTemplate.addRow(build(row)));

        return layoutTemplate;
    }

    private LayoutRow build(List<LayoutComponent> components) {
        LayoutRow row = new LayoutRow();

        int columns = components.size();

        if(columns > 0) {

            int span = Math.floorDiv(maxColumns, columns);

            CountDown countDown = new CountDown(maxColumns % columns);

            components.forEach(layoutComponent -> {
                int columnSpan = span;

                if(countDown.hasValue()) {
                    columnSpan ++;
                }

                LayoutColumn column = new LayoutColumn(String.valueOf(columnSpan));

                column.add(layoutComponent);
                row.add(column);
            });

        }

        return row;
    }

    private class CountDown {
        private int value;

        public CountDown(int value) {
            this.value = value;
        }

        public boolean hasValue() {
            return 0 < value --;
        }
    }
}
