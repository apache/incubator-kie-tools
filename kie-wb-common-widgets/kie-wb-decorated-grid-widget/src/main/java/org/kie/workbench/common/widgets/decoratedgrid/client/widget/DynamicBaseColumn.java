/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicDataRow;

/**
 * A representation of a column in a table. The column may maintain view data
 * for each cell on demand. New view data, if needed, is created by the cell's
 * onBrowserEvent method, stored in the Column, and passed to future calls to
 * Cell's {@link Cell#onBrowserEvent} and {@link Cell#render} methods.
 * <p/>
 * Forked GWT2.1.1's Column<T, C> class to make Cell<C> non-final.
 */
public abstract class DynamicBaseColumn
        implements
        HasCell<DynamicDataRow, CellValue<? extends Comparable<?>>> {

    /**
     * The {@link Cell} responsible for rendering items in the column.
     */
    protected DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell;

    /**
     * The {@link FieldUpdater} used for updating values in the column.
     */
    protected FieldUpdater<DynamicDataRow, CellValue<? extends Comparable<?>>> fieldUpdater;

    /**
     * Construct a new Column with a given {@link Cell}.
     * @param cell the Cell used by this Column
     */
    public DynamicBaseColumn( DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell ) {
        if ( cell == null ) {
            throw new IllegalArgumentException( "cell cannot be null" );
        }
        this.cell = cell;
    }

    /**
     * Returns the {@link Cell} responsible for rendering items in the column.
     * @return a Cell
     */
    public DecoratedGridCellValueAdaptor<? extends Comparable<?>> getCell() {
        return cell;
    }

    /**
     * Returns the {@link FieldUpdater} used for updating values in the column.
     * @return an instance of FieldUpdater<T, C>
     * @see #setFieldUpdater(FieldUpdater)
     */
    public FieldUpdater<DynamicDataRow, CellValue<? extends Comparable<?>>> getFieldUpdater() {
        return fieldUpdater;
    }

    /**
     * Returns the column value from within the underlying data object.
     */
    public abstract CellValue<? extends Comparable<?>> getValue( DynamicDataRow object );

    /**
     * Handle a browser event that took place within the column.
     * @param context the cell context
     * @param elem the parent Element
     * @param object the base object to be updated
     * @param event the native browser event
     */
    public void onBrowserEvent( Context context,
                                Element elem,
                                final DynamicDataRow object,
                                NativeEvent event ) {
        final int index = context.getIndex();
        ValueUpdater<CellValue<? extends Comparable<?>>> valueUpdater = ( fieldUpdater == null ) ? null
                : new ValueUpdater<CellValue<? extends Comparable<?>>>() {
            public void update( CellValue<? extends Comparable<?>> value ) {
                fieldUpdater.update( index,
                                     object,
                                     value );
            }
        };
        cell.onBrowserEvent( context,
                             elem,
                             getValue( object ),
                             event,
                             valueUpdater );
    }

    /**
     * Render the object into the cell.
     * @param context the cell context
     * @param object the object to render
     * @param sb the buffer to render into
     */
    public void render( Context context,
                        DynamicDataRow object,
                        SafeHtmlBuilder sb ) {
        cell.render( context,
                     getValue( object ),
                     sb );
    }

    /**
     * Set the cell used to render the column
     * @param cell
     */
    public void setCell( DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell ) {
        if ( cell == null ) {
            throw new IllegalArgumentException( "cell cannot be null" );
        }
        this.cell = cell;
    }

    /**
     * Set the {@link FieldUpdater} used for updating values in the column.
     * @param fieldUpdater the field updater
     * @see #getFieldUpdater()
     */
    public void setFieldUpdater( FieldUpdater<DynamicDataRow, CellValue<? extends Comparable<?>>> fieldUpdater ) {
        if ( fieldUpdater == null ) {
            throw new IllegalArgumentException( "fieldUpdater cannot be null" );
        }
        this.fieldUpdater = fieldUpdater;
    }

}
