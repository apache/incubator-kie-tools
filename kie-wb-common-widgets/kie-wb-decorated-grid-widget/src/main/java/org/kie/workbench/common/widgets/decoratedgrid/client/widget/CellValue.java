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

import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * This is a wrapper around a value. The wrapper provides additional information
 * required to use the vanilla value in a Decision Table with merge
 * capabilities. One coordinate is maintained and two indexes to map to and from
 * HTML table coordinates. The indexes used to be maintained in SelectionManager
 * however it required two more N x N collections of "mapping" objects in
 * addition to that containing the actual data. The coordinate represents the
 * physical location of the cell on an (R, C) grid. One index maps the physical
 * coordinate of the cell to the logical coordinate of the HTML table whilst the
 * other index maps from the logical coordinate to the physical cell. For
 * example, given data (0,0), (0,1), (1,0) and (1,1) with cell at (0,0) merged
 * into (1,0) only the HTML coordinates (0,0), (0,1) and (1,0) exist; with
 * physical coordinates (0,0) and (1,0) relating to HTML coordinate (0,0) which
 * has a row span of 2. Therefore physical cells (0,0) and (1,0) have a
 * <code>mapDataToHtml</code> coordinate of (0,0) whilst physical cell (1,0) has
 * a <code>mapHtmlToData</code> coordinate of (1,1).
 * @param <T> The data-type of the value
 */
public class CellValue<T extends Comparable<T>>
        implements
        Comparable<CellValue<T>> {

    //Possible states of the cell
    public static enum CellState {
        SELECTED,
        GROUPED,
        OTHERWISE
    }

    /**
     * A grouped cell, containing a list of grouped cells. If a cell spanning
     * three rows is grouped, the normal CellValue is replaced with a
     * GroupedCellValue (within a GroupedDynamicDataRow). In addition to the
     * GroupedCellValue's value the new GroupedCellValue contains a list of the
     * original three cells.
     */
    public class GroupedCellValue extends CellValue<T> {

        //Grouped cells
        private List<CellValue<T>> groupedCells = new ArrayList<CellValue<T>>();

        /**
         * Constructor, nothing to see here, move on
         * @param value
         * @param row
         * @param col
         */
        public GroupedCellValue( T value ) {
            super( value );
        }

        /**
         * Add a cell to the group of cells
         * @param cell
         */
        public void addCellToGroup( CellValue<T> cell ) {
            this.groupedCells.add( cell );
        }

        /**
         * Ensure the children (or grouped) Cells' State reflects the parent
         * Grouped Cell's State change
         */
        @Override
        public void addState( CellState state ) {
            for ( CellValue<T> cell : this.groupedCells ) {
                cell.addState( state );
            }
            super.addState( state );
        }

        /**
         * Does this grouped cell contain multiple values
         * @return
         */
        public boolean hasMultipleValues() {
            return checkForMultipleValues();
        }

        /**
         * Ensure the children (or grouped) Cells' State reflects the parent
         * Grouped Cell's State change
         */
        @Override
        public void removeState( CellState state ) {
            for ( CellValue<T> cell : this.groupedCells ) {
                cell.removeState( state );
            }
            super.removeState( state );
        }

        /**
         * Ensure the children (or grouped) Cells' value reflects the parent
         * Grouped Cell's value change
         */
        @Override
        public void setValue( Object value ) {
            for ( CellValue<T> cell : this.groupedCells ) {
                cell.setValue( value );
            }
            super.setValue( value );
        }

        //Check whether the cell contains multiple values
        private boolean checkForMultipleValues() {
            boolean hasMultipleValues = false;
            T value1 = super.getValue();
            for ( CellValue<T> cell : this.groupedCells ) {
                if ( cell instanceof CellValue.GroupedCellValue ) {
                    GroupedCellValue gcv = (GroupedCellValue) cell;
                    hasMultipleValues = hasMultipleValues || gcv.checkForMultipleValues();
                }
                T value2 = cell.getValue();
                hasMultipleValues = hasMultipleValues || !equalOrNull( value1,
                                                                       value2 );
            }
            return hasMultipleValues;
        }

        /**
         * Get the list of cells grouped
         * @return
         */
        List<CellValue<T>> getGroupedCells() {
            return this.groupedCells;
        }

    }

    private T value;
    private int                rowSpan       = 1;
    private Coordinate coordinate    = new Coordinate();
    private Coordinate         mapHtmlToData = new Coordinate();
    private Coordinate         mapDataToHtml = new Coordinate();
    private EnumSet<CellState> state         = EnumSet.noneOf( CellState.class );

    public CellValue( T value ) {
        this.value = value;
    }

    public void addState( CellState state ) {
        this.state.add( state );
    }

    // Used for sorting
    public int compareTo( CellValue<T> cv ) {
        if ( this.value == null ) {
            if ( cv.value == null ) {
                return 0;
            }
            return 1;
        } else {
            if ( cv.value == null ) {
                return -1;
            }
        }
        return this.value.compareTo( cv.value );
    }

    /**
     * Convert a CellValue into a GroupedCellValue object
     * @return
     */
    public GroupedCellValue convertToGroupedCell() {
        GroupedCellValue groupedCell = new GroupedCellValue( this.getValue() );
        if ( this.isOtherwise() ) {
            groupedCell.addState( CellState.OTHERWISE );
        }
        return groupedCell;
    }

    @Override
    @SuppressWarnings("rawtypes")
    // Used by calls to DynamicDataRow.equals()
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( !( obj instanceof CellValue ) ) {
            return false;
        }
        CellValue that = (CellValue) obj;
        return equalOrNull( this.value,
                            that.value )
                && this.rowSpan == that.rowSpan
                && equalOrNull( this.coordinate,
                                that.coordinate )
                && equalOrNull( this.mapHtmlToData,
                                that.mapHtmlToData )
                && equalOrNull( this.mapDataToHtml,
                                that.mapDataToHtml )
                && this.state.equals( that.state );
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    public Coordinate getHtmlCoordinate() {
        return new Coordinate( this.mapDataToHtml );
    }

    public Coordinate getPhysicalCoordinate() {
        return new Coordinate( this.mapHtmlToData );
    }

    public int getRowSpan() {
        return this.rowSpan;
    }

    public T getValue() {
        return this.value;
    }

    @Override
    // Used by calls to DynamicDataRow.equals()
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + ( value == null ? 0 : value.hashCode() );
        hash = ~~hash;
        hash = hash * 31 + rowSpan;
        hash = ~~hash;
        hash = hash * 31 + ( coordinate == null ? 0 : coordinate.hashCode() );
        hash = ~~hash;
        hash = hash * 31 + ( mapHtmlToData == null ? 0 : mapHtmlToData.hashCode() );
        hash = ~~hash;
        hash = hash * 31 + ( mapDataToHtml == null ? 0 : mapDataToHtml.hashCode() );
        hash = ~~hash;
        hash = hash * 31 + state.hashCode();
        hash = ~~hash;
        return hash;
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    public boolean isGrouped() {
        return this.state.contains( CellState.GROUPED );
    }

    public boolean isOtherwise() {
        return this.state.contains( CellState.OTHERWISE );
    }

    public boolean isSelected() {
        return this.state.contains( CellState.SELECTED );
    }

    public void removeState( CellState state ) {
        this.state.remove( state );
    }

    public void setCoordinate( Coordinate coordinate ) {
        if ( coordinate == null ) {
            throw new IllegalArgumentException( "Coordinate cannot be null." );
        }
        this.coordinate = coordinate;
    }

    public void setHtmlCoordinate( Coordinate c ) {
        if ( c == null ) {
            throw new IllegalArgumentException( "Coordinate cannot be null." );
        }
        this.mapDataToHtml = c;
    }

    public void setPhysicalCoordinate( Coordinate c ) {
        if ( c == null ) {
            throw new IllegalArgumentException( "Coordinate cannot be null." );
        }
        this.mapHtmlToData = c;
    }

    public void setRowSpan( int rowSpan ) {
        if ( rowSpan < 0 ) {
            throw new IllegalArgumentException( "rowSpan cannot be less than zero." );
        }
        this.rowSpan = rowSpan;
    }

    @SuppressWarnings("unchecked")
    public void setValue( Object value ) {
        this.value = (T) value;
    }

    //Check whether two values are equal or both null
    private boolean equalOrNull( Object o1,
                                 Object o2 ) {
        if ( o1 == null && o2 == null ) {
            return true;
        }
        if ( o1 != null && o2 == null ) {
            return false;
        }
        if ( o1 == null && o2 != null ) {
            return false;
        }
        return o1.equals( o2 );
    }

}
