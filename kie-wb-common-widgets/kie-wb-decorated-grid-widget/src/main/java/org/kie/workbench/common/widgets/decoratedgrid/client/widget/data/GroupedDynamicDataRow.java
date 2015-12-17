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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.data;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;

/**
 * A grouped row of data in the Decision Table. This object represents the row
 * within the table that is visible. It contains a collection of grouped rows
 * excluding the first row of the grouped block. For example: A set of five rows
 * grouped results in one GroupedDynamicDataRow containing four child
 * DynamicDataRows
 */
public class GroupedDynamicDataRow extends DynamicDataRow {

    private static final long serialVersionUID = 5758783945346050329L;

    private List<DynamicDataRow> groupedRows = new ArrayList<DynamicDataRow>();

    @Override
    public CellValue<? extends Comparable<?>> get( int index ) {
        CellValue<?> cv = super.get( index );
        return cv;
    }

    public List<DynamicDataRow> getChildRows() {
        return this.groupedRows;
    }

    /**
     * Set a value at the given index. All grouped child cells will be set to
     * the same value. Caution should be exercised if the value was first read
     * from a GroupedDynamicDataRow as the value will be a GroupedCellValue
     * which, if added back to the row, will not give the desired result.
     * @param index
     * @param element The CellValue
     */
    @Override
    public CellValue<? extends Comparable<?>> set( int index,
                                                   CellValue<? extends Comparable<?>> element ) {
        for ( DynamicDataRow groupedRow : this.groupedRows ) {
            groupedRow.set( index,
                            element );
        }
        return super.set( index,
                          element );
    }

    /**
     * Add a value. Child cells will be created and set to the same value.
     * Caution should be exercised if the value was first read from a
     * GroupedDynamicDataRow as the value will be a GroupedCellValue which, if
     * added back to the row, will not give the desired result.
     * @param e The CellValue
     */
    @Override
    public boolean add( CellValue<? extends Comparable<?>> e ) {
        for ( DynamicDataRow groupedRow : this.groupedRows ) {
            groupedRow.add( e );
        }
        return super.add( e );
    }

    /**
     * Add a value at the given index. Child cells will be created and set to
     * the same value. Caution should be exercised if the value was first read
     * from a GroupedDynamicDataRow as the value will be a GroupedCellValue
     * which, if added back to the row, will not give the desired result.
     * @param index
     * @param element The CellValue
     */
    @Override
    public void add( int index,
                     CellValue<? extends Comparable<?>> element ) {
        super.add( index,
                   element );
        for ( DynamicDataRow groupedRow : this.groupedRows ) {
            groupedRow.add( index,
                            element );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean addChildRow( DynamicDataRow childRow ) {
        for ( int iCol = 0; iCol < childRow.size(); iCol++ ) {
            if ( this.get( iCol ) instanceof CellValue.GroupedCellValue ) {
                CellValue.GroupedCellValue gcv = (CellValue.GroupedCellValue) this.get( iCol );
                gcv.addCellToGroup( childRow.get( iCol ) );
            }
        }
        return this.groupedRows.add( childRow );
    }

    @Override
    void clear() {
        for ( DynamicDataRow groupedRow : this.groupedRows ) {
            groupedRow.clear();
        }
        super.clear();
    }

    /**
     * Remove a value at the given index
     */
    @Override
    public CellValue<? extends Comparable<?>> remove( int index ) {
        for ( DynamicDataRow groupedRow : this.groupedRows ) {
            groupedRow.remove( index );
        }
        return super.remove( index );
    }

}
