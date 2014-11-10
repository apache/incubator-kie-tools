/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.tables;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;

public class SelectionColumn<T> extends Column<T, Boolean> {

    public static <T> SelectionColumn<T> createAndAddSelectionColumn( DataGrid<T> dataGrid ) {
        SelectionColumn<T> selectionColumn = new SelectionColumn<T>( dataGrid );
        dataGrid.addColumn( selectionColumn, SafeHtmlUtils.fromSafeConstant( "<br>" ) );
        return selectionColumn;
    }

    private final DataGrid<T> dataGrid;

    public SelectionColumn( DataGrid<T> dataGrid ) {
        super( new CheckboxCell( true,
                                 true ) );
        this.dataGrid = dataGrid;
        addUpdater();
    }

    private void addUpdater() {
        setFieldUpdater( new FieldUpdater<T, Boolean>() {
            public void update( int index,
                                T object,
                                Boolean value ) {
                dataGrid.getSelectionModel().setSelected( object, value );
            }
        } );
    }

    public Boolean getValue( T object ) {
        return dataGrid.getSelectionModel().isSelected( object );
    }

}
