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


package org.uberfire.ext.widgets.common.client.tables;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.ext.widgets.table.client.UberfireColumnPicker;

public class ColumnPicker<T> extends UberfireColumnPicker<T> {

    public static int DETAULT_COLUMN_WIDTH = 120;
    private int dataGridMinWidth = 0;

    public ColumnPicker(DataGrid<T> dataGrid) {
        super(dataGrid);
    }

    protected void sortAndAddColumns(List<ColumnMeta<T>> columnMetas) {
        updateColumnsMeta(columnMetas);
        super.sortAndAddColumns(columnMetas);
    }

    private void updateColumnsMeta(List<ColumnMeta<T>> columnMetas) {
        for (ColumnMeta meta : columnMetas) {
            checkColumnMeta(meta);
        }
    }

    protected void checkColumnMeta(ColumnMeta<T> columnMeta) {
    }

    protected void addResetButtom(final int left,
                                  final int top,
                                  VerticalPanel popupContent) {
            Button resetButton = GWT.create(Button.class);
            resetButton.setText(CommonConstants.INSTANCE.Reset());
            resetButton.setSize(ButtonSize.EXTRA_SMALL);
            resetButton.addClickHandler(event -> resetTableColumns(left,
                                                                   top));

            popupContent.add(resetButton);
    }

    public void adjustColumnWidths() {
        super.adjustColumnWidths();
    }

    public int getDataGridMinWidth() {
        return this.dataGridMinWidth;
    }

}
