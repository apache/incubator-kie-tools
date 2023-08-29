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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

public abstract class ColumnFieldUpdater<TYPE, FLAT_TYPE> implements FieldUpdater<TableEntry<TYPE>, FLAT_TYPE> {

    private UberfirePagedTable<TableEntry<TYPE>> table;
    private Column column;
    private CellEditionHandler cellEditionHandler;

    public ColumnFieldUpdater(UberfirePagedTable<TableEntry<TYPE>> table, Column column) {
        this.table = table;
        this.column = column;
    }

    public void setCellEditionHandler(CellEditionHandler<TYPE> cellEditionHandler) {
        this.cellEditionHandler = cellEditionHandler;
    }

    protected abstract boolean validate(FLAT_TYPE value,
                                        TableEntry<TYPE> model);

    @Override
    public void update(int index,
                       TableEntry<TYPE> model,
                       FLAT_TYPE value) {
        cellEditionHandler.clearValidationErrors();
        if(validate(value, model)) {
            cellEditionHandler.valueChanged(index,
                                            convert(value));
        } else {
            rollbackChange(model);
        }
    }

    private void rollbackChange(TableEntry<TYPE> model) {
        EditTextCell cell = (EditTextCell) column.getCell();
        cell.clearViewData(table.getDataProvider().getKey(model));
        table.redraw();
    }

    protected TYPE convert(FLAT_TYPE flatValue) {
        return (TYPE) flatValue;
    }
}
