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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.SortConfiguration;

/**
 * A simple container for rows of data.
 */
public class DynamicData
        implements
        Iterable<DynamicDataRow> {

    private boolean isMerged = false;

    private List<Boolean> visibleColumns = new ArrayList<Boolean>();

    private static final long serialVersionUID = 5061393855340039472L;

    private List<DynamicDataRow> data = new ArrayList<DynamicDataRow>();

    /**
     * Add column to data
     * @param index
     * @param columnData
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addColumn( int index,
                           List<CellValue<? extends Comparable<?>>> columnData,
                           boolean isVisible ) {

        //Check the column contains the same number of rows as the existing table
        int numberOfRows = 0;
        if ( this.data.size() > 0 ) {
            for ( DynamicDataRow row : this.data ) {
                numberOfRows++;
                if ( row instanceof GroupedDynamicDataRow ) {
                    numberOfRows = numberOfRows + ( (GroupedDynamicDataRow) row ).getChildRows().size() - 1;
                }
            }
        }
        if ( numberOfRows != columnData.size() ) {
            throw new IllegalArgumentException( "columnData contains a different number of rows to that defined." );
        }

        //Add the column data to the table
        int iRowIndex = 0;
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            CellValue<? extends Comparable<?>> cell = columnData.get( iRowIndex );
            if ( row instanceof GroupedDynamicDataRow ) {
                GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;

                //Setting value on a GroupedCellValue causes all children to assume the same value
                CellValue.GroupedCellValue gcv = cell.convertToGroupedCell();
                groupedRow.add( index,
                                gcv );

                //So set the children's values accordingly
                for ( int iGroupedRow = 0; iGroupedRow < groupedRow.getChildRows().size(); iGroupedRow++ ) {
                    cell = columnData.get( iRowIndex );
                    gcv.addCellToGroup( cell );
                    groupedRow.getChildRows().get( iGroupedRow ).set( index,
                                                                      cell );
                    iRowIndex++;
                }
            } else {
                row.add( index,
                         cell );
                iRowIndex++;
            }
        }

        visibleColumns.add( index,
                            isVisible );

        assertModelMerging();
    }

    /**
     * Move a column
     * @param sourceColumnIndex
     * @param targetColumnIndex
     */
    public void moveColumn( int sourceColumnIndex,
                            int targetColumnIndex ) {
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            row.add( targetColumnIndex,
                     row.remove( sourceColumnIndex ) );
        }
        visibleColumns.add( targetColumnIndex,
                            visibleColumns.remove( sourceColumnIndex ) );
        assertModelMerging();
    }

    /**
     * Add an empty row of data to the end of the table
     * @return DynamicDataRow The newly created row
     */
    public DynamicDataRow addRow() {
        DynamicDataRow row = new DynamicDataRow();
        data.add( row );

        assertModelMerging();
        return row;
    }

    /**
     * Add a row of data at the specified index
     * @param index
     * @param rowData
     */
    public void addRow( int index,
                        DynamicDataRow rowData ) {
        data.add( index,
                  rowData );

        assertModelMerging();
    }

    /**
     * Add a row of data at the end of the table
     * @param rowData
     */
    public void addRow( DynamicDataRow rowData ) {
        addRow( data.size(),
                rowData );
    }

    /**
     * Apply grouping by collapsing applicable rows
     * @param startCell
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void applyModelGrouping( CellValue<?> startCell ) {

        int startRowIndex = startCell.getCoordinate().getRow();
        int endRowIndex = findMergedCellExtent( startCell.getCoordinate() ).getRow();
        int colIndex = startCell.getCoordinate().getCol();

        //Delete grouped rows replacing with a single "grouped" row
        CellValue.GroupedCellValue groupedCell;
        DynamicDataRow row = data.get( startRowIndex );
        GroupedDynamicDataRow groupedRow = new GroupedDynamicDataRow();
        for ( int iCol = 0; iCol < row.size(); iCol++ ) {
            groupedCell = row.get( iCol ).convertToGroupedCell();
            if ( iCol == colIndex ) {
                groupedCell.addState( CellValue.CellState.GROUPED );
            } else {
                groupedCell.removeState( CellValue.CellState.GROUPED );
            }
            groupedRow.add( groupedCell );
        }

        //Add individual cells to "grouped" row
        for ( int iRow = startRowIndex; iRow <= endRowIndex; iRow++ ) {
            DynamicDataRow childRow = data.get( startRowIndex );
            groupedRow.addChildRow( childRow );
            data.remove( childRow );
        }
        data.remove( row );
        data.add( startRowIndex,
                  groupedRow );

        assertModelMerging();
    }

    public void clear() {
        data.clear();
    }

    /**
     * Delete column data
     * @param index
     */
    public void deleteColumn( int index ) {
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            row.remove( index );
        }
        visibleColumns.remove( index );
        assertModelMerging();
    }

    public DynamicDataRow deleteRow( int index ) {
        DynamicDataRow row = data.remove( index );
        assertModelMerging();
        return row;
    }

    /**
     * Get the CellValue at the given coordinate
     * @param c
     * @return
     */
    public CellValue<? extends Comparable<?>> get( Coordinate c ) {
        return data.get( c.getRow() ).get( c.getCol() );
    }

    public DynamicDataRow get( int index ) {
        return data.get( index );
    }

    /**
     * Return grid's data. Grouping in the data will be expanded and can
     * therefore can be used prior to populate the underlying data structures
     * prior to persisting.
     * @return data
     */
    public DynamicData getFlattenedData() {
        DynamicData dataClone = new DynamicData();
        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            if ( row instanceof GroupedDynamicDataRow ) {
                List<DynamicDataRow> expandedRow = expandGroupedRow( row,
                                                                     true );
                dataClone.data.addAll( expandedRow );
            } else {
                dataClone.data.add( row );
            }
        }
        return dataClone;
    }

    public int indexOf( DynamicDataRow row ) {
        return data.indexOf( row );
    }

    /**
     * Return the state of merging
     * @return
     */
    public boolean isMerged() {
        return isMerged;
    }

    /**
     * Return the state of grouping
     * @return true if any rows are grouped
     */
    public boolean isGrouped() {
        for ( DynamicDataRow row : this.data ) {
            if ( row instanceof GroupedDynamicDataRow ) {
                return true;
            }
        }
        return false;
    }

    public Iterator<DynamicDataRow> iterator() {
        return data.iterator();
    }

    /**
     * Remove grouping by expanding applicable rows
     * @param startCell
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List<DynamicDataRow> removeModelGrouping( CellValue<?> startCell ) {

        int startRowIndex = startCell.getCoordinate().getRow();

        startCell.removeState( CellValue.CellState.GROUPED );

        //Check if rows need to be recursively expanded
        boolean bRecursive = true;
        DynamicDataRow row = data.get( startRowIndex );
        for ( int iCol = 0; iCol < row.size(); iCol++ ) {
            CellValue<?> cv = row.get( iCol );
            if ( cv instanceof CellValue.GroupedCellValue ) {
                bRecursive = !( bRecursive ^ ( (CellValue.GroupedCellValue) cv ).hasMultipleValues() );
            }
        }

        //Delete "grouped" row and replace with individual rows
        List<DynamicDataRow> expandedRow = expandGroupedRow( row,
                                                             bRecursive );
        deleteRow( startRowIndex );
        data.addAll( startRowIndex,
                     expandedRow );

        assertModelMerging();

        //If the row is replaced with another grouped row ensure the row can be expanded
        row = data.get( startRowIndex );
        boolean hasCellToExpand = false;
        for ( CellValue<?> cell : row ) {
            if ( cell instanceof CellValue.GroupedCellValue ) {
                if ( cell.isGrouped() && cell.getRowSpan() > 0 ) {
                    hasCellToExpand = true;
                    break;
                }
            }
        }
        if ( !hasCellToExpand ) {
            for ( CellValue<?> cell : row ) {
                if ( cell instanceof CellValue.GroupedCellValue && cell.getRowSpan() == 1 ) {
                    cell.addState( CellValue.CellState.GROUPED );
                }
            }
        }
        return expandedRow;
    }

    /**
     * Set the value at the specified coordinate
     * @param c
     * @param value
     */
    public void set( Coordinate c,
                     Object value ) {
        if ( c == null ) {
            throw new IllegalArgumentException( "c cannot be null" );
        }
        data.get( c.getRow() ).get( c.getCol() ).setValue( value );
        assertModelMerging();
    }

    /**
     * Set whether a columns is Visible
     * @param index index of column
     * @param isVisible True if the column is visible
     */
    public void setColumnVisibility( int index,
                                     boolean isVisible ) {
        this.visibleColumns.set( index,
                                 isVisible );
        assertModelIndexes();
    }

    /**
     * Set whether the grid's data is merged or not. Clearing merging within the
     * data also clears grouping
     * @param isMerged
     */
    public void setMerged( boolean isMerged ) {
        this.isMerged = isMerged;
        if ( isMerged ) {
            assertModelMerging();
        } else {
            removeModelGrouping();
            removeModelMerging();
        }
    }

    public int size() {
        return data.size();
    }

    public void sort( final List<SortConfiguration> sortConfig ) {

        if ( sortConfig.size() == 0 ) {

            //No sort configuration, restore original creation sequence
            Collections.sort( data,
                              new Comparator<DynamicDataRow>() {

                                  public int compare( DynamicDataRow leftRow,
                                                      DynamicDataRow rightRow ) {
                                      int comparison = 0;
                                      long li = leftRow.getCreationIndex();
                                      long ri = rightRow.getCreationIndex();
                                      if ( li < ri ) {
                                          comparison = -1;
                                      } else if ( li > ri ) {
                                          comparison = 1;
                                      }
                                      return comparison;
                                  }
                              } );

        } else {

            //Sort Configuration needs to be sorted itself by sortOrder first
            Collections.sort( sortConfig,
                              new Comparator<SortConfiguration>() {

                                  public int compare( SortConfiguration o1,
                                                      SortConfiguration o2 ) {
                                      Integer si1 = o1.getSortIndex();
                                      Integer si2 = o2.getSortIndex();
                                      return si1.compareTo( si2 );
                                  }

                              } );

            //Sort data
            Collections.sort( data,
                              new Comparator<DynamicDataRow>() {

                                  @SuppressWarnings({ "rawtypes", "unchecked" })
                                  public int compare( DynamicDataRow leftRow,
                                                      DynamicDataRow rightRow ) {
                                      int comparison = 0;
                                      for ( int index = 0; index < sortConfig.size(); index++ ) {
                                          SortConfiguration sc = sortConfig.get( index );
                                          Comparable leftColumnValue = leftRow.get( sc.getColumnIndex() );
                                          Comparable rightColumnValue = rightRow.get( sc.getColumnIndex() );
                                          comparison =
                                                  ( leftColumnValue == rightColumnValue ) ? 0
                                                          : ( leftColumnValue == null ) ? -1
                                                          : ( rightColumnValue == null ) ? 1
                                                          : leftColumnValue.compareTo( rightColumnValue );
                                          if ( comparison != 0 ) {
                                              switch ( sc.getSortDirection() ) {
                                                  case ASCENDING:
                                                      break;
                                                  case DESCENDING:
                                                      comparison = -comparison;
                                                      break;
                                                  default:
                                                      throw new IllegalStateException(
                                                              "Sorting can only be enabled for ASCENDING or"
                                                                      + " DESCENDING, not sortDirection ("
                                                                      + sc.getSortDirection()
                                                                      + ") ." );
                                              }
                                              return comparison;
                                          }
                                      }
                                      return comparison;
                                  }
                              } );
        }

        assertModelMerging();

    }

    // Here lays a can of worms! Each cell in the Decision Table has three
    // coordinates: (1) The physical coordinate, (2) The coordinate relating to
    // the HTML table element and (3) The coordinate mapping a HTML table
    // element back to the physical coordinate. For example a cell could have
    // the (1) physical coordinate (0,0) which equates to (2) HTML element (0,1)
    // in which case the cell at physical coordinate (0,1) would have a (3)
    // mapping back to (0,0).
    private void assertModelIndexes() {

        if ( data.size() == 0 ) {
            return;
        }

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );

            int colCount = 0;
            for ( int iCol = 0; iCol < row.size(); iCol++ ) {

                int newRow = iRow;
                int newCol = colCount;
                CellValue<? extends Comparable<?>> indexCell = row.get( iCol );
                indexCell.setCoordinate( new Coordinate( iRow,
                                                         iCol ) );

                // Don't index hidden columns; indexing is used to
                // map between HTML elements and the data behind
                if ( visibleColumns.get( iCol ) ) {

                    if ( indexCell.getRowSpan() != 0 ) {
                        newRow = iRow;
                        newCol = colCount++;

                        CellValue<? extends Comparable<?>> cell = data.get( newRow ).get( newCol );
                        cell.setPhysicalCoordinate( new Coordinate( iRow,
                                                                    iCol ) );

                    } else {
                        DynamicDataRow priorRow = data.get( iRow - 1 );
                        CellValue<? extends Comparable<?>> priorCell = priorRow.get( iCol );
                        Coordinate priorHtmlCoordinate = priorCell.getHtmlCoordinate();
                        newRow = priorHtmlCoordinate.getRow();
                        newCol = priorHtmlCoordinate.getCol();
                    }
                } else {
                    final int priorColIndex = ( iCol > 0 ? iCol - 1 : 0 );
                    CellValue<? extends Comparable<?>> priorCell = row.get( priorColIndex );
                    Coordinate priorHtmlCoordinate = priorCell.getHtmlCoordinate();
                    newRow = priorHtmlCoordinate.getRow();
                    newCol = priorHtmlCoordinate.getCol();
                }
                indexCell.setHtmlCoordinate( new Coordinate( newRow,
                                                             newCol ) );
            }
        }
    }

    /**
     * Ensure merging and indexing is reflected in the entire model. This should
     * be called whenever any changes are made to the underlying data externally
     * to the add/remove methods provided publicly herein, such as bulk move
     * operations.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void assertModelMerging() {

        if ( data.size() == 0 ) {
            return;
        }

        //Remove merging first as it initialises all coordinates
        removeModelMerging();

        final int COLUMNS = data.get( 0 ).size();

        //Only apply merging if merged
        if ( isMerged ) {

            int minRowIndex = 0;
            int maxRowIndex = data.size();

            //Add an empty row to the end of the data to simplify detection of merged cells that run to the end of the table
            DynamicDataRow blankRow = new DynamicDataRow();
            for ( int iCol = 0; iCol < COLUMNS; iCol++ ) {
                CellValue cv = new CellValue( null );
                Coordinate c = new Coordinate( data.size(),
                                               iCol );
                cv.setCoordinate( c );
                cv.setHtmlCoordinate( c );
                cv.setPhysicalCoordinate( c );
                blankRow.add( cv );
            }
            data.add( blankRow );
            maxRowIndex++;

            //Look in columns for cells with identical values
            for ( int iCol = 0; iCol < COLUMNS; iCol++ ) {
                CellValue<?> cell1 = data.get( minRowIndex ).get( iCol );
                CellValue<?> cell2 = null;
                for ( int iRow = minRowIndex + 1; iRow < maxRowIndex; iRow++ ) {
                    cell1.setRowSpan( 1 );
                    cell2 = data.get( iRow ).get( iCol );

                    //Merge if both cells contain the same value and neither is grouped
                    boolean bSplit = true;
                    if ( !cell1.isEmpty() && !cell2.isEmpty() ) {
                        if ( cell1.getValue().equals( cell2.getValue() ) ) {
                            bSplit = false;
                            if ( cell1 instanceof CellValue.GroupedCellValue ) {
                                bSplit = true;
                            }
                            if ( cell2 instanceof CellValue.GroupedCellValue ) {
                                bSplit = true;
                            }
                        }
                    } else if ( cell1.isOtherwise() && cell2.isOtherwise() ) {
                        bSplit = false;
                        if ( cell1 instanceof CellValue.GroupedCellValue ) {
                            CellValue.GroupedCellValue gcv = (CellValue.GroupedCellValue) cell1;
                            if ( gcv.hasMultipleValues() ) {
                                bSplit = true;
                            }
                        }
                        if ( cell2 instanceof CellValue.GroupedCellValue ) {
                            CellValue.GroupedCellValue gcv = (CellValue.GroupedCellValue) cell2;
                            if ( gcv.hasMultipleValues() ) {
                                bSplit = true;
                            }
                        }
                    }

                    if ( bSplit ) {
                        mergeCells( cell1,
                                    cell2 );
                        cell1 = cell2;
                    }

                }
            }

            //Remove dummy blank row
            data.remove( blankRow );

        }

        // Set indexes after merging has been corrected
        assertModelIndexes();

    }

    //Expand a grouped row and return a list of expanded rows
    private List<DynamicDataRow> expandGroupedRow( DynamicDataRow row,
                                                   boolean bRecursive ) {

        List<DynamicDataRow> ungroupedRows = new ArrayList<DynamicDataRow>();

        if ( row instanceof GroupedDynamicDataRow ) {

            GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;
            for ( int iChildRow = 0; iChildRow < groupedRow.getChildRows().size(); iChildRow++ ) {
                DynamicDataRow childRow = groupedRow.getChildRows().get( iChildRow );

                if ( bRecursive ) {
                    if ( childRow instanceof GroupedDynamicDataRow ) {
                        List<DynamicDataRow> expandedRow = expandGroupedRow( childRow,
                                                                             bRecursive );
                        ungroupedRows.addAll( expandedRow );
                    } else {
                        ungroupCells( childRow );
                        ungroupedRows.add( childRow );
                    }
                } else {
                    ungroupedRows.add( childRow );
                }
            }
        } else {
            ungroupCells( row );
            ungroupedRows.add( row );
        }

        return ungroupedRows;
    }

    //Find the bottom coordinate of a merged cell
    private Coordinate findMergedCellExtent( Coordinate c ) {
        if ( c.getRow() == data.size() - 1 ) {
            return c;
        }
        Coordinate nc = new Coordinate( c.getRow() + 1,
                                        c.getCol() );
        CellValue<?> newCell = get( nc );
        while ( newCell.getRowSpan() == 0 && nc.getRow() < data.size() - 1 ) {
            nc = new Coordinate( nc.getRow() + 1,
                                 nc.getCol() );
            newCell = get( nc );
        }
        if ( newCell.getRowSpan() != 0 ) {
            nc = new Coordinate( nc.getRow() - 1,
                                 nc.getCol() );
            newCell = get( nc );
        }
        return nc;
    }

    //Merge between the two provided cells
    private void mergeCells( CellValue<?> cell1,
                             CellValue<?> cell2 ) {
        int iStartRowIndex = cell1.getCoordinate().getRow();
        int iEndRowIndex = cell2.getCoordinate().getRow();
        int iColIndex = cell1.getCoordinate().getCol();

        //Any rows that are grouped need row span of zero
        for ( int iRow = iStartRowIndex; iRow < iEndRowIndex; iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            row.get( iColIndex ).setRowSpan( 0 );
        }
        cell1.setRowSpan( iEndRowIndex - iStartRowIndex );

    }

    //Initialise cell parameters when ungrouped
    private void ungroupCells( DynamicDataRow row ) {
        for ( int iCol = 0; iCol < row.size(); iCol++ ) {
            CellValue<?> cell = row.get( iCol );
            cell.removeState( CellValue.CellState.GROUPED );
        }
    }

    /**
     * Remove all grouping throughout the model
     */
    void removeModelGrouping() {

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            if ( row instanceof GroupedDynamicDataRow ) {
                List<DynamicDataRow> expandedRow = expandGroupedRow( row,
                                                                     true );
                deleteRow( iRow );
                data.addAll( iRow,
                             expandedRow );
                iRow = iRow + expandedRow.size() - 1;
            }
        }

    }

    /**
     * Remove merging from model
     */
    void removeModelMerging() {

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow row = data.get( iRow );

            for ( int iCol = 0; iCol < row.size(); iCol++ ) {
                CellValue<?> cell = row.get( iCol );
                Coordinate c = new Coordinate( iRow,
                                               iCol );
                cell.setCoordinate( c );
                cell.setHtmlCoordinate( c );
                cell.setPhysicalCoordinate( c );
                cell.setRowSpan( 1 );
            }
        }

        // Set indexes after merging has been corrected
        assertModelIndexes();
    }

}