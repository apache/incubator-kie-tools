/*
 * Copyright 2019 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.tables;

import java.util.List;

import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.ext.widgets.table.client.UberfireColumnPicker;

public class ColumnPicker<T> extends UberfireColumnPicker<T> {

    public static int DETAULT_COLUMN_WIDTH = 120;
    private int dataGridMinWidth = 0;

    public ColumnPicker(DataGrid<T> dataGrid) {
        super(dataGrid);
    }

    protected void sortAndAddColumns(List<ColumnMeta<T>> columnMetas) {
        super.sortAndAddColumns(columnMetas);
    }

    public void adjustColumnWidths() {
        super.adjustColumnWidths();

    }

    public int getDataGridMinWidth() {
        return this.dataGridMinWidth;
    }

}
