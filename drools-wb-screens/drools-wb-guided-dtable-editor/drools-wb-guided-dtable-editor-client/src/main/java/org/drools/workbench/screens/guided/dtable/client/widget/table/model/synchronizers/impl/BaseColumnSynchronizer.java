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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.util.Arrays;
import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public abstract class BaseColumnSynchronizer<A extends BaseColumnSynchronizer.ColumnMetaData, U extends BaseColumnSynchronizer.ColumnMetaData, D extends BaseColumnSynchronizer.ColumnMetaData> extends BaseSynchronizer<A, U, D> {

    public interface ColumnMetaData<C extends BaseColumn> extends MetaData {

        C getColumn();

    }

    public static class ColumnMetaDataImpl implements ColumnMetaData<BaseColumn> {

        private final BaseColumn column;

        public ColumnMetaDataImpl( final BaseColumn column ) {
            this.column = checkNotNull( "column",
                                        column );
        }

        @Override
        public BaseColumn getColumn() {
            return column;
        }

    }

    @Override
    public boolean handlesInsert( final MetaData metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        return false;
    }

    @Override
    public void insert( final A metaData ) throws ModelSynchronizer.MoveColumnVetoException {
        //Do nothing. Not implemented for columns. All operations are handled by Append.
    }

    protected void synchroniseAppendColumn( final BaseColumn modelColumn ) {
        final int columnIndex = model.getExpandedColumns().indexOf( modelColumn );
        final GridColumn<?> uiModelColumn = gridWidgetColumnFactory.convertColumn( modelColumn,
                                                                                   access,
                                                                                   view );
        uiModel.insertColumn( columnIndex,
                              uiModelColumn );

        for ( int rowIndex = 0; rowIndex < model.getData().size(); rowIndex++ ) {
            final DTCellValue52 modelCell = makeModelCellValue( modelColumn,
                                                                cellUtilities,
                                                                columnUtilities );
            final List<DTCellValue52> modelRow = model.getData().get( rowIndex );
            modelRow.add( columnIndex,
                          modelCell );

            //BaseGridData is sparsely populated; only add values if needed.
            if ( modelCell.hasValue() ) {
                uiModel.setCellInternal( rowIndex,
                                         columnIndex,
                                         gridWidgetCellFactory.convertCell( modelCell,
                                                                            modelColumn,
                                                                            cellUtilities,
                                                                            columnUtilities ) );
            }
        }

        uiModel.indexColumn( columnIndex );
    }

    protected void synchroniseAppendColumn( final BaseColumn modelColumn,
                                            final List<DTCellValue52> originalColumnData ) {
        final int columnIndex = model.getExpandedColumns().indexOf( modelColumn );
        final GridColumn<?> uiModelColumn = gridWidgetColumnFactory.convertColumn( modelColumn,
                                                                                   access,
                                                                                   view );
        uiModel.insertColumn( columnIndex,
                              uiModelColumn );

        for ( int rowIndex = 0; rowIndex < model.getData().size(); rowIndex++ ) {
            final DTCellValue52 modelCell = originalColumnData.get( rowIndex );
            final List<DTCellValue52> modelRow = model.getData().get( rowIndex );
            modelRow.add( columnIndex,
                          modelCell );

            if ( modelCell.hasValue() ) {
                uiModel.setCellInternal( rowIndex,
                                         columnIndex,
                                         gridWidgetCellFactory.convertCell( modelCell,
                                                                            modelColumn,
                                                                            cellUtilities,
                                                                            columnUtilities ) );
            }
        }

        uiModel.indexColumn( columnIndex );
    }

    protected void synchroniseUpdateColumn( final BaseColumn modelColumn ) {
        final int columnIndex = model.getExpandedColumns().indexOf( modelColumn );
        final GridColumn<?> uiModelColumn = gridWidgetColumnFactory.convertColumn( modelColumn,
                                                                                   access,
                                                                                   view );
        uiModel.updateColumn( columnIndex,
                              uiModelColumn );

        for ( int rowIndex = 0; rowIndex < model.getData().size(); rowIndex++ ) {
            final List<DTCellValue52> modelRow = model.getData().get( rowIndex );
            final DTCellValue52 modelCell = modelRow.get( columnIndex );

            //BaseGridData is sparsely populated; only add values if needed.
            if ( modelCell.hasValue() ) {
                uiModel.setCellInternal( rowIndex,
                                         columnIndex,
                                         gridWidgetCellFactory.convertCell( modelCell,
                                                                            modelColumn,
                                                                            cellUtilities,
                                                                            columnUtilities ) );
            }
        }

        uiModel.indexColumn( columnIndex );
    }

    protected void synchroniseDeleteColumn( final int columnIndex ) {
        for ( int rowIndex = 0; rowIndex < model.getData().size(); rowIndex++ ) {
            final List<DTCellValue52> modelRow = model.getData().get( rowIndex );
            modelRow.remove( columnIndex );
        }

        final GridColumn<?> uiModelColumn = uiModel.getColumns().get( columnIndex );
        uiModel.deleteColumn( uiModelColumn );
    }

    protected DTCellValue52 makeModelCellValue( final BaseColumn modelColumn,
                                                final CellUtilities cellUtilities,
                                                final ColumnUtilities columnUtilities ) {
        DTCellValue52 dcv;
        if ( modelColumn instanceof LimitedEntryCol ) {
            dcv = new DTCellValue52( Boolean.FALSE );
        } else {
            dcv = new DTCellValue52( modelColumn.getDefaultValue() );
        }
        final DataType.DataTypes dataType = columnUtilities.getDataType( modelColumn );
        cellUtilities.convertDTCellValueType( dataType,
                                              dcv );
        return dcv;
    }

    // Update a Column's visibility
    protected void setColumnVisibility( final BaseColumn modelColumn,
                                        final boolean isHidden ) {
        modelColumn.setHideColumn( isHidden );
        final int iModelColumn = model.getExpandedColumns().indexOf( modelColumn );
        uiModel.getColumns().get( iModelColumn ).setVisible( !isHidden );
    }

    // Update a Column's header
    //TODO {manstis} Move this to the Synchronizers to support HeaderMetaData
    protected void setColumnHeader( final BaseColumn modelColumn,
                                    final String header ) {
        modelColumn.setHeader( header );
        final int iModelColumn = model.getExpandedColumns().indexOf( modelColumn );
        uiModel.getColumns().get( iModelColumn ).getHeaderMetaData().get( 0 ).setTitle( header );
    }

    // Clear the values in a column
    protected void clearColumnData( final BaseColumn column ) {
        final int columnIndex = this.model.getExpandedColumns().indexOf( column );
        for ( List<DTCellValue52> row : this.model.getData() ) {
            final DTCellValue52 dcv = row.get( columnIndex );
            dcv.clearValues();
        }
    }

    // Ensure the values in a column are within the Value List
    protected void updateCellsForOptionValueList( final BaseColumn originalColumn,
                                                  final BaseColumn editedColumn ) {
        //If the new column definition has no Value List the existing values remain valid
        final List<String> values = Arrays.asList( columnUtilities.getValueList( editedColumn ) );
        final boolean clearExistingValues = values.size() > 0;

        final int columnIndex = model.getExpandedColumns().indexOf( originalColumn );
        for ( List<DTCellValue52> row : this.model.getData() ) {
            final DTCellValue52 dcv = row.get( columnIndex );
            if ( clearExistingValues && !values.contains( dcv.getStringValue() ) ) {
                row.remove( columnIndex );
            }
        }
    }

}
