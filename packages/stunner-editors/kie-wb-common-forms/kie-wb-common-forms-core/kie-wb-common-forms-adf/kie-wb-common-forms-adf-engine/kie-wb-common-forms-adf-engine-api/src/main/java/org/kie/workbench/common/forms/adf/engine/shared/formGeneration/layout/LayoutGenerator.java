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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.adf.definitions.settings.ColSpan;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutColumnDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutSettings;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@Dependent
public class LayoutGenerator {

    public static final int MAX_SPAN = 12;

    private ColSpan[] structure;

    private List<Row> rows = new ArrayList<>();

    private Row currentRow;

    public void init(LayoutColumnDefinition[] structure) {

        currentRow = null;

        rows.clear();

        int autoCount = 0;
        int maxSpan = 0;

        for (LayoutColumnDefinition col : structure) {
            if (col.getSpan().equals(ColSpan.AUTO)) {
                autoCount++;
            } else {
                maxSpan += col.getSpan().getSpan();
            }
        }

        if (maxSpan > MAX_SPAN) {
            throw new IllegalArgumentException("Max SPAN allowed for all layout columns is 12.");
        }
        if (maxSpan < MAX_SPAN && autoCount == 0) {
            throw new IllegalArgumentException("Wrong layout definition, the columns total span must be 12");
        }
        if (maxSpan + autoCount > MAX_SPAN) {
            throw new IllegalArgumentException("There's not enough space for all columns in layout.");
        }

        int freeSpan = MAX_SPAN - maxSpan;

        int freeOffset = 0;
        int freeAVGSpan = 0;

        if (freeSpan > 0) {
            freeOffset = freeSpan % autoCount;
            freeAVGSpan = Math.floorDiv(freeSpan,
                                        autoCount);
        }

        List<ColSpan> spans = new ArrayList<>();

        for (LayoutColumnDefinition definition : structure) {
            if (definition.getSpan().equals(ColSpan.AUTO)) {
                int span = freeAVGSpan;

                if (freeOffset > 0) {
                    span++;
                    freeOffset--;
                }
                spans.add(ColSpan.calculateSpan(span));
            } else {
                spans.add(definition.getSpan());
            }
        }

        this.structure = spans.toArray(new ColSpan[spans.size()]);
    }

    public void addComponent(LayoutComponent component,
                             LayoutSettings settings) {
        if (currentRow == null || currentRow.isFull()) {
            newRow();
        }

        if (!currentRow.addComponent(component,
                                     settings)) {
            newRow();
            currentRow.addComponent(component,
                                    settings);
        }
    }

    public LayoutTemplate build() {
        LayoutTemplate template = new LayoutTemplate();

        rows.stream()
                .filter(row -> !row.cells.isEmpty())
                .forEach(row -> {
                    LayoutRow layoutRow = new LayoutRow();

                    template.addRow(layoutRow);

                    row.cells.forEach(cell -> {
                        LayoutColumn layoutColumn = new LayoutColumn(String.valueOf(cell.horizontalSpan));
                        layoutRow.add(layoutColumn);

                        if (cell.getComponentsCount() == 0) {
                            return;
                        } else if (cell.getComponentsCount() == 1) {
                            layoutColumn.add(cell.components.get(0));
                        } else {
                            cell.components.forEach(component -> {
                                LayoutRow nestedRow = new LayoutRow();
                                layoutColumn.addRow(nestedRow);
                                LayoutColumn nestedColumn = new LayoutColumn(String.valueOf(MAX_SPAN));
                                nestedRow.add(nestedColumn);
                                nestedColumn.add(component);
                            });
                        }
                    });
                });

        return template;
    }

    protected void newRow() {
        List<Cell> cells = new ArrayList<>();
        for (ColSpan span : structure) {
            cells.add(new Cell(span.getSpan()));
        }
        currentRow = new Row(cells);
        rows.add(currentRow);
    }

    private class Row {

        List<Cell> cells;

        int currentIndex = 0;

        public Row(List<Cell> cells) {
            this.cells = cells;
        }

        boolean isFull() {
            return currentIndex == cells.size() || (currentIndex > 0 && cells.get(currentIndex - 1).wrap);
        }

        public boolean addComponent(LayoutComponent component,
                                    LayoutSettings settings) {

            int horizontalSpan = settings.getHorizontalSpan();

            Cell currentCell = cells.get(currentIndex);
            currentCell.wrap = settings.isWrap();

            currentCell.addLayoutComponent(component);

            if (horizontalSpan > 1) {
                while (horizontalSpan > 1 && cells.size() > currentIndex + 1) {
                    Cell cell = cells.remove(currentIndex + 1);
                    currentCell.horizontalSpan += cell.horizontalSpan;
                }
            }

            currentIndex++;
            return true;
        }
    }

    private class Cell {

        private int horizontalSpan = 1;
        private int verticalSpan = 1;
        private boolean wrap;

        private List<LayoutComponent> components = new ArrayList<>();

        public Cell(int horizontalSpan) {
            this.horizontalSpan = horizontalSpan;
        }

        private void addLayoutComponent(LayoutComponent component) {
            components.add(component);
        }

        private int getComponentsCount() {
            return components.size();
        }

        public int getVerticalSpan() {
            return verticalSpan;
        }

        public void setVerticalSpan(int verticalSpan) {
            this.verticalSpan = verticalSpan;
        }
    }
}
